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

package de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes;

/**
 * represents a simple RDF tuple
 * 
 * @author toni.gruetze
 */
public class Tuple {
  private String subject;
  private String predicate;
  private String object;

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getPredicate() {
    return this.predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getObject() {
    return this.object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  /**
   * gets a array representation of this instance
   * 
   * @return a array with {subject, predicate, object}
   */
  public String[] toArray() {
    return new String[] {this.getSubject(), this.getPredicate(), this.getObject()};
  }

  @Override
  public String toString() {
    return "[" + this.getSubject() + ", " + this.getPredicate() + ", " + this.getObject() + "]";
  }
}
