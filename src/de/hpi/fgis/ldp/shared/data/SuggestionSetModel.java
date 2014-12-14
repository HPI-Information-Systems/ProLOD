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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is a container for synonyms and corresponding general statistics about all rules.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class SuggestionSetModel implements IsSerializable, Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = -1708487691164342572L;
  private List<Subject> subjects;

  public List<Subject> getSubjects() {
    return subjects;
  }

  public List<AssociationRuleModel> getObjectRules() {
    return objectRules;
  }

  public List<AssociationRuleModel> getPredicateRules() {
    return predicateRules;
  }

  private List<AssociationRuleModel> objectRules;
  private List<AssociationRuleModel> predicateRules;

  /**
   * Enables the default constructor for RPCs
   */
  protected SuggestionSetModel() {
    // nothing to do
  }

  /**
   * Creates new Container with all synonym pairs and a map of statistics about how frequent large
   * itemsets of a certain size occur
   * 
   * @param modelSet
   */
  public SuggestionSetModel(List<AssociationRuleModel> predicateRules,
      List<AssociationRuleModel> objectRules, List<Subject> subjects) {
    setPredicateRules(predicateRules);
    setObjectRules(objectRules);
    setSubjects(subjects);

  }

  private void setSubjects(List<Subject> subjects) {
    this.subjects = subjects;

  }

  private void setObjectRules(List<AssociationRuleModel> objectRules) {
    this.objectRules = objectRules;

  }

  private void setPredicateRules(List<AssociationRuleModel> predicateRules) {
    this.predicateRules = predicateRules;

  }

}
