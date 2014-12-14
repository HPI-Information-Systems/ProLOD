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

package de.hpi.fgis.ldp.shared.event;

import java.util.ArrayList;

import de.hpi.fgis.ldp.shared.data.Predicate;

public abstract class AbstractPredicateDataTableReceivedEvent<E extends AbstractPredicateDataTableReceivedEvent<?>>
    extends AbstractDataTableReceivedEvent<E> {
  private ArrayList<Predicate> predicates;

  public void setPredicates(ArrayList<Predicate> predicates) {
    this.predicates = predicates;
  }

  public ArrayList<Predicate> getPredicates() {
    return predicates;
  }
}
