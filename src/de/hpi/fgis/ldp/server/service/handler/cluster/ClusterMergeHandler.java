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

import de.hpi.fgis.ldp.server.algorithms.labeling.LabelingPreprocessor;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.exception.RPCException;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterMergeRequest;
import de.hpi.fgis.ldp.shared.rpc.cluster.ClusterMergeResult;

public class ClusterMergeHandler implements ActionHandler<ClusterMergeRequest, ClusterMergeResult> {

  @Inject
  private Log logger;
  @Inject
  private IClusterStorage clusterStorage;
  @Inject
  private ISchemaStorage schemaStorage;
  @Inject
  private IClusterLoader clusterLoader;
  @Inject
  private RequestClustersHandler childLoader;
  @Inject
  private LabelingPreprocessor labelingPreprocessor;
  // @Inject
  // private ISchemaStorage schemaStorage;
  @Inject
  private Provider<DebugProgress> debugProcess;

  @Override
  public ClusterMergeResult execute(final ClusterMergeRequest action, final ExecutionContext context)
      throws RPCException {

    Cluster parent = action.getParent();

    ArrayList<Cluster> clusters = action.getClusters();

    logger.info("Cluster merging called for cluster " + parent.getId() + ". Merging "
        + clusters.size() + " clusters.");

    IProgress progress = debugProcess.get();

    progress.startProgress("merging clusters", 10);

    Session session = new Session(parent.getDataSource(), "cluster_merge_session");
    session.setId(parent.getChildSessionID());
    // load session
    session.setEntityClusters(clusterLoader.loadSession(parent).getEntityClusters(
        debugProcess.get()));

    Cluster from = clusters.get(0);
    Cluster to = clusters.get(1);
    // FIXME implement for more than 2 merge clusters
    EntityCluster fromCluster = null, toCluster = null;
    for (int index = 0; index < session.getEntityClusters().size(); index++) {
      EntityCluster entityCluster = session.getEntityClusters().get(index);
      if (entityCluster.getMetaData().getId() == to.getId()) {
        toCluster = entityCluster;
        session.getEntityClusters().remove(index--);
      } else if (entityCluster.getMetaData().getId() == from.getId()) {
        fromCluster = entityCluster;
      }
      session.getEntityClusters().get(index).getMetaData().setIndex(index);
    }

    if (toCluster == null || fromCluster == null) {
      throw new RPCException("Unable to merge (unknown clusters)!");
    }

    toCluster.join(fromCluster);
    session.getEntityClusters().remove(fromCluster);
    session.getEntityClusters().add(toCluster);

    // store results to database
    try {
      clusterStorage.storeSession(parent, session, progress.continueWithSubProgress(1));

      if (parent.getId() < 0) {
        // update root session
        schemaStorage.publishRootSession(session, progress.continueWithSubProgress(1));
      } else {
        // update parent cluster
        clusterStorage.setChildSession(parent, session, progress.continueWithSubProgress(1));
      }
      parent.setChildSessionID(session.getId());

      logger.info("Labeling new created cluster");
    } catch (SQLException e) {
      logger.warn("Unable to merge clusters!", e);
      throw new RPCException("Unable to merge (unknown clusters)!");
    }

    this.labelingPreprocessor.computeClusterLabel(parent, progress.continueWithSubProgress(7));

    try {
      List<Cluster> children =
          this.childLoader.getChildren(parent, progress.continueWithSubProgress(1));
      progress.stopProgress();
      logger.error("merged to: " + children);
      return new ClusterMergeResult(parent, new ArrayList<Cluster>(children));
    } catch (SQLException e) {
      throw new RPCException(e);
    }

  }

  @Override
  public void rollback(final ClusterMergeRequest action, final ClusterMergeResult result,
      final ExecutionContext context) throws RPCException {
    // Nothing to do here
  }

  @Override
  public Class<ClusterMergeRequest> getActionType() {
    return ClusterMergeRequest.class;
  }

}
