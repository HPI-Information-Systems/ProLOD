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
 * For Questions please contact:
 *  * Anja Jentzsch <anja.jentzsch@hpi.uni-potsdam.de>,
 *  * Christoph Böhm <christoph.boehm@hpi.uni-potsdam.de>, or
 *  * Felix Naumann <felix.naumann@hpi.uni-potsdam.de>
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.algorithms.clustering.ontology;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.clustering.datastructures.InMemoryEntityList;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.config.clustering.OntologyConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * This class provides a cluster algorithm using an ontology
 * 
 * @author anja.jentzsch
 * 
 */
public class OntologyClustering implements IClusterAlgorithm {

  protected final Log logger;
  protected final IClusterStorage clusterStorage;
  protected final IEntitySchemaLoader loader;
  private String clusterName;
  private Map<String, String> classNameHierarchy;

  @Inject
  protected OntologyClustering(Log logger, IClusterStorage clusterStorage,
      IEntitySchemaLoader loader) {
    this.logger = logger;
    this.clusterStorage = clusterStorage;
    this.loader = loader;
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
    if (clusterConfig instanceof OntologyConfig) {
      return this.startOntology(parent, (OntologyConfig) clusterConfig, progress);
    }
    throw new IllegalArgumentException("Unable to start without valid configuration!");
  }

  private Session startOntology(Cluster parent, OntologyConfig ontologyConfig, IProgress progress)
      throws Exception {

    progress.startProgress("Starting ontology cluster run...", 90);
    try {
      // loading entities
      List<IEntity> entities = this.getEntities(parent, progress.continueWithSubProgress(30));
      progress.continueProgressAt(30);

      // calculating subclusters
      this.clusterName = parent.getLabel();
      if (this.clusterName == null) {
        this.clusterName = "http://www.w3.org/2002/07/owl#Thing";
      }
      List<EntityCluster> subClusters =
          this.cluster(this.loader, parent.getDataSource(), entities,
              ontologyConfig.getNumberOfClusters(), progress.continueWithSubProgress(20));
      progress.continueProgressAt(50);

      // storing new subclusters
      Map<String, List<EntityCluster>> subEntityClusters =
          new HashMap<String, List<EntityCluster>>();
      Map<String, EntityCluster> entityClusterMap = new HashMap<String, EntityCluster>();

      List<EntityCluster> subClusterList = new ArrayList<EntityCluster>();
      for (EntityCluster subCluster : subClusters) {
        entityClusterMap.put(subCluster.getClusterName(), subCluster);
        if (this.clusterName.equals(this.classNameHierarchy.get(subCluster.getClusterName()))) {
          subClusterList.add(subCluster);
        } else {
          String parentClusterName = this.classNameHierarchy.get(subCluster.getClusterName());
          if (parentClusterName != null) {
            List<EntityCluster> tempEntityClusterList = subEntityClusters.get(parentClusterName);
            if (tempEntityClusterList == null) {
              tempEntityClusterList = new ArrayList<EntityCluster>();
            }
            tempEntityClusterList.add(subCluster);
            subEntityClusters.put(parentClusterName, tempEntityClusterList);
          }
        }
        /*
         * if ("http://dbpedia.org/ontology/Person".equals(this.
         * classNameHierarchy.get(subCluster.getClusterName()))) {
         * personsubClusterList.add(subCluster);
         * 
         * EntityCluster parentCluster = null; // person cluster if (parentCluster.getChildSession()
         * == null) { Session childSession = new
         * Session(parentCluster.getMetaData().getDataSource(), "subclusters of cluster " +
         * parentCluster.getClusterName() + "()"); parentCluster.setChildSession(childSession); }
         * parentCluster .getChildSession().setEntityClusters(clusterDetails);
         * 
         * 
         * }
         */
      }

      Session session = this.store(parent, subClusterList, progress.continueWithSubProgress(10));
      progress.continueProgressAt(60);

      /*
       * for (String subClusterName : getKeyByValue(this.classNameHierarchy, this.clusterName)) {
       * //List<EntityCluster> childClusters = subEntityClusters.get(this.classNameHierarchy
       * .get(subClusterName)); List<EntityCluster> childClusters =
       * subEntityClusters.get(subClusterName); EntityCluster parentCluster =
       * entityClusterMap.get(subClusterName); if (parentCluster.getChildSession() == null) {
       * Session childSession = new Session(parentCluster.getMetaData().getDataSource(),
       * "subclusters of cluster " + parentCluster.getClusterName() + "("+childClusters.size()+")");
       * parentCluster.setChildSession(childSession);
       * parentCluster.getChildSession().setEntityClusters(childClusters);
       * this.store(parentCluster.getMetaData(), childClusters,
       * progress.continueWithSubProgress(10)); } }
       */

      /* WORKING */
      /*
       * for (Map.Entry<String, List<EntityCluster>> subEntityClusterList :
       * subEntityClusters.entrySet()) { String parentClusterName = subEntityClusterList.getKey();
       * if (parentClusterName != null) { List<EntityCluster> childClusters =
       * subEntityClusterList.getValue(); EntityCluster parentCluster =
       * entityClusterMap.get(parentClusterName); if (parentCluster.getChildSession() == null) {
       * Session childSession = new Session(parentCluster.getMetaData().getDataSource(),
       * "subclusters of cluster " + parentCluster.getClusterName() + "("+childClusters.size()+")");
       * parentCluster.setChildSession(childSession);
       * parentCluster.getChildSession().setEntityClusters(childClusters);
       * this.store(parentCluster.getMetaData(), childClusters,
       * progress.continueWithSubProgress(10)); } } }
       */

      for (String children : getKeyByValue(this.classNameHierarchy, this.clusterName)) {
        storeClustersRecursively(progress, subEntityClusters, entityClusterMap, children);
      }

      return session;
    } finally {
      progress.stopProgress();
    }
  }

  private void storeClustersRecursively(IProgress progress,
      Map<String, List<EntityCluster>> subEntityClusters,
      Map<String, EntityCluster> entityClusterMap, String clusterName) throws SQLException {
    if (clusterName != null) {
      List<EntityCluster> childClusters = subEntityClusters.get(clusterName);
      if (childClusters != null) {
        EntityCluster parentCluster = entityClusterMap.get(clusterName);
        if (parentCluster.getChildSession() == null) {
          Session childSession =
              new Session(parentCluster.getMetaData().getDataSource(), "subclusters of cluster "
                  + parentCluster.getClusterName() + "(" + childClusters.size() + ")");
          parentCluster.setChildSession(childSession);
          parentCluster.getChildSession().setEntityClusters(childClusters);
          this.store(parentCluster.getMetaData(), childClusters,
              progress.continueWithSubProgress(10));
        }
        for (String children : getKeyByValue(this.classNameHierarchy, clusterName)) {
          storeClustersRecursively(progress, subEntityClusters, entityClusterMap, children);
        }
      }
    }
  }

  private List<EntityCluster> cluster(IEntitySchemaLoader loader, DataSource source,
      List<IEntity> entities, final int numOfClusters, IProgress progress) {
    logger.debug("Start ontology clustering...");

    Ontology clusterer = new Ontology(source, loader, progress);
    clusterer.setEntities(entities);
    clusterer.setNumClusters(numOfClusters);

    IClusterFactory factory = clusterer.cluster();

    clusterer.getClassHierarchy();
    clusterer.getClasses();
    this.classNameHierarchy = clusterer.getClassNameHierarchy();

    return factory.getEntityClusters(progress);
  }

  private List<IEntity> getEntities(final Cluster parent, IProgress progress) throws SQLException {
    try {
      IEntitySchemaList entityList = null;
      entityList = loader.getEntityList(parent, "sp", progress);
      if (entityList == null) {
        return new ArrayList<IEntity>(0);
      }
      return new InMemoryEntityList(entityList);
    } catch (SQLException e) {
      logger.error("Unable to load cluster entities!", e);
      throw e;
    }
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

  private List<String> getKeyByValue(Map<String, String> map, String value) {
    List<String> keys = new ArrayList<String>();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }
}
