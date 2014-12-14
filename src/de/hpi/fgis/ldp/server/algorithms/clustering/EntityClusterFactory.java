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

package de.hpi.fgis.ldp.server.algorithms.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.fgis.ldp.server.algorithms.clustering.similarity.ISchemaComparator;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class EntityClusterFactory implements IClusterFactory {

  protected List<Set<IEntity>> clusters;
  private ISchemaComparator comparator;
  private final DataSource dataSource;
  private final Map<Integer, String> clusterNames = new HashMap<Integer, String>();

  public EntityClusterFactory(DataSource source) {
    this.clusters = new ArrayList<Set<IEntity>>();
    this.dataSource = source;
  }

  public List<Set<IEntity>> getClusters() {
    return this.clusters;
  }

  public void setComparator(ISchemaComparator comparator) {
    this.comparator = comparator;
  }

  public void addEntity(int clusterId, IEntity entity) {
    this.clusters.get(clusterId).add(entity);
  }

  public void setClusterName(int clusterId, String clusterName) {
    this.clusterNames.put(clusterId, clusterName);
  }

  public Iterable<Integer> clusters() {
    return new Iterable<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return EntityClusterFactory.this.clusterIterator();
      }

    };
  }

  public Iterator<Integer> clusterIterator() {
    return new Iterator<Integer>() {
      int cur = 0;

      @Override
      public boolean hasNext() {
        return EntityClusterFactory.this.clusters.size() > this.cur;
      }

      @Override
      public Integer next() {
        return Integer.valueOf(this.cur++);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public int createNewCluster(IEntity intialEntity) {
    Set<IEntity> cluster = new HashSet<IEntity>();
    cluster.add(intialEntity);
    this.clusters.add(cluster);
    return this.clusters.size() - 1;
  }

  public Iterable<IEntity> entities(int clusterIndex) {
    return this.clusters.get(clusterIndex);
  }

  public int size(int clusterIndex) {
    return this.clusters.get(clusterIndex).size();
  }

  public void removeEntity(int clusterId, IEntity entity) {
    this.clusters.get(clusterId).remove(entity);
  }

  @Override
  public List<EntityCluster> getEntityClusters(final IProgress progress) {
    List<EntityCluster> result = new ArrayList<EntityCluster>();
    int clusterIndex = 0;
    progress.startProgress("building clusters", this.clusters.size());
    for (Set<IEntity> cluster : this.clusters) {
      int[] entities = new int[cluster.size()];
      int i = 0;
      for (IEntity entity : cluster) {
        entities[i++] = entity.getId();
      }
      Arrays.sort(entities);
      ISchema mean = null == this.comparator ? null : this.comparator.computeMean(cluster);
      double error =
          null == this.comparator ? -1 : this.comparator.computeAvgError(
              this.clusters.get(clusterIndex), mean);
      EntityCluster entityCluster =
          new EntityCluster(this.dataSource, clusterIndex++, entities, mean, error);
      if (this.clusterNames != null) {
        entityCluster.setClusterName(this.clusterNames.get(clusterIndex - 1));
      }
      result.add(entityCluster);
      if (clusterIndex % 100 == 1) {
        progress.continueProgressAt(clusterIndex);
      }
    }
    progress.stopProgress();
    return result;
  }

  /**
   * Compute the error distribution for the given set of entities using the provided mean.
   * 
   * @return An array of size 101 reflecting the error distribution.
   */
  public long[] computeErrorDistribution(int clusterIndex, ISchema mean, ISchemaComparator cmp) {

    long[] errorDistribution = new long[101];
    for (int i = 0; i < errorDistribution.length; i++) {
      errorDistribution[i] = 0;
    }

    for (IEntity entity : this.clusters.get(clusterIndex)) {
      double error = 1.0 - cmp.computeSim(entity.schema(), mean);
      int derror = (int) (error * 100);
      errorDistribution[derror]++;
    }

    return errorDistribution;
  }
}
