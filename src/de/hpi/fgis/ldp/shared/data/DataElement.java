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

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * represents a abstract data element
 * 
 * @author toni.gruetze
 * 
 */
public abstract class DataElement implements Serializable, IsSerializable {
  private static final long serialVersionUID = 1773049499521028950L;
  protected int id = -1;
  protected String label;

  protected DataElement() {
    // hide default constructor
  }

  /**
   * gets the unique id of this data element (in the storage, eg database). If it is not stored, the
   * is is -1 or less.
   */
  public int getId() {
    return this.id;
  }

  // FIXME rename to "getName"
  /**
   * Gets the label of this data element
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * checks if this instance represents an abstract pattern for different data elements
   * 
   * @return <code>true</code> if this instance represents an pattern and not an actual db-instance,
   *         othherwise <code>false</code>.
   */
  public boolean isPattern() {
    return this.getId() < 0;
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
    result = prime * result + getNormalizedId();
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(java.lang.Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DataElement other = (DataElement) obj;
    if (getNormalizedId() != other.getNormalizedId()) {
      return false;
    }
    if (label == null) {
      if (other.label != null) {
        return false;
      }
    } else if (!label.equals(other.label)) {
      return false;
    }
    return true;
  }

  private int getNormalizedId() {
    return id < 0 ? -1 : id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getLabel();
  }
}
