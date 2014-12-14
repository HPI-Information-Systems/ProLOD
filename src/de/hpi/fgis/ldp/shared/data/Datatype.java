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
 * represents a datatype
 * 
 * @author toni.gruetze
 * 
 */
public class Datatype extends DataElement {
  private static final long serialVersionUID = -2530357644928425298L;

  /**
   * Database representation of an unknown datatype
   */
  public static final Datatype UNKNOWN = new Datatype(0, "Unknown"); // 00000000

  /**
   * Database representation of the OTHER datatype
   */
  public static final Datatype OTHER = new Datatype(1, "Other"); // 00000001

  /**
   * Database representation of the STRING datatype
   */
  public static final Datatype STRING = new Datatype(3, "String"); // 00000011

  /**
   * Database representation of the TEXT datatype
   */
  public static final Datatype TEXT = new Datatype(7, "Text"); // 000000111

  /**
   * Database representation of the INTEGER datatype
   */
  public static final Datatype INTEGER = new Datatype(11, "Integer"); // 000001011

  /**
   * Database representation of the FLOAT datatype
   */
  public static final Datatype DECIMAL = new Datatype(19, "Decimal"); // 000010011

  /**
   * Database representation of the DATE datatype
   */
  public static final Datatype DATE = new Datatype(35, "Date"); // 000100011

  /**
   * Database representation of the LINK datatype
   */
  public static final Datatype LINK = new Datatype(67, "External link"); // 001000011

  /**
   * Database representation of the INTERNAL_LINK datatype
   */
  public static final Datatype INTERNAL_LINK = new Datatype(195, "Internal link"); // 011000011

  /**
   * Database representation of the EMPTY datatype
   */
  public static final Datatype EMPTY_VALUE = new Datatype(259, "Empty"); // 100000011

  /**
   * Database representations of all datatypes
   */
  public final static Datatype[] ALL = new Datatype[] {UNKNOWN, OTHER, STRING, TEXT, INTEGER,
      DECIMAL, DATE, LINK, INTERNAL_LINK, EMPTY_VALUE};

  /**
   * gets the id of the datatype
   * 
   * @param name the name of the datatype
   * @return the id of the datatype
   */
  public static int getID(final String name) {
    for (final Datatype currentDatatype : ALL) {
      if (currentDatatype.getLabel().equals(name)) {
        return currentDatatype.getId();
      }
    }

    return -1;
  }

  /**
   * gets the name of the datatype
   * 
   * @param name the id datatype
   * @return the name of the datatype
   */
  public static String getName(final int id) {
    for (final Datatype currentDatatype : ALL) {
      if (currentDatatype.getId() == id) {
        return currentDatatype.getLabel();
      }
    }

    return null;
  }

  /**
   * gets the datatype
   * 
   * @param name the id datatype
   * @return the datatype
   */
  public static Datatype getDatatype(final int id) {
    for (final Datatype currentDatatype : ALL) {
      if (currentDatatype.getId() == id) {
        return currentDatatype;
      }
    }

    return null;
  }

  protected Datatype() {
    // hide default constructor
  }

  /**
   * creates a new datatype with the given attributes
   * 
   * @param id the id of the datatype
   * @param name the name of the datatype
   */
  protected Datatype(final int id, final String name) {
    if (id < 0) {
      throw new IllegalArgumentException("Unknown Datatype with id " + id);
    }
    this.id = id;
    this.label = name;
  }

  /**
   * checks if this instance represents an abstract pattern for different predicates
   * 
   * @return <code>true</code> if this instance represents an pattern and not an actual db-instance,
   *         othherwise <code>false</code>.
   */
  @Override
  public boolean isPattern() {
    return false;
  }
}
