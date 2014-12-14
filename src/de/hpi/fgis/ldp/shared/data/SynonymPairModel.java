package de.hpi.fgis.ldp.shared.data;

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

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;

/**
 * This class is a container for association Rules. Objects of this class are used for client server
 * transfer of rules.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class SynonymPairModel implements IsSerializable {

  private String condition;
  private String consequence;
  private double correlation;
  private DataTable details;
  private int frequency;

  /**
   * Enables the default constructor for RPCs
   */
  protected SynonymPairModel() {
    // nothiing to do
  }

  /**
   * Default simple constructor without subject list
   * 
   * @param condition
   * @param consequence
   * @param confidence
   * @param correlation
   * @param dt
   */
  public SynonymPairModel(String condition, String consequence, double correlation, int frequency,
      DataTable dt) {
    this.setCondition(condition);
    this.setConsequence(consequence);
    this.setFrequency(frequency);
    this.setCorrelationCoefficient(correlation);
    this.setDetails(dt);

  }

  public void setDetails(DataTable dt) {
    details = dt;

  }

  public DataTable getDetails() {
    return details;

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

}
