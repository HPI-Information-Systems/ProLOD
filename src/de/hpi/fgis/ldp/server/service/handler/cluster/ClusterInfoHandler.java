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

package de.hpi.fgis.ldp.server.service.handler.cluster;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.DataTableResult;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterInfoRequest;

public class ClusterInfoHandler implements ActionHandler<ClusterInfoRequest, DataTableResult> {

  @Inject
  private Log logger;
  @Inject
  private IClusterLoader clusterLoader;
  @Inject
  private @Named("db.defaultUserView") String defaultUserView;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public DataTableResult execute(final ClusterInfoRequest action, final ExecutionContext context)
      throws RPCException {
    Cluster cluster = action.getCluster();

    if (!DataSource.DEFAULT_USER_VIEW.equals(defaultUserView)) {
      logger.fatal("Default user view name differs (" + DataSource.DEFAULT_USER_VIEW + " and "
          + defaultUserView + ")!");
      throw new IllegalStateException("Default user view name differs ("
          + DataSource.DEFAULT_USER_VIEW + " and " + defaultUserView + ")!");
    }

    logger.debug("Managing cluster info request for cluster " + cluster.getId() + " ("
        + cluster.getLabel() + ")");

    try {
      if (cluster.getId() >= 0) {
        // TODO inject progress
        return new DataTableResult(this.clusterLoader.getMetaInformation(cluster,
            debugProcess.get()));
      } else {
        DataColumn<String> key = new DataColumn<String>("Key", true);
        DataColumn<String> value = new DataColumn<String>("Value", true);

        key.setElement(0, "Label");
        value.setElement(0, cluster.getLabel());
        key.setElement(1, "User view");
        final String userView = cluster.getDataSource().getUserView();
        if (userView == null) {
          value.setElement(1, "[" + this.defaultUserView + "]");
        } else {
          value.setElement(1, userView);
        }
        key.setElement(2, "Entities");
        value.setElement(2, String.valueOf(cluster.getSize()));
        key.setElement(3, "RDF triples");
        value.setElement(3, String.valueOf(cluster.getTripleCount()));

        key.setElement(4, "internal Schema");
        value.setElement(4, cluster.getDataSource().getLabel());

        DataTable infos = new DataTable(key);
        infos.addColumn(value);

        return new DataTableResult(infos);
      }
    } catch (Exception cause) {
      logger.error("Unable to get cluster information for " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final ClusterInfoRequest action, final DataTableResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<ClusterInfoRequest> getActionType() {
    return ClusterInfoRequest.class;
  }

}
