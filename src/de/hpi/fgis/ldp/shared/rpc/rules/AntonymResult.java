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

package de.hpi.fgis.ldp.shared.rpc.rules;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.hpi.fgis.ldp.shared.data.InversePredicateModel;
import de.hpi.fgis.ldp.shared.rpc.CachableResult;

public class AntonymResult implements CachableResult, IsSerializable {
  private static final long serialVersionUID = 3585186844010915942L;
  private ArrayList<InversePredicateModel> antonyms = null;

  protected AntonymResult() {
    // hide default constructor
  }

  public AntonymResult(ArrayList<InversePredicateModel> antonyms) {
    this.antonyms = antonyms;
  }

  public ArrayList<InversePredicateModel> getAntonyms() {
    return this.antonyms;
  }
}
