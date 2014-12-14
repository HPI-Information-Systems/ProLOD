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

package de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList;
import de.hpi.fgis.ldp.server.datastructures.impl.PrunedEntitySchemaList;

/**
 * a {@link IBasketList} implementation which base upon another {@link IBasketList} instance (e.g.
 * after pruning)
 * 
 */
public class PrunedBasketList implements IBasketList {
  private static final long serialVersionUID = 6986073734792351332L;

  private final IBasketList internalList;
  private final int[] mapping;
  private final boolean[] usedBaskets;

  /**
   * create a new {@link PrunedEntitySchemaList}.
   * 
   * @param internalList the {@link IBasketList} on which this instance is based on
   * @param newBasketIndices the indices of the item sets of the new instance within the
   *        <code>internalList</code> to
   */
  public PrunedBasketList(final IBasketList internalList, final int[] newBasketIndices) {
    this.internalList = internalList;
    this.mapping = newBasketIndices;
    final int oldSize = internalList.getBasketCount();
    final int newSize = newBasketIndices.length;

    for (int basketIndex = 0; basketIndex < newSize; basketIndex++) {
      if (0 > newBasketIndices[basketIndex] || newBasketIndices[basketIndex] >= oldSize) {
        throw (new IllegalArgumentException("Unexpected basketIndex "
            + newBasketIndices[basketIndex] + "!"));
      }
    }

    this.usedBaskets = new boolean[this.getBasketCount()];
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getBasketID(int)
   */
  @Override
  public int getBasketID(final int basketIndex) {
    return this.internalList.getBasketID(this.mapping[basketIndex]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getBasketCount()
   */
  @Override
  public int getBasketCount() {
    return this.mapping.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getItems(int)
   */
  @Override
  public int[] getItems(final int basketIndex) {
    return this.internalList.getItems(this.mapping[basketIndex]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList #prune()
   */
  @Override
  public IBasketList prune() {
    final int basketCount = this.getBasketCount();
    int newBasketListSize = 0;
    for (int basketIndex = 0; basketIndex < basketCount; basketIndex++) {
      if (this.usedBaskets[basketIndex]) {
        newBasketListSize++;
      }
    }

    final int[] newBasketIndices = new int[newBasketListSize];
    int newBasketIndex = 0;
    for (int basketIndex = 0; basketIndex < basketCount; basketIndex++) {
      if (this.usedBaskets[basketIndex]) {
        newBasketIndices[newBasketIndex++] = this.mapping[basketIndex];
      }
    }

    return new PrunedBasketList(this.internalList, newBasketIndices);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #setUsedBasket(int)
   */
  @Override
  public void setUsedBasket(final int basketIndex) {
    this.usedBaskets[basketIndex] = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #containsAll(int, int[])
   */
  @Override
  public boolean containsAll(final int basketIndex, final int[] items) {
    return this.internalList.containsAll(this.mapping[basketIndex], items);
  }
}
