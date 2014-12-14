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

import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.cluster.RenameClusterRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.RenameClusterResult;

public class ClusterRenameHandler implements
    ActionHandler<RenameClusterRequest, RenameClusterResult> {

  @Inject
  private Log logger;
  @Inject
  private IClusterStorage clusterStorage;

  // @Inject
  // private ISchemaStorage schemaStorage;

  @Override
  public RenameClusterResult execute(final RenameClusterRequest action,
      final ExecutionContext context) throws RPCException {
    Cluster cluster = action.getCluster();

    logger.debug("Managing cluster rename request for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {
      Cluster result = null;
      if (cluster.getId() >= 0) {
        result = this.clusterStorage.renameCluster(cluster, cluster.getLabel());
      } else {
        // TODO implement (and change prolod_main.schemata)
        // // TODO inject
        // result =
        // this.schemaStorage.relabelSchema(cluster.getDataSource(),
        // cluster.getLabel(), CMDProgress.getInstance());
      }

      return new RenameClusterResult(result);
    } catch (Exception cause) {
      logger.error("Unable to rename cluster " + cluster.getId(), cause);

      throw new RPCException(cause);
    }
  }

  @Override
  public void rollback(final RenameClusterRequest action, final RenameClusterResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<RenameClusterRequest> getActionType() {
    return RenameClusterRequest.class;
  }

}
