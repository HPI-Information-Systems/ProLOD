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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hpi.fgis.ldp.server.algorithms.clustering.EntityClusterFactory;
import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * This is the standard K-Means implementation.
 * 
 * @author hefenbrock
 * 
 */
public class KMeans {
  protected Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
  protected List<IEntity> entities;
  protected EntityClusterFactory factory;
  protected ISchemaComparator comparator;

  protected List<ISchema> means;
  protected int numClusters;
  protected Random rand = new Random();

  private int[] assignments;
  private int numSeedCadidates = 1;

  // stop clustering after this number of iterations
  // if no convergence is achieved
  public static final int MAX_ITERATIONS = 20;
  // stop clustering if less than 0.75% of all entities
  // were reassigned during a iteration
  public static final double MIN_REASSIGNED = 0.75;

  public KMeans(DataSource source) {
    this.factory = new EntityClusterFactory(source);
  }

  public EntityClusterFactory cluster() {
    // check();

    this.initClusters(this.numClusters);

    int iterations = 1;

    double procReassigned = 0;
    do {
      this.computeMeans();
      int reassigned = this.reassign();

      double oldProcReassigned = procReassigned;
      procReassigned = (reassigned * 100.0) / this.entities.size();

      // TODO check that every cluster has at least one entity

      if (procReassigned == oldProcReassigned) {
        this.logger.log(Level.CONFIG, "    Ping-pong condition detected - aborting.");
        break;
      }

      this.logger.log(Level.CONFIG, "  Iteration " + iterations++ + " (" + procReassigned
          + "% reassigned (" + reassigned + "))");

    } while (KMeans.MIN_REASSIGNED < procReassigned && iterations <= KMeans.MAX_ITERATIONS);

    return this.factory;
  }

  // perform some checks
  public void check() throws Exception {
    if (0 == this.numClusters) {
      throw new Exception("KMeansImpl: Number of clusters not specified.");
    }
    if (this.numClusters > this.entities.size()) {
      throw new Exception("KMeansImpl: More clusters than entities.");
    }
    if (this.numSeedCadidates + this.numClusters - 1 > this.entities.size()) {
      throw new Exception("KMeansImpl: Too many seed candidates.");
    }
  }

  // step 1
  protected void initClusters(int numClusters) {
    // nothing to be cluster
    if (this.entities.size() <= 0) {
      return;
    }
    this.assignments = new int[this.entities.size()];
    this.means = new ArrayList<ISchema>();
    Set<IEntity> selectedEntities = new HashSet<IEntity>();
    for (int i = 0; i < numClusters; i++) {
      // select random candidates
      IEntity[] candidates = new IEntity[this.numSeedCadidates];
      for (int j = 0; j < candidates.length; j++) {
        // make sure we don't take one entity twice
        do {
          candidates[j] = this.entities.get(this.rand.nextInt(this.entities.size()));
        } while (selectedEntities.contains(candidates[j]));
      }
      // pick best entity from candidates
      double minMaxSim = 1;
      int bestEntity = -1;
      for (int j = 0; j < candidates.length; j++) {
        double maxSim = 0;
        for (int k = 0; k < i; k++) {
          double sim = this.comparator.computeSim(candidates[j].schema(), this.means.get(k));
          if (sim > maxSim) {
            maxSim = sim;
          }
        }
        if (maxSim <= minMaxSim) {
          bestEntity = j;
          minMaxSim = maxSim;
        }
      }
      IEntity selectedEntity = candidates[bestEntity];
      selectedEntities.add(selectedEntity);
      int clusterIndex = this.factory.createNewCluster(selectedEntity);
      // the assumption is that clusterId == i
      this.assignments[selectedEntity.getIndex()] = clusterIndex;
      // the schema of the initial entity is the initial mean schema
      this.means.add(selectedEntity.schema());
    }
    // create initial assignment
    for (IEntity entity : this.entities) {
      if (!selectedEntities.contains(entity)) {
        Iterator<Integer> clusters = this.factory.clusterIterator();
        int bestCluster = -1;
        double bestSim = -1;
        while (clusters.hasNext()) {
          int cluster = clusters.next().intValue();
          double sim = this.comparator.computeSim(this.means.get(cluster), entity.schema());
          if (sim > bestSim || -1 == bestSim) {
            // better cluster found
            bestCluster = cluster;
            bestSim = sim;
          }
        }
        this.factory.addEntity(bestCluster, entity);
        this.assignments[entity.getIndex()] = bestCluster;
      }
    }
  }

  // step 2
  protected int reassign() {
    int reassigned = 0;
    // for each entity, compute the best cluster
    for (IEntity entity : this.entities) {
      Iterator<Integer> clusters = this.factory.clusterIterator();
      int bestCluster = this.assignments[entity.getIndex()];
      double bestSim = this.comparator.computeSim(this.means.get(bestCluster), entity.schema());
      while (clusters.hasNext()) {
        int cluster = clusters.next().intValue();
        double sim = this.comparator.computeSim(this.means.get(cluster), entity.schema());
        if (sim > bestSim || (sim == bestSim) // if sim is equal, choose
            // larger
            // cluster
            && this.factory.size(cluster) > this.factory.size(this.assignments[entity.getIndex()])) {
          // better cluster found
          bestCluster = cluster;
          bestSim = sim;
        }
      }
      if (bestCluster != this.assignments[entity.getIndex()]) {
        reassigned++;
        this.reassign(entity, bestCluster);
      }
    }
    return reassigned;
  }

  // step 3
  protected void computeMeans() {
    Iterator<Integer> clusters = this.factory.clusterIterator();
    while (clusters.hasNext()) {
      int cluster = clusters.next().intValue();
      this.means.set(cluster, this.comparator.computeMean(this.factory.entities(cluster)));
    }
  }

  private void reassign(IEntity entity, int clusterId) {
    // remove old assignment
    this.factory.removeEntity(this.assignments[entity.getIndex()], entity);
    // place new assignment
    this.factory.addEntity(clusterId, entity);
    this.assignments[entity.getIndex()] = clusterId;
  }

  public void setNumClusters(int num) {
    this.numClusters = num;
  }

  public void setNumSeedCandidates(int num) {
    this.numSeedCadidates = num;
  }

  public void setEntities(List<IEntity> entities) {
    this.entities = entities;
  }

  public void setEntityComparator(ISchemaComparator cmp) {
    this.comparator = cmp;
    this.factory.setComparator(cmp);
  }

}
