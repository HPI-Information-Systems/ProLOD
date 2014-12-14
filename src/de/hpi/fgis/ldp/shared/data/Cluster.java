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

package de.hpi.fgis.ldp.shared.data;

/**
 * represents a cluster/partition in the data cloud w/o details
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 * 
 */
public class Cluster extends DataElement {
  private static final long serialVersionUID = -5643961087065532523L;
  private int index;
  private double error = -1;
  private int size = 0;
  private int tripleCount;
  private DataSource dataSource;
  private Long processIdentifier;

  private int childSessionID = -1;

  protected Cluster() {
    // hide default constructor
  }

  public Cluster(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * gets the index of this cluster in the current session.
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * If this cluster is associated to a sub-session, this method will return the sub-session, null
   * otherwise.
   */
  public int getChildSessionID() {
    return this.childSessionID;
  }

  /**
   * The error of an cluster is the average distance of its entities from the cluster mean.
   */
  public double getError() {
    return this.error;
  }

  /**
   * gets the size of this cluster (number of entities)
   * 
   * @return the number of entities
   */
  public int getSize() {
    return this.size;
  }

  /**
   * sets the unique cluster id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * sets the index of this cluster in the current session.
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * sets the subclustering session
   */
  public void setChildSessionID(int id) {
    this.childSessionID = id;
  }

  // FIXME rename to "setName"
  /**
   * sets the label of this cluster
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * sets the error of an cluster is the average distance of its entities from the cluster mean.
   */
  public void setError(double error) {
    this.error = error;
  }

  /**
   * gets the size of this cluster (number of entities)
   * 
   * @param size the number of entities
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * gets the amount of triples of this cluster
   * 
   * @return the number of triples
   */
  public int getTripleCount() {
    return this.tripleCount;
  }

  /**
   * sets the triple count
   * 
   * @param count the amount of triples in this cluster
   */
  public void setTripleCount(int count) {
    this.tripleCount = count;
  }

  /**
   * gets the data source of this cluster
   * 
   * @return the data source
   */
  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * checks either the cluster is in Progress or not
   * 
   * @return <code>true</code> if the cluster is appended to a long running process, otherwise
   *         <code>false</code>
   */
  public boolean isInProgress() {
    return this.processIdentifier != null;
  }

  /**
   * gets the identifier of the calculation process of this cluster
   * 
   * @return the process identifier or <code>null</code> if no process is attached
   */
  public Long getProgressIdentifier() {
    return this.processIdentifier;
  }

  /**
   * sets the identifier of the calculation process of this cluster
   * 
   * @param identifier the process identifier or <code>null</code> if no process is attached
   */
  public void setProgressIdentifier(Long identifier) {
    this.processIdentifier = identifier;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
    result = prime * result + super.hashCode();
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Cluster other = (Cluster) obj;
    if (dataSource == null) {
      if (other.dataSource != null) {
        return false;
      }
    } else if (!dataSource.equals(other.dataSource)) {
      return false;
    }
    return super.equals(obj);
  }
}
