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

package de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * This Class provides an abstrac data Type for handling an Association Rule and its statistics
 * 
 * @author ziawasch.abedjan
 * 
 */
public class AssociationRule {
  // private final DataSource source;
  private TIntList condition;
  private TIntList consequence;
  private TIntList subjects;
  private int frequency = -1;
  private double confidence = 0;
  private double correlationCoefficient = 0;
  private int size;

  /**
   * Constructor for simple Rules
   * 
   * @param cond
   * @param after
   * @param conf
   */
  public AssociationRule(TIntList cond, TIntList after, double conf) {
    // this.source = source;
    this.setCondition(cond);
    this.setConsequence(after);
    this.setConfidence(conf);
  }

  public AssociationRule(TIntList cond, TIntList after, double conf, double correl) {
    // this.source = source;
    this.setCondition(cond);
    this.setConsequence(after);
    this.setConfidence(conf);
    this.setCorrelationCoefficient(correl);
  }

  public AssociationRule(Integer cond, Integer after, double conf, double correl) {
    this(cond, after, conf);
    this.setCorrelationCoefficient(correl);
  }

  public AssociationRule(int cond, int after, double conf) {
    // this.source = source;
    TIntArrayList cV = new TIntArrayList();
    cV.add(cond);
    TIntArrayList aV = new TIntArrayList();
    aV.add(after);
    this.setCondition(cV);
    this.setConsequence(aV);
    this.setConfidence(conf);
  }

  public AssociationRule(int a, int b, double currentConf, double roh, TIntList subjectList,
      int frequency, int size) {
    this(a, b, currentConf, roh);
    this.setSubjects(subjectList);
    this.setFrequency(frequency);
    this.setSize(size);
  }

  /**
   * Sets the size of the related Large (size)-Itemset.
   * 
   * @param size
   */
  private void setSize(int size) {
    this.size = size;

  }

  public AssociationRule(TIntList setA, TIntList setB, double currentConf, double roh,
      TIntList subjectList, int frequency, int size) {
    this(setA, setB, currentConf, roh);
    this.setSubjects(subjectList);
    this.setFrequency(frequency);
    this.setSize(size);
  }

  /**
   * Sets a Condition of a rule consisting of more than one predicate.
   * 
   * @param cond
   */
  public void setCondition(TIntList cond) {
    this.condition = cond;
  }

  /**
   * Gets the Condition of a rule consisting of more than one predicate
   * 
   * @return
   */
  public TIntList getCondition() {
    return this.condition;
  }

  /**
   * Sets a Consequence of a rule consisting of more than one predicate.
   * 
   * @param Consequence
   */
  public void setConsequence(TIntList after) {
    this.consequence = after;
  }

  /**
   * Gets the Consequence of a rule consisting of more than one predicate
   * 
   * @return
   */
  public TIntList getConsequence() {
    return this.consequence;
  }

  /**
   * Sets a Confidence of a rule.
   * 
   * @param Confidence
   */
  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  /**
   * Gets a Confidence of a rule.
   * 
   * @param Confidence
   */
  public double getConfidence() {
    return this.confidence;
  }

  /**
   * Sets the Correlation Coefficient of a rule.
   * 
   * @param correlationCoefficient
   */
  public void setCorrelationCoefficient(double correlationCoefficient) {
    this.correlationCoefficient = correlationCoefficient;
  }

  /**
   * Gets the Correlation Coefficient of a rule.
   * 
   * @return
   */
  public double getCorrelationCoefficient() {
    return this.correlationCoefficient;
  }

  /**
   * Sets the associated Subject IDs
   * 
   * @param subjectList
   */
  public void setSubjects(TIntList subjectList) {
    this.subjects = subjectList;
  }

  /**
   * Gets the associated Subject IDs
   * 
   * @return
   */
  public TIntList getSubjects() {
    return this.subjects;
  }

  /**
   * Sets the Frequency of the related Large itemset
   * 
   * @param frequency
   */
  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  /**
   * Gets the Frequency of the related large itemset
   * 
   * @return
   */
  public int getFrequency() {
    return this.frequency;
  }

  /**
   * Gets the size of the related large itemset
   * 
   * @return
   */
  public int getSize() {
    return this.size;
  }

  //
  // /**
  // * Converts the related Subject IDs to their values and delivers them.
  // *
  // * @return
  // */
  // public Vector<String> getSubjectsAsString() {
  // HashMap<Integer, String> subjectMap = new HashMap<Integer, String>();
  //
  // for (Integer i : this.subjects) {
  // subjectMap.put(i, "");
  // }
  // // TODO inject
  // MetaLoader loader = new MetaLoader(this.source);
  // loader.fillSubjectNameMap(subjectMap);
  //
  // return new Vector<String>(subjectMap.values());
  // }
  // /**
  // * Converts the Condition elements of a Rule from IDs to the corresponding
  // * String Values.
  // *
  // * @return
  // */
  // public String getConditionAsString() {
  // HashMap<Integer, String> predicates = new HashMap<Integer, String>();
  //
  // int condS = this.condition.size();
  // for (int i = 0; i < condS; i++) {
  // predicates.put(this.condition.elementAt(i), "");
  // }
  // // TODO inject
  // MetaLoader loader = new MetaLoader(this.source);
  // loader.fillPredicateNameMap(predicates);
  //
  // StringBuffer sb = new StringBuffer();
  // for (int i = 0; i < condS; i++) {
  // sb.append(predicates.get(this.condition.elementAt(i)));
  // if (i < condS - 1)
  // sb.append(",");
  // }
  // return sb.toString();
  // }
  //
  // /**
  // * Converts the Consequence elements of a Rule from IDs to the
  // corresponding
  // * String Values.
  // *
  // * @return
  // */
  // public String getConsequenceAsString() {
  // HashMap<Integer, String> predicates = new HashMap<Integer, String>();
  //
  // int afterS = this.consequence.size();
  // for (int i = 0; i < afterS; i++) {
  // predicates.put(this.consequence.elementAt(i), "");
  // }
  // // TODO inject
  // MetaLoader loader = new MetaLoader(this.source);
  // loader.fillPredicateNameMap(predicates);
  //
  // StringBuffer sb = new StringBuffer();
  // for (int i = 0; i < afterS; i++) {
  // sb.append(predicates.get(this.consequence.elementAt(i)));
  // if (i < afterS - 1)
  // sb.append(",");
  // }
  // return sb.toString();
  // }
  //
  // /**
  // * A toString Method for showing Rules on the Console.
  // */
  // @Override
  // public String toString() {
  // // IMetaLoader metaLoader = RuleDataLoader.getInstance();
  // StringBuffer sb = new StringBuffer();
  // sb.append("Association Rule: IF (");
  // sb.append(this.getConditionAsString());
  //
  // if (this.getCorrelationCoefficient() < 0)
  // sb.append(") THEN NOT( ");
  // else
  // sb.append(") THEN( ");
  //
  // sb.append(this.getConsequenceAsString());
  // sb.append(")");
  //
  // String ruleString = sb.toString();
  //
  // return ruleString;
  // }

}
