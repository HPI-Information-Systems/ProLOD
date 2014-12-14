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

package de.hpi.fgis.ldp.server.algorithms.clustering.kmeans;

import java.util.List;

import de.hpi.fgis.ldp.server.algorithms.clustering.EntityClusterFactory;
import de.hpi.fgis.ldp.server.algorithms.clustering.EntityExtractor;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * The Recursive K-Means implementation combines standard K-Means and iterative K-Means to create a
 * hierarchical clustering.
 * 
 * @author hefenbrock
 * 
 */
public class RecursiveKMeans {

  // we only need it to extract entities
  EntityExtractor extractor = EntityExtractor.getInstance();

  /**
   * Create a hierarchical clustering.
   * 
   * @param rootSession The root session of the hierarchical clustering.
   * @param entities The entities to cluster.
   * @param comparator The entity schema comparator to be used.
   * @param abortOnError Divide clusters into subclusters if error is >= abortOnError
   * @param abortOnSize Divide clusters into subclusters if size is >= abortOnError
   * @param numClusters Provide the number of clusters for each depth, beginning with depth 0. This
   *        also defines the maximum possible depth.
   */
  public void clusterRecursively(Session rootSession, List<IEntity> entities,
      ISchemaComparator comparator, double abortOnError, int abortOnSize, final IProgress progress,
      int... numClusters) {

    this.cluster(rootSession, null, entities, comparator, abortOnError, abortOnSize, 0,
        numClusters, true, progress);

    progress.stopProgress();

  }

  private void cluster(Session session, EntityCluster parentClusterEntities,
      List<IEntity> entities, ISchemaComparator comparator, double abortOnError, int abortOnSize,
      int depth, int[] numClusters, boolean skipIterative, final IProgress progress) {

    if (null == session) {
      session =
          new Session(parentClusterEntities.getMetaData().getDataSource(), "cl"
              + parentClusterEntities.getMetaData().getIndex());
    }
    if (null != parentClusterEntities) {
      parentClusterEntities.setChildSession(session);
    }

    progress.startProgress("starting recursive cluster run", 100);

    IterativeKMeans ikmeans = new IterativeKMeans(session.getDataSource());
    EntityClusterFactory clusterRun = null;
    double iterativeError = -1;
    if (!skipIterative) {
      ikmeans.setEntities(entities);
      ikmeans.setAbortOnMaxError(abortOnError);
      ikmeans.setNumClusters(numClusters[depth]);
      ikmeans.setNumSeedCandidates(10);
      ikmeans.setEntityComparator(comparator);

      clusterRun = ikmeans.cluster();
      iterativeError = comparator.computeAvgError(clusterRun.getClusters());
    }

    if (skipIterative || IterativeKMeans.AbortReason.maxK == ikmeans.getAbortReason()) {
      KMeans kmeans = new KMeans(session.getDataSource());
      kmeans.setEntities(entities);
      kmeans.setNumClusters(numClusters[depth]);
      kmeans.setNumSeedCandidates(10);
      kmeans.setEntityComparator(comparator);

      EntityClusterFactory clusterReRun = kmeans.cluster();
      if (skipIterative || comparator.computeAvgError(clusterReRun.getClusters()) > iterativeError) {
        clusterRun = clusterReRun;
      }
    }

    if (clusterRun != null) {
      session.setEntityClusters(clusterRun.getEntityClusters(progress.continueWithSubProgress(50)));
    }

    if (depth + 1 == numClusters.length
    /* || IterativeKMeans.AbortReason.error == ikmeans.getAbortReason() */) {
      progress.stopProgress();
      return;
    }

    IProgress subprogress = progress.continueWithSubProgress(50);
    subprogress.startProgress("creating subclusters", session.getEntityClusters().size() * 10);
    int currentPosition = 0;
    for (EntityCluster clusterDetail : session.getEntityClusters()) {
      // TODO start progress for each subcluster
      if (clusterDetail.getMetaData().getError() > abortOnError
          && clusterDetail.entityIds().size() > abortOnSize) {
        List<IEntity> extractedEntities =
            this.extractor.extract(clusterDetail.entityIds(), entities);
        this.cluster(null, clusterDetail, extractedEntities, comparator, abortOnError, abortOnSize,
            depth + 1, numClusters, false, subprogress.continueWithSubProgress(10));
      }
      subprogress.continueProgressAt((++currentPosition) * 10);
    }
    subprogress.stopProgress();
    progress.stopProgress();
  }

}
