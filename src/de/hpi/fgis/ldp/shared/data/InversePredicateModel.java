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
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a pair of predicates and it's coefficients
 * 
 */
public class InversePredicateModel implements IsSerializable, Serializable,
    Comparable<InversePredicateModel> {
  private static final long serialVersionUID = 3687281469833144786L;
  private int predicateIDOne;
  private String predicateNameOne;

  private int predicateIDTwo;
  private String predicateNameTwo;

  private double correlation;
  private double support;
  private int countEntitiesOne;
  private int countEntitiesTwo;
  private int countIntersection;

  private ArrayList<String> exampleSubjects1;
  private ArrayList<String> exampleSubjects2;

  /**
   * Enables the default constructor for RPCs
   */
  public InversePredicateModel() {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(InversePredicateModel o) {
    if (o == null) {
      return 1;
    }

    return -Double.compare(this.correlation, o.correlation);
  }

  /**
   * Get the id of the first predicate
   * 
   * @return the id of the first predicate
   */
  public int getPredicateIDOne() {
    return this.predicateIDOne;
  }

  /**
   * set the id of the first predicate
   * 
   * @param predicateIDOne the id of the first predicate
   */
  public void setPredicateIDOne(int predicateIDOne) {
    this.predicateIDOne = predicateIDOne;
  }

  /**
   * Get the name of the first predicate
   * 
   * @return the name of the first predicate
   */
  public String getPredicateNameOne() {
    return this.predicateNameOne;
  }

  /**
   * Set the name of the first predicate
   * 
   * @param predicateNameOne the name of the first predicate
   */
  public void setPredicateNameOne(String predicateNameOne) {
    this.predicateNameOne = predicateNameOne;
  }

  /**
   * Get the id of the second predicate
   * 
   * @return the id of the second predicate
   */
  public int getPredicateIDTwo() {
    return this.predicateIDTwo;
  }

  /**
   * set the id of the second predicate
   * 
   * @param predicateIDTwo the id of the second predicate
   */
  public void setPredicateIDTwo(int predicateIDTwo) {
    this.predicateIDTwo = predicateIDTwo;
  }

  /**
   * Get the name of the second predicate
   * 
   * @return the name of the second predicate
   */
  public String getPredicateNameTwo() {
    return this.predicateNameTwo;
  }

  /**
   * Set the name of the second predicate
   * 
   * @param predicateNameTwo the name of the second predicate
   */
  public void setPredicateNameTwo(String predicateNameTwo) {
    this.predicateNameTwo = predicateNameTwo;
  }

  /**
   * Get the correlation of the (inverse) predicates
   * 
   * @return the correlation of the (inverse) predicates
   */
  public double getCorrelation() {
    return this.correlation;
  }

  /**
   * Set the correlation of the (inverse) predicates
   * 
   * @param correlation the correlation of the (inverse) predicates
   */
  public void setCorrelation(double correlation) {
    this.correlation = correlation;
  }

  /**
   * Get the support of the (inverse) predicates
   * 
   * @return the support of the (inverse) predicates
   */
  public double getSupport() {
    return this.support;
  }

  /**
   * Set the support of the (inverse) predicates
   * 
   * @param support the support of the (inverse) predicates
   */
  public void setSupport(double support) {
    this.support = support;
  }

  /**
   * Set the examples
   * 
   * @param example1 the examples for the first Subject
   * @param example2 the examples for the second Subject
   */
  public void setExampleSubjects(final ArrayList<String> example1, final ArrayList<String> example2) {
    if (example1.size() != example2.size()) {
      throw (new IllegalArgumentException("differences in example set size found"));
    }
    this.exampleSubjects1 = example1;
    this.exampleSubjects2 = example2;
  }

  /**
   * Get the example subjects (first subject)
   * 
   * @return the example subjects
   */
  public List<String> getExampleSubjects1() {
    return this.exampleSubjects1;
  }

  /**
   * Get the example subjects (second subject)
   * 
   * @return the example subjects
   */
  public List<String> getExampleSubjects2() {
    return this.exampleSubjects2;
  }

  /**
   * Get the count of all entities with predicate one links
   * 
   * @return the count of entities
   */
  public int getEntityOneCount() {
    return this.countEntitiesOne;
  }

  /**
   * Set the amount of all entities with predicate one links
   * 
   * @param count the count of entities
   */
  public void setEntityOneCount(int count) {
    this.countEntitiesOne = count;
  }

  /**
   * Get the count of all entities with predicate two links
   * 
   * @return the count of entities
   */
  public int getEntityTwoCount() {
    return this.countEntitiesTwo;
  }

  /**
   * Set the amount of all entities with predicate two links
   * 
   * @param count the count of entities
   */
  public void setEntityTwoCount(int count) {
    this.countEntitiesTwo = count;
  }

  /**
   * Get the amount of inverse entity pairs
   * 
   * @return the amount of inverse entity pairs
   */
  public int getCountIntersection() {
    return this.countIntersection;
  }

  /**
   * Set the amount of inverse entity pairs
   * 
   * @param countIntersection the amount of inverse entity pairs
   */
  public void setCountIntersection(int countIntersection) {
    this.countIntersection = countIntersection;
  }
}
