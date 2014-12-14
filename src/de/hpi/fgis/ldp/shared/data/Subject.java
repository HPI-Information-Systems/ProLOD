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
 * represents a subject
 * 
 * @author toni.gruetze
 * 
 */
public class Subject extends DataElement {
  private static final long serialVersionUID = -3164678018919988217L;

  protected Subject() {
    // hide default constructor
  }

  /**
   * creates a new subject with the given attributes
   * 
   * @param id the id of the subject
   * @param name the name of the subject
   */
  public Subject(final int id, final String name) {
    this.id = id;
    this.label = name;
  }
}
