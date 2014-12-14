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

import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.clustering.datastructures.InMemoryEntityList;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.TopNMeans;
import de.hpi.fgis.ldp.server.algorithms.labeling.LabelingPreprocessor;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.config.clustering.KMeansConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * this class provides a cluster algorithm using KMeans
 * 
 * @author toni.gruetze
 * 
 */
public class KMeansClustering implements IClusterAlgorithm {

  protected final Log logger;
  protected final IClusterStorage clusterStorage;
  protected final IEntitySchemaLoader loader;
  protected final LabelingPreprocessor labelingPreprocessor;

  @Inject
  protected KMeansClustering(Log logger, IClusterStorage clusterStorage,
      IEntitySchemaLoader loader, LabelingPreprocessor labelingPreprocessor) {
    this.logger = logger;
    this.clusterStorage = clusterStorage;
    this.loader = loader;
    this.labelingPreprocessor = labelingPreprocessor;
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
    if (clusterConfig instanceof KMeansConfig) {
      return this.startKMeans(parent, (KMeansConfig) clusterConfig, progress);
    }
    throw new IllegalArgumentException("Unable to start KMeans without valid configuration!");
  }

  private Session startKMeans(Cluster parent, KMeansConfig kMeansConfig, IProgress progress)
      throws Exception {

    progress.startProgress("Starting KMeans cluster run...", 90);
    try {
      // loading entities
      List<IEntity> entities = this.getEntities(parent, progress.continueWithSubProgress(30));
      progress.continueProgressAt(30);

      // calculating subclusters
      List<EntityCluster> subClusters =
          this.cluster(parent.getDataSource(), entities, kMeansConfig.getNumberOfClusters(),
              progress.continueWithSubProgress(20));
      progress.continueProgressAt(50);

      // storing new subclusters
      Session session = this.store(parent, subClusters, progress.continueWithSubProgress(10));
      progress.continueProgressAt(60);

      // labeling new subclusters
      this.label(session, progress.continueWithSubProgress(30));

      return session;
    } finally {
      progress.stopProgress();
    }
  }

  private List<IEntity> getEntities(final Cluster parent, IProgress progress) throws SQLException {
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

  private List<EntityCluster> cluster(DataSource source, List<IEntity> entities,
      final int numOfClusters, IProgress progress) {
    logger.debug("creating KMeans factory...");

    ISchemaComparator comparator = new TopNMeans();

    // TODO integrate progress
    KMeans clusterer = new KMeans(source);
    clusterer.setEntities(entities);
    clusterer.setEntityComparator(comparator);
    clusterer.setNumClusters(numOfClusters);
    clusterer.setNumSeedCandidates(10);

    // TODO check input params
    IClusterFactory factory = clusterer.cluster();

    return factory.getEntityClusters(progress);
  }

  private Session store(Cluster parent, List<EntityCluster> newClusters, IProgress progress)
      throws SQLException {
    // add cluster to new session
    Session session =
        new Session(parent.getDataSource(), "subclusters of cluster " + parent.getId() + "("
            + newClusters.size() + ")");

    session.setEntityClusters(newClusters);

    progress.startProgress("Storing clusters...", newClusters.size() + 1);
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

  protected void label(Session session, IProgress progress) {
    int sessionId = session.getId();
    if (sessionId != -1) {
      Cluster cluster = new Cluster(session.getDataSource());
      cluster.setChildSessionID(sessionId);
      this.labelingPreprocessor.computeClusterLabel(cluster, progress);
    }
  }
}
