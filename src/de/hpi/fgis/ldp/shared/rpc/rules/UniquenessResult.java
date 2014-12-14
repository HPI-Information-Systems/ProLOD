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

import de.hpi.fgis.ldp.shared.data.UniquenessModel;
import de.hpi.fgis.ldp.shared.rpc.CachableResult;

public class UniquenessResult implements CachableResult, IsSerializable {
  private static final long serialVersionUID = 7585186844010915942L;
  private ArrayList<UniquenessModel> uniqueness = null;

  protected UniquenessResult() {
    // hide default constructor
  }

  public UniquenessResult(ArrayList<UniquenessModel> result) {
    this.uniqueness = result;
  }

  public ArrayList<UniquenessModel> getUniqueness() {
    return this.uniqueness;
  }
}
