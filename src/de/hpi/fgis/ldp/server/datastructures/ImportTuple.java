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

import de.hpi.fgis.ldp.shared.data.Datatype;

/**
 * represents a entry (row) in the initial import table
 * 
 * @author toni.gruetze
 */
public class ImportTuple {

  private String subject;
  private String predicate;
  private String object;
  private String normPattern;
  private String pattern;
  private Datatype datatype;
  private double parsedValue;

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

  public String getNormPattern() {
    return this.normPattern;
  }

  public void setNormPattern(String normPattern) {
    this.normPattern = normPattern;
  }

  public String getPattern() {
    return this.pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public Datatype getDatatype() {
    return this.datatype;
  }

  public void setDatatype(Datatype datatype) {
    this.datatype = datatype;
  }

  public double getParsedValue() {
    return this.parsedValue;
  }

  public void setParsedValue(double parsedValue) {
    this.parsedValue = parsedValue;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getObject() {
    return this.object;
  }
}
