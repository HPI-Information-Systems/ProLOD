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

import java.io.Serializable;

/**
 * Encapsulates the predicates of all Entities of a RDF-(Sub-)Graph.
 * 
 */
public interface IBasketList extends Serializable {

  /**
   * Get the amount of baskets in this instance.
   * 
   * @return the amount of baskets
   */
  public int getBasketCount();

  /**
   * Distinct sorted list of Items in one basket (identified by <code>basketIndex</code>).<br/>
   * <br/>
   * Bsp: subject("Albert Einstein")<br/>
   * predicate("hat Kind") (objekt("Hannelore Einstein")<br/>
   * predicate("hat Kind") (objekt("Friedrich Einstein")<br/>
   * 
   * @param basketIndex Index of basket to get items for.
   * @return distinct and sorted list of Items.
   */
  public int[] getItems(final int basketIndex);

  /**
   * checks if specified basket contains all items of a given array
   * 
   * @param basketIndex the index of the basket to check
   * @param items the items to be potentially contained in the basket
   * @return <code>true</code>, if all elements of <code>items</code> are part of the subset of
   *         basket with <code>basketIndex</code>, otherwise <code>false</code>.
   */
  public boolean containsAll(final int basketIndex, final int[] items);

  /**
   * Get the id of the basket (entity)
   * 
   * @param basketIndex the index of the basket
   * @return the id of the basket (entity).
   */
  public int getBasketID(final int basketIndex);

  /**
   * Mark basket as used (this means that it will not be ignored in the next prune step/it will be
   * part of the pruned list)
   * 
   * @param basketIndex the index of the basket
   */
  public void setUsedBasket(final int basketIndex);

  /**
   * Create a new {@link IBasketList} with all "used" elements of this instance.
   * 
   * @return a new {@link IBasketList}
   */
  public IBasketList prune();
}
