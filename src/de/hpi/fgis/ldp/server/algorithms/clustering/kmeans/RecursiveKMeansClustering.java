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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.clustering.datastructures.InMemoryEntityList;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.TopNMeans;
import de.hpi.fgis.ldp.server.algorithms.labeling.LabelingPreprocessor;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.config.clustering.RecursiveKMeansConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * this class provides a cluster algorithm using KMeans
 * 
 * @author toni.gruetze
 * 
 */
public class RecursiveKMeansClustering extends KMeansClustering {

  @Inject
  protected RecursiveKMeansClustering(Log logger, IClusterStorage clusterStorage,
      IEntitySchemaLoader loader, LabelingPreprocessor labelingPreprocessor) {
    super(logger, clusterStorage, loader, labelingPreprocessor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm#cluster
   * (de.hpi.fgis.ldp.shared.data.cluster.ICluster, java.lang.Object,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public Session cluster(Cluster parent, IClusterConfig clusterConfig, IProgress progress)
      throws Exception {
    if (clusterConfig instanceof RecursiveKMeansConfig) {
      return this.startRecursiveKMeans(parent, (RecursiveKMeansConfig) clusterConfig, progress);
    }
    throw new IllegalArgumentException("Unable to start KMeans without valid configuration!");
  }

  private Session startRecursiveKMeans(Cluster parent, RecursiveKMeansConfig recusriveKMeansConfig,
      IProgress progress) throws Exception {
    final int[] clusterSize = recusriveKMeansConfig.getNumberOfClusters();
    int maxNumOfClusters = 1;

    for (int amount : clusterSize) {
      maxNumOfClusters *= amount;
    }
    progress.startProgress("Starting recursive KMeans cluster run...", 50 + 2 * maxNumOfClusters);

    try {
      // loading entities
      List<IEntity> entities = this.getEntities(parent, progress.continueWithSubProgress(50));

      // calculating subclusters
      Session clusterTree =
          this.clusterRecursively(parent.getDataSource(), entities, recusriveKMeansConfig,
              progress.continueWithSubProgress(maxNumOfClusters));

      EntityCluster rootCluster = new EntityCluster(parent, null, null);
      rootCluster.setChildSession(clusterTree);

      IProgress rootClusterProgress = progress.continueWithSubProgress(maxNumOfClusters);
      rootClusterProgress.startProgress("storing root cluster", 100);
      return this.storeEntityCluster(rootCluster, rootClusterProgress);
    } finally {
      progress.stopProgress();
    }
  }

  // this method needs a progress instance with 100 steps
  private Session storeEntityCluster(EntityCluster cluster, IProgress progress) throws SQLException {
    // storing clusters
    Session session =
        this.store(cluster.getMetaData(), cluster.getChildSession().getEntityClusters(),
            progress.continueWithSubProgress(40));

    // labeling new subclusters
    this.label(session, progress.continueWithSubProgress(10));

    IProgress subprogress = progress.continueWithSubProgress(50);
    subprogress.startProgress("storing subclusters", session.getEntityClusters().size() * 10);

    for (EntityCluster clusterDetail : session.getEntityClusters()) {
      IProgress newProgress = subprogress.continueWithSubProgress(10);
      newProgress.startProgress("storing subcluster", 100);

      if (clusterDetail.getChildSession() != null
          && clusterDetail.getChildSession().getEntityClusters() != null) {
        this.storeEntityCluster(clusterDetail, newProgress);
      }
      newProgress.stopProgress();
    }

    return session;
  }

  private List<IEntity> getEntities(final Cluster parent, final IProgress progress)
      throws SQLException {
    try {
      IEntitySchemaList entityList = null;
      if (parent.getId() >= 0) {
        entityList = loader.getEntityList(parent, "sp", progress);
      } else {
        entityList = loader.getEntityList(parent.getDataSource(), "sp", 10, 0.025, progress);
      }

      // handle entityList==null;
      if (entityList == null) {
        return new ArrayList<IEntity>(0);
      }
      return new InMemoryEntityList(entityList);
    } catch (SQLException e) {
      logger.error("Unable to load cluster entities!", e);
      throw e;
    }
  }

  private Session clusterRecursively(DataSource source, List<IEntity> entities,
      RecursiveKMeansConfig recusriveKMeansConfig, IProgress subProgress) {
    logger.debug("creating RecursiveKMeans factory...");

    ISchemaComparator comparator = new TopNMeans();

    Session session = new Session(source, "new initial_session");

    RecursiveKMeans rkmeans = new RecursiveKMeans();
    rkmeans.clusterRecursively(session, entities, comparator,
        recusriveKMeansConfig.getAbortOnError(), recusriveKMeansConfig.getAbortOnSize(),
        subProgress, recusriveKMeansConfig.getNumberOfClusters());

    return session;
  }

  private Session store(Cluster parent, List<EntityCluster> newClusters, IProgress progress)
      throws SQLException {
    // add cluster to new session
    Session session =
        new Session(parent.getDataSource(), "subclusters of cluster " + parent.getId() + "("
            + newClusters.size() + ")");

    session.setEntityClusters(newClusters);

    progress.startProgress("Storing clusters...", newClusters.size());
    try {
      this.clusterStorage.storeSession(parent, session, progress.continueWithSubProgress(1));

      this.clusterStorage.setChildSession(parent, session,
          progress.continueWithSubProgress(newClusters.size()));
      return session;
    } catch (SQLException e) {
      logger.error("Unable to store clusters!", e);
      throw e;
    } finally {
      progress.stopProgress();
    }
  }
}
