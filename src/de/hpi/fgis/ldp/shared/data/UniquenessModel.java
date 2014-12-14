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
 * Represents a predicate and its uniqueness and density
 * 
 * @author anja.jentzsch
 */
public class UniquenessModel implements IsSerializable, Serializable, Comparable<UniquenessModel> {
  private static final long serialVersionUID = 4398281469833144786L;
  private int predicateID;
  private String predicateName;

  private double uniqueness;
  private double density;
  private Integer uniqueValues;
  private Integer values;

  private ArrayList<String> exampleSubjects;

  /**
   * Enables the default constructor for RPCs
   */
  public UniquenessModel() {}

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(UniquenessModel o) {
    if (o == null) {
      return 1;
    }
    return 1;
    // return -Double.compare(this.uniqueness, o.uniqueness);
  }

  /**
   * set the id of the predicate
   * 
   * @param predicateID the id of the first predicate
   */
  public void setPredicateID(int predicateID) {
    this.predicateID = predicateID;
  }

  /**
   * Get the name of the predicate
   * 
   * @return the name of the predicate
   */
  public String getPredicateName() {
    return this.predicateName;
  }

  /**
   * Set the name of the first predicate
   * 
   * @param predicateNameOne the name of the first predicate
   */
  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }

  /**
   * Get the uniqueness of the predicate
   * 
   * @return the uniqueness of the predicate
   */
  public double getUniqueness() {
    return this.uniqueness;
  }

  /**
   * Set the uniqueness of the predicate
   * 
   * @param uniqueness the uniqueness of the predicate
   */
  public void setUniqueness(double uniqueness) {
    this.uniqueness = uniqueness;
  }

  /**
   * Get the density of the predicate
   * 
   * @return the density of the predicate
   */
  public double getDensity() {
    return this.density;
  }

  /**
   * Set the density of the predicate
   * 
   * @param density the density of the predicate
   */
  public void setDensity(double density) {
    this.density = density;
  }

  /**
   * Returns the keyness of the predicate.
   */
  public Double getKeyness() {
    return this.harmonicMean(this.density, this.uniqueness);
  }

  private double harmonicMean(double... data) {
    double sum = 0.0;
    for (double element : data) {
      sum += 1.0 / element;
    }
    return data.length / sum;
  }

  /**
   * Get the number of property values
   * 
   * @return the number of property values
   */
  public Integer getValues() {
    return this.values;
  }

  /**
   * Set the number of property values
   * 
   * @param density the number of property values
   */
  public void setValues(Integer values) {
    this.values = values;
  }

  /**
   * Get the number of unique property values
   * 
   * @return the number of unique property values
   */
  public Integer getUniqueValues() {
    return this.uniqueValues;
  }

  /**
   * Set the number of unique property values
   * 
   * @param density the number of unique property values
   */
  public void setUniqueValues(Integer uniqueValues) {
    this.uniqueValues = uniqueValues;
  }

  /**
   * Set the examples
   * 
   * @param example the examples for the first Subject
   */
  public void setExampleSubjects(final ArrayList<String> example) {
    this.exampleSubjects = example;
  }

  /**
   * Get the example subjects (first subject)
   * 
   * @return the example subjects
   */
  public List<String> getExampleSubjects() {
    return this.exampleSubjects;
  }

}
