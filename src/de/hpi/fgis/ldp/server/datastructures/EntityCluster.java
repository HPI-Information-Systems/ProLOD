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

package de.hpi.fgis.ldp.server.datastructures;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * represents a cluster/partition in the data cloud
 * 
 * @author daniel.hefenbrock
 * 
 */
public class EntityCluster {
  private ISchema meanSchema = null;
  protected int[] entities;
  private final Cluster data;
  private Session childSession;
  private String clusterName = null;

  public EntityCluster(DataSource source, int index) {
    this.data = new Cluster(source);
    this.data.setIndex(index);
  }

  public EntityCluster(DataSource source, int index, int[] entities, ISchema mean, double error) {
    this.data = new Cluster(source);
    this.data.setIndex(index);
    this.entities = entities;
    this.meanSchema = mean;
    this.data.setError(error);
  }

  public EntityCluster(DataSource source, int id, int index, String label, int[] entities,
      Session childSession, ISchema mean, double error) {
    this.data = new Cluster(source);
    this.data.setId(id);
    this.data.setIndex(index);
    this.data.setLabel(label);
    this.data.setError(error);
    this.entities = entities;
    this.childSession = childSession;
    this.meanSchema = mean;
  }

  public EntityCluster(Cluster cluster, int[] entities, ISchema mean) {
    this.data = cluster;
    this.entities = entities;
    this.meanSchema = mean;
  }

  /**
   * gets the meta data (actual cluster instance) of this instance
   * 
   * @return the actual cluster
   */
  public Cluster getMetaData() {
    return this.data;
  }

  /**
   * Returns a list containing all ids of all entities of the cluster. Assumption: This list is
   * ordered ascending.
   */
  public List<Integer> entityIds() {
    return new AbstractList<Integer>() {

      @Override
      public Integer get(int index) {
        return Integer.valueOf(entities[index]);
      }

      @Override
      public int size() {
        return entities.length;
      }

    };
  }

  /**
   * All entities of otherClutser will be added to this cluster. otherCluster will be cleared.
   */
  public void join(EntityCluster otherCluster) {
    int[] newEntities = new int[this.entities.length + otherCluster.entityIds().size()];
    for (int i = 0; i < this.entities.length; i++) {
      newEntities[i] = this.entities[i];
    }
    int i = this.entities.length;
    for (int entityId : otherCluster.entityIds()) {
      newEntities[i++] = entityId;
    }
    this.entities = newEntities;
    Arrays.sort(this.entities);
    otherCluster.clear();
  }

  /**
   * Drop all entities. The cluster will be empty afterwards.
   */
  public void clear() {
    this.entities = new int[0];
  }

  /**
   * Returns the mean schema of this cluster.
   */
  public ISchema getMeanSchema() {
    return this.meanSchema;
  }

  /**
   * sets the child session
   */
  public void setChildSession(Session sess) {
    this.childSession = sess;
  }

  /**
   * gets the child session
   * 
   * @return
   */
  public Session getChildSession() {
    return this.childSession;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getClusterName() {
    return this.clusterName;
  }
}
