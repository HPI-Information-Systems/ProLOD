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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is a container for AssociationRuleModels and corresponding general statistics about
 * all rules.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class ARSetModel implements IsSerializable, Serializable {
  private static final long serialVersionUID = -1314915002273938440L;
  private ArrayList<AssociationRuleModel> modelSet = new ArrayList<AssociationRuleModel>();

  /**
   * Enables the default constructor for RPCs
   */
  protected ARSetModel() {
    // nothing to do
  }

  /**
   * Creates new Container with all rules as AssociationRuleModels and a map of statistics about how
   * frequent large itemsets of a certain size occur
   * 
   * @param modelSet
   */
  public ARSetModel(ArrayList<AssociationRuleModel> modelSet) {
    this.setModelSet(modelSet);
  }

  /**
   * Sets a vector containing Rules.
   * 
   * @param modelSet
   */
  public void setModelSet(ArrayList<AssociationRuleModel> modelSet) {
    this.modelSet = modelSet;
  }

  /**
   * Gets the vector that contains the rule representation
   * 
   * @return
   */
  public ArrayList<AssociationRuleModel> getModelSet() {
    return this.modelSet;
  }
}
