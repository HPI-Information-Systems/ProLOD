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
 * represents a predicate
 * 
 * @author toni.gruetze
 * 
 */
public class Predicate extends DataElement {
  private static final long serialVersionUID = -6460520095842802138L;

  protected Predicate() {
    // hide default constructor
  }

  /**
   * creates a new abstract pattern for different predicates in the db
   * 
   * @param pattern the pattern of the predicates
   */
  public Predicate(final String pattern) {
    this.id = -1;
    this.label = pattern;
  }

  /**
   * creates a new predicate with the given attributes
   * 
   * @param id the id of the predicate
   * @param name the name of the predicate
   */
  public Predicate(final int id, final String name) {
    this.id = id;
    this.label = name;
  }
}
