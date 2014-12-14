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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is a container for association Rules. Objects of this class are used for client server
 * transfer of rules.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class AssociationRuleModel implements IsSerializable {

  private String condition;
  private String consequence;
  private double confidence;
  private double correlation;
  private ArrayList<String> subjects;
  private int frequency;
  private int size;

  /**
   * Enables the default constructor for RPCs
   */
  protected AssociationRuleModel() {
    // nothiing to do
  }

  /**
   * Default simple constructor without subject list
   * 
   * @param condition
   * @param consequence
   * @param confidence
   * @param correlation
   */
  public AssociationRuleModel(String condition, String consequence, double confidence,
      double correlation) {
    this.setCondition(condition);
    this.setConsequence(consequence);
    this.setConfidence(confidence);
    this.setCorrelationCoefficient(correlation);

  }

  /**
   * Sets the number of items involved in the current rule.
   * 
   * @param size
   */
  private void setSize(int size) {
    this.size = size;

  }

  /**
   * Constructor with all parameters including list of related subjects.
   * 
   * @param conditionAsString
   * @param consequenceAsString
   * @param confidence2
   * @param correlationCoefficient
   * @param subjectsAsString
   * @param frequency
   * @param size
   */
  public AssociationRuleModel(String conditionAsString, String consequenceAsString,
      double confidence2, double correlationCoefficient, Vector<String> subjectsAsString,
      int frequency, int size) {
    this(conditionAsString, consequenceAsString, confidence2, correlationCoefficient);
    this.setSubjects(subjectsAsString);
    this.setFrequency(frequency);
    this.setSize(size);
  }

  /**
   * Sets the String representation of a Condition
   * 
   * @param condition
   */
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   * gets the String representation of a Condition
   * 
   * @return
   */
  public String getCondition() {
    return this.condition;
  }

  /**
   * Sets the String representation of a Consequence
   * 
   * @param consequence
   */
  public void setConsequence(String consequence) {
    this.consequence = consequence;
  }

  /**
   * gets the String representation of a Consequence
   * 
   * @return
   */
  public String getConsequence() {
    return this.consequence;
  }

  /**
   * Sets the related Correlation Coefficient
   * 
   * @param correlation
   */
  public void setCorrelationCoefficient(double correlation) {
    this.correlation = correlation;
  }

  /**
   * gets the related Correlation Coefficient
   * 
   * @return
   */
  public double getCorrelationCoefficient() {
    return this.correlation;
  }

  /**
   * Sets the related Confidence value
   * 
   * @param confidence
   */
  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  /**
   * gets the related Confidence value.
   * 
   * @return
   */
  public double getConfidence() {
    return this.confidence;
  }

  /**
   * Sets a list of subject names.
   * 
   * @param subjects
   */
  public void setSubjects(List<String> subjects) {
    this.subjects = new ArrayList<String>(subjects);
  }

  /**
   * Gets a list of subject names.
   */
  public List<String> getSubjects() {
    return this.subjects;
  }

  /**
   * Sets the frequency of the associated large itemset of the current rule.
   * 
   * @param frequency
   */
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  /**
   * gets the frequency of the large item set that is associated with the rule.
   * 
   * @return
   */
  public int getFrequency() {
    return this.frequency;
  }

  /**
   * Gets the number of Items involved in the current rule
   * 
   * @return
   */
  public int getSize() {
    return this.size;
  }
}
