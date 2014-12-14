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

import java.util.List;

import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * A session is a set of clusterDetails which were created in one cluster run.
 * 
 * @author daniel.hefenbrock
 */
public class Session {
  private int id = -1;
  private String name;
  private List<EntityCluster> entityClusters;
  private final DataSource source;

  public Session(DataSource source, int id) {
    this.id = id;
    this.source = source;
  }

  public Session(DataSource source, String name) {
    this.name = name;
    this.source = source;
  }

  /**
   * Name of the session (optional).
   */
  public String getName() {
    return this.name;
  }

  /**
   * gets the {@link DataSource} of this session
   * 
   * @return the {@link DataSource}
   */
  public DataSource getDataSource() {
    return source;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * If the id is -1, this means that the session is not persisted to the storage.
   */
  public int getId() {
    return this.id;
  }

  public List<EntityCluster> getEntityClusters() {
    return this.entityClusters;
  }

  public void setEntityClusters(List<EntityCluster> clusterDetails) {
    this.entityClusters = clusterDetails;
  }
}
