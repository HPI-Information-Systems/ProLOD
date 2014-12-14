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
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;

/**
 * Implementation of {@link IBasketList} which stores the data of all baskets
 * 
 */
public class BasketList implements IBasketList {
  private static final long serialVersionUID = -149948892873203853L;

  private final IEntitySchemaList entitySchemaList;
  private final boolean[] usedBaskets;

  /**
   * Create a new {@link BasketList} instance.
   * 
   * @param entitySchemaList the {@link IEntitySchemaList} instance on which this BasketList will be
   *        based on.
   */
  public BasketList(final IEntitySchemaList entitySchemaList) {
    this.entitySchemaList = entitySchemaList;

    this.usedBaskets = new boolean[this.getBasketCount()];

    // not necessary -> false is default value
    // Arrays.fill(this.usedBaskets, false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getBasketID(int)
   */
  @Override
  public int getBasketID(final int basketIndex) {
    return this.entitySchemaList.getEntityID(basketIndex);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getBasketCount()
   */
  @Override
  public int getBasketCount() {
    return this.entitySchemaList.getEntityCount();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList
   * #getItems(int)
   */
  @Override
  public int[] getItems(final int basketIndex) {
    return this.entitySchemaList.getSchema(basketIndex);
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
        newBasketIndices[newBasketIndex++] = basketIndex;
      }
    }

    return new PrunedBasketList(this, newBasketIndices);
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
  public boolean containsAll(final int basketIndex, final int[] subset) {
    return this.entitySchemaList.containsAll(basketIndex, subset);
  }
}
