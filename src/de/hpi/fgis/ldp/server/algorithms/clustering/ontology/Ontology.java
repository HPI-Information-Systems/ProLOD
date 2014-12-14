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
 *  * Christoph B??hm <christoph.boehm@hpi.uni-potsdam.de>, or
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
import java.util.logging.Logger;

import de.hpi.fgis.ldp.server.algorithms.clustering.EntityClusterFactory;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * This is the ontology implementation.
 * 
 * @author anja.jentzsch
 * 
 */
public class Ontology {
  protected Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
  protected List<IEntity> entities;
  protected EntityClusterFactory factory;
  protected IEntitySchemaLoader loader;
  protected DataSource source;
  protected ISchemaComparator comparator;
  protected IProgress progress;

  protected List<ISchema> means;
  protected int numClusters;

  protected Map<Integer, String> classes;
  protected Map<Integer, Integer> classHierarchy;
  protected Integer rootClass;

  private int[] assignments;

  public Ontology(DataSource source, IEntitySchemaLoader loader, IProgress progress) {
    this.source = source;
    this.factory = new EntityClusterFactory(source);
    this.loader = loader;
    this.progress = progress;
    this.loadOntology();
  }

  private void loadOntology() {
    this.classes = getClassesFromSchema();
    // this.rootClass = getRootClass();
    this.classHierarchy = createClassHierarchy();
  }

  private Map<Integer, String> getClassesFromSchema() {
    Map<Integer, String> classes = new HashMap<Integer, String>();
    try {
      classes = loader.getClasses(new Cluster(source), progress);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return classes;
  }

  public Map<Integer, String> getClasses() {
    return this.classes;
  }

  private Map<Integer, Integer> createClassHierarchy() {
    Map<Integer, Integer> hierarchy = new HashMap<Integer, Integer>();
    try {
      hierarchy = loader.getClassHierarchy(source, progress);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return hierarchy;
  }

  public Map<Integer, Integer> getClassHierarchy() {
    return this.classHierarchy;
  }

  public Map<String, String> getClassNameHierarchy() {
    Map<String, String> hierarchy = new HashMap<String, String>();
    for (Map.Entry<Integer, Integer> hierarchyPair : this.classHierarchy.entrySet()) {
      hierarchy.put(this.classes.get(hierarchyPair.getKey()),
          this.classes.get(hierarchyPair.getValue()));
    }
    return hierarchy;
  }

  private Integer getRootClass() {
    Integer root = null;
    try {
      root = loader.getRootClass(source, progress);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return root;
  }

  public EntityClusterFactory cluster() {
    // check();
    this.initClusters(this.numClusters);
    return this.factory;
  }

  // perform some checks
  public void check() throws Exception {
    if (0 == this.numClusters) {
      throw new Exception("OntologyImpl: Number of clusters not specified.");
    }
    if (this.numClusters > this.entities.size()) {
      throw new Exception("OntologyImpl: More clusters than entities.");
    }
  }

  protected void initClusters(int numClusters) {

    if (this.entities.size() <= 0) {
      return;
    }
    this.assignments = new int[this.entities.size()];

    Map<Integer, ArrayList<Integer>> entityClassList;
    try {
      entityClassList = loader.getEntityClassList(source, progress, this.classes);
      List<Integer> clusterSeen = new ArrayList<Integer>();
      Map<Integer, Integer> clusterDbMap = new HashMap<Integer, Integer>();
      for (IEntity entity : this.entities) {
        Integer entityDbId = entity.getId();
        if (entityClassList.containsKey(entityDbId)) {
          ArrayList<Integer> clusterDbIds = entityClassList.get(entityDbId);
          for (Integer clusterDbId : clusterDbIds) {
            if (!clusterSeen.contains(clusterDbId)) {
              int clusterIndex = this.factory.createNewCluster(entity);
              this.factory.setClusterName(clusterIndex, this.classes.get(clusterDbId));
              clusterDbMap.put(clusterDbId, clusterIndex);
              clusterSeen.add(clusterDbId);
            } else {
              this.factory.addEntity(clusterDbMap.get(clusterDbId), entity);
            }
            this.assignments[entity.getIndex()] = clusterDbMap.get(clusterDbId);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public void setNumClusters(int num) {
    this.numClusters = num;
  }

  public void setEntities(List<IEntity> entities) {
    this.entities = entities;
  }

  public void setEntityComparator(ISchemaComparator cmp) {
    this.comparator = cmp;
    this.factory.setComparator(cmp);
  }
}
