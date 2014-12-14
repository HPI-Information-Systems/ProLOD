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
 * represents a normalized pattern of a object value
 * 
 * @author toni.gruetze
 * 
 */
public class NormalizedPattern extends DataElement {
  private static final long serialVersionUID = -2860816026712184587L;

  protected NormalizedPattern() {
    // hide default constructor
  }

  /**
   * creates a new abstract pattern for different normalized pattern in the db (e.g. with wildcards)
   * 
   * @param pattern the pattern of the normalized pattern
   */
  public NormalizedPattern(final String pattern) {
    this.id = -1;
    this.label = pattern;
  }

  /**
   * creates a new normalized pattern with the given attributes
   * 
   * @param id the id of the normalized pattern
   * @param name the name of the normalized pattern
   */
  public NormalizedPattern(final int id, final String name) {
    this.id = id;
    this.label = name;
  }
}
