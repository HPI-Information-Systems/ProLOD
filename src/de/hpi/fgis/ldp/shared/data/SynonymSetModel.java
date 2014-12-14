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
 * This class is a container for synonyms and corresponding general statistics about all rules.
 * 
 * @author ziawasch.abedjan
 * 
 */
public class SynonymSetModel implements IsSerializable, Serializable {
  private static final long serialVersionUID = 1249714764059558204L;
  private ArrayList<SynonymPairModel> modelSet = new ArrayList<SynonymPairModel>();

  /**
   * Enables the default constructor for RPCs
   */
  protected SynonymSetModel() {
    // nothing to do
  }

  /**
   * Creates new Container with all synonym pairs and a map of statistics about how frequent large
   * itemsets of a certain size occur
   * 
   * @param modelSet
   */
  public SynonymSetModel(ArrayList<SynonymPairModel> modelSet) {
    this.setModelSet(modelSet);
  }

  /**
   * Sets a vector containing synonyms.
   * 
   * @param modelSet
   */
  public void setModelSet(ArrayList<SynonymPairModel> modelSet) {
    this.modelSet = modelSet;
  }

  /**
   * Gets the vector that contains the synonym representation
   * 
   * @return
   */
  public ArrayList<SynonymPairModel> getModelSet() {
    return this.modelSet;
  }
}
