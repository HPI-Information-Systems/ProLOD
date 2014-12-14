/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.service.handler.schema;

import java.sql.SQLException;
import java.util.ArrayList;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.schema.ChangeUserViewRequest;
import de.hpi.fgis.ldp.shared.rpc.schema.ChangeUserViewResult;

public class ChangeUserViewHandler implements
    ActionHandler<ChangeUserViewRequest, ChangeUserViewResult> {

  @Inject
  private Log logger;
  @Inject
  private ISchemaStorage schemaStorage;
  @Inject
  private ISchemaLoader schemaLoader;
  @Inject
  private IClusterStorage clusterStorage;
  @Inject
  private IClusterLoader clusterLoader;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public ChangeUserViewResult execute(final ChangeUserViewRequest action,
      final ExecutionContext context) throws RPCException {
    DataSource source = action.getSchema();
    DataSource target = source.asUserView(action.getTargetView());

    try {
      // load the root cluster (and hope it is available)
      // TODO inject
      Cluster resultCluster = schemaLoader.getRootCluster(target, debugProcess.get());

      // create view from source, if not available
      if (resultCluster == null) {
        if (!action.copyIfNotExist()) {
          throw new RPCException("Expected user view \"" + target.getUserView() + "\" not found!");
        }
        // TODO inject
        IProgress progress = debugProcess.get();
        progress.startProgress("Copying clustering...", 4);

        clusterStorage.copyClustering(source, target.getUserView(),
            progress.continueWithSubProgress(1));

        final Cluster sourceRootCluster =
            schemaLoader.getRootCluster(source, progress.continueWithSubProgress(1));
        Session targetRootSession = new Session(target, sourceRootCluster.getChildSessionID());

        schemaStorage.publishRootSession(targetRootSession, progress.continueWithSubProgress(1));

        // load the new created root cluster
        resultCluster =
            schemaLoader.getRootCluster(targetRootSession.getDataSource(),
                progress.continueWithSubProgress(1));

        progress.stopProgress();
      }
      // TODO inject
      ArrayList<Cluster> children =
          new ArrayList<Cluster>(clusterLoader.getClusters(resultCluster, debugProcess.get()));

      return new ChangeUserViewResult(resultCluster, children);
    } catch (SQLException cause) {
      logger.error("Unable to change user view to \"" + target.getUserView()
          + "\" for schema named \"" + target.getLabel() + "\"!", cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final ChangeUserViewRequest action, final ChangeUserViewResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<ChangeUserViewRequest> getActionType() {
    return ChangeUserViewRequest.class;
  }

}
