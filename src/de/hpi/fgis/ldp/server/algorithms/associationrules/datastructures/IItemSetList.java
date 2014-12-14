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
 * Encapsulates information of different Itemsets with the same Size.
 * 
 */
public interface IItemSetList extends Serializable, Cloneable {

  /**
   * The size of all item sets in this {@link IItemSetList}.
   * 
   * @return the size of all item sets.
   */
  public int getItemSetSize();

  /**
   * The number of item sets in this {@link IItemSetList}.
   * 
   * @return the number of item sets.
   */
  public int getItemSetCount();

  /**
   * Sorted list of Items in the selected set.
   * 
   * @param itemSetIndex index of item set to get items for.
   * @return distinct and sorted list of Items.
   */
  public int[] getItemSet(final int itemSetIndex);

  /**
   * Increase the support of the selected set.
   * 
   * @param itemSetIndex index of item set
   */
  public void increaseFrequency(final int itemSetIndex);

  /**
   * Get the support of the selected set.
   * 
   * @param itemSetIndex index of item set
   * @return the support of the set of items
   */
  public int getFrequency(final int itemSetIndex);

  /**
   * prune the list
   * 
   * @param minFrequency minimal support for potential parents in this set
   * @return pruned list
   */
  public IItemSetList prune(final int minFrequency);

  /**
   * small item sets the list (these instances will be ignored during the prune step)
   * 
   * @param maxFrequency maximal support for potential parents in this set
   * @return pruned list
   */
  public IItemSetList getSmallItemSets(final int maxFrequency);

  /**
   * Generate Candidates with {@link IItemSetList}.getItemSetSize()+1
   * 
   * @return new candidates
   */
  public IItemSetList generateCandidates();
}
