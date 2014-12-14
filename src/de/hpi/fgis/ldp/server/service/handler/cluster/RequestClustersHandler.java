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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.util.job.JobManager;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterChildrenRequestResult;

public class RequestClustersHandler implements
    ActionHandler<ClusterChildrenRequest, ClusterChildrenRequestResult> {

  @Inject
  private Log logger;
  @Inject
  private IClusterLoader clusterLoader;
  @Inject
  private ISchemaLoader schemaLoader;
  @Inject
  private JobNameSource jobNameSource;
  @Inject
  private JobManager manager;
  @Inject
  private Provider<DebugProgress> debugProcess;

  // @Inject
  // public RequestClustersHandler(final Log logger, final IClusterLoader
  // loader) {
  // this.logger = logger;
  // this.loader = loader;
  // }

  @Override
  public ClusterChildrenRequestResult execute(final ClusterChildrenRequest action,
      final ExecutionContext context) throws RPCException {
    // int sessionID = action.getSessionID();
    // Cluster parent = new Cluster(new DataSource(Constants.db2Schema));
    // parent.setChildSessionID(sessionID);
    // Cluster parent = action.getCluster();
    // if(parent==null) {
    // // TODO load all data sources from db instead
    // parent = new Cluster(new DataSource(Constants.db2Schema));
    // parent.setChildSessionID(Constants.ROOT_CLUSTER_SESSION_ID);
    // }

    Cluster parent = action.getCluster();

    logger.debug("Managing cluster children request for cluster " + parent);

    try {

      ArrayList<Cluster> childClusters = new ArrayList<Cluster>();

      if (parent == null) {
        // get root clusters
        // TODO inject progress instance
        final List<Cluster> clusters = this.getChildren(debugProcess.get());
        childClusters.addAll(clusters);
      } else if (parent.getChildSessionID() >= 0) {

        // get clusters
        // TODO inject progress instance
        final List<Cluster> clusters = this.getChildren(parent, debugProcess.get());
        childClusters.addAll(clusters);
      }

      // look for cluster in "InProgress" state
      for (final Cluster currentCluster : childClusters) {
        final String currentJobName = jobNameSource.getJobName(currentCluster);
        if (manager.isInProgress(currentJobName)) {
          currentCluster.setProgressIdentifier(Long.valueOf(manager
              .getProgressIdentifier(currentJobName)));
        }
      }
      // ignore child sessions with id < 0
      // final List<Cluster> clusters = new ArrayList<Cluster>();
      // final Cluster cluster = new Cluster();
      // childClusters.setLabel("bla blub");
      // childClusters.setId(1);
      // childClusters.add(cluster);

      return new ClusterChildrenRequestResult(childClusters);
    } catch (Exception cause) {
      logger.error(
          "Unable to get clusters for session "
              + ((parent == null) ? "???" : Integer.toString(parent.getChildSessionID())), cause);

      throw new RPCException(cause);
    }
  }

  public List<Cluster> getChildren(IProgress progress) throws SQLException {
    return this.schemaLoader.getRootClusters(progress);
  }

  public List<Cluster> getChildren(final Cluster parent, IProgress progress) throws SQLException {
    return this.clusterLoader.getClusters(parent, progress);
  }

  @Override
  public void rollback(final ClusterChildrenRequest action,
      final ClusterChildrenRequestResult result, final ExecutionContext context)
      throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<ClusterChildrenRequest> getActionType() {
    return ClusterChildrenRequest.class;
  }

}
