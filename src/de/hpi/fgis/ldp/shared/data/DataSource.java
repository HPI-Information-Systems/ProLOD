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
 * represents a data source of a object value
 * 
 * @author toni.gruetze
 * 
 */
public class DataSource extends DataElement {
  private static final long serialVersionUID = 8406408632156737912L;
  private String userView;
  public static final String DEFAULT_USER_VIEW = "default";
  public static final String ONTOLOGY_USER_VIEW = "ontology";
  public static final String CLUSTER_USER_VIEW = DEFAULT_USER_VIEW;

  protected DataSource() {
    // hide default constructor
  }

  /**
   * creates a new data source with the given attributes
   * 
   * @param name the name of the data source
   */
  public DataSource(final String name) {
    this(name, null);
  }

  /**
   * creates a new data source with the given attributes
   * 
   * @param name the name of the data source
   * @param userView the name of the user view of this data source
   */
  protected DataSource(final String name, String userView) {
    this.id = -1;
    this.label = name;
    this.userView = userView;
  }

  /**
   * gets a representation of this data source with the specified user view
   * 
   * @param userView the name of the user view of this data source
   * @return this data source with the given user view
   */
  public DataSource asUserView(String userView) {
    return new DataSource(this.label, userView);
  }

  /**
   * gets the name of the user view of this data source. If is the default view this value may be
   * <code>null</code>.
   */
  public String getUserView() {
    return this.userView;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.data.DataElement#isPattern()
   */
  @Override
  public boolean isPattern() {
    return false;
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
    if (label == null) {
      if (other.label != null) {
        return false;
      }
    } else if (!label.equalsIgnoreCase(other.label)) {
      return false;
    }
    return true;
  }
}
