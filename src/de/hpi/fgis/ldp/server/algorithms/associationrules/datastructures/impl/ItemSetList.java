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

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.datastructures.array.IntArray2D;
import de.hpi.fgis.ldp.server.util.ArrayTools;

/**
 * a {@link IItemSetList} implementation
 * 
 */
public class ItemSetList implements IItemSetList {
  private static final long serialVersionUID = 3152222522713429763L;
  /**
   * Represents the data of the Item set in a 2D Array<br/>
   * 1st dimension: item set<br/>
   * 2nd dimension: element of set
   */
  private final IntArray2D data;
  private final int itemSetSize;
  private final int supportIndex;

  /**
   * create a new {@link ItemSetList} instance
   * 
   * @param setSize the amount of items within any set
   * @param setCount the amount of item sets in the list
   */
  public ItemSetList(final int setSize, final int setCount) {
    this.data = new IntArray2D(setCount, setSize + 1, Integer.MIN_VALUE);
    this.itemSetSize = this.data.sizeOfDim2() - 1;
    this.supportIndex = this.data.sizeOfDim2() - 1;
  }

  /**
   * set the values of a item set
   * 
   * @param itemSetIndex the index of the item set to set
   * @param itemIDs the id's in the item set
   */
  public void setItemSet(final int itemSetIndex, final int... itemIDs) {
    if (itemIDs.length != this.itemSetSize) {
      throw (new IllegalArgumentException("Illegal size of item set (expected: " + this.itemSetSize
          + " given: " + itemIDs.length + ")"));
    }

    // TODO feature of IntArray2D -> using arraycopy
    for (int itemIndex = 0; itemIndex < this.itemSetSize; itemIndex++) {
      this.data.setValue(itemSetIndex, itemIndex, itemIDs[itemIndex]);
    }

    this.data.setValue(itemSetIndex, this.supportIndex, 0);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#getItemSet(int)
   */
  @Override
  public int[] getItemSet(final int itemSetIndex) {
    // TODO feature of IntArray2D -> using arraycopy
    final int[] items = new int[this.itemSetSize];
    for (int itemIndex = 0; itemIndex < this.itemSetSize; itemIndex++) {
      items[itemIndex] = this.data.getValue(itemSetIndex, itemIndex);
    }

    return items;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#getItemSetCount()
   */
  @Override
  public int getItemSetCount() {
    return this.data.sizeOfDim1();
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#getItemSetSize()
   */
  @Override
  public int getItemSetSize() {
    return this.itemSetSize;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#getFrequency(int)
   */
  @Override
  public int getFrequency(final int itemSetIndex) {
    return this.data.getValue(itemSetIndex, this.supportIndex);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#increaseFrequency(int)
   */
  @Override
  public void increaseFrequency(final int itemSetIndex) {
    this.data.setValue(itemSetIndex, this.supportIndex,
        this.data.getValue(itemSetIndex, this.supportIndex) + 1);
  }

  /**
   * set the usage frequency of a item set
   * 
   * @param itemSetIndex the index of the item set
   * @param value the frequency to set
   */
  public void setFrequency(final int itemSetIndex, final int value) {
    this.data.setValue(itemSetIndex, this.supportIndex, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#generateCandidates()
   */
  @Override
  public IItemSetList generateCandidates() {
    ItemSetList allCandidates =
        new ItemSetList(this.getItemSetSize() + 1, this.getItemSetCount() * this.getItemSetCount());

    final int itemSetCount = this.getItemSetCount();
    final int intersectSize = this.getItemSetSize() - 1;
    int newItemSetIndex = 0;
    for (int firstItemSetIndex = 0; firstItemSetIndex < itemSetCount; firstItemSetIndex++) {
      final int[] firstList = this.getItemSet(firstItemSetIndex);
      for (int secondItemSetIndex = firstItemSetIndex + 1; secondItemSetIndex < itemSetCount; secondItemSetIndex++) {

        final int[] secondList = this.getItemSet(secondItemSetIndex);
        if (ArrayTools.equals(firstList, secondList, 0, intersectSize)) {
          final int[] newItems = new int[firstList.length + 1];
          if (firstList[intersectSize] < secondList[intersectSize]) {
            System.arraycopy(firstList, 0, newItems, 0, firstList.length);
            newItems[firstList.length] = secondList[intersectSize];
          } else {
            System.arraycopy(secondList, 0, newItems, 0, secondList.length);
            newItems[secondList.length] = firstList[intersectSize];
          }
          allCandidates.setItemSet(newItemSetIndex++, newItems);
        }
      }
    }
    // no items found
    if (newItemSetIndex == 0) {
      return null;
    }

    ItemSetList candidates = new ItemSetList(allCandidates.getItemSetSize(), newItemSetIndex);

    for (int itemSetIndex = 0; itemSetIndex < newItemSetIndex; itemSetIndex++) {
      candidates.setItemSet(itemSetIndex, allCandidates.getItemSet(itemSetIndex));
    }

    return candidates;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures. IItemSetList#prune(int)
   */
  @Override
  public IItemSetList prune(int minFrequency) {
    int countBigItemSets = 0;

    for (int itemSetIndex = 0; itemSetIndex < this.getItemSetCount(); itemSetIndex++) {
      if (this.getFrequency(itemSetIndex) >= minFrequency) {
        countBigItemSets++;
      }
    }

    // no items found
    if (countBigItemSets == 0) {
      return null;
    }

    ItemSetList prunedList = new ItemSetList(this.getItemSetSize(), countBigItemSets);

    int newItemSetIndex = 0;
    for (int itemSetIndex = 0; itemSetIndex < this.getItemSetCount(); itemSetIndex++) {
      final int frequency = this.getFrequency(itemSetIndex);
      if (frequency >= minFrequency) {
        prunedList.setItemSet(newItemSetIndex, this.getItemSet(itemSetIndex));
        prunedList.setFrequency(newItemSetIndex++, frequency);
      }
    }

    return prunedList;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.
   * IItemSetList#getSmallItemSets(int)
   */
  @Override
  public IItemSetList getSmallItemSets(int maxFrequency) {
    int countSmallItemSets = 0;

    for (int itemSetIndex = 0; itemSetIndex < this.getItemSetCount(); itemSetIndex++) {
      if (this.getFrequency(itemSetIndex) < maxFrequency) {
        countSmallItemSets++;
      }
    }

    // no items found
    if (countSmallItemSets == 0) {
      return null;
    }

    ItemSetList prunedList = new ItemSetList(this.getItemSetSize(), countSmallItemSets);

    int newItemSetIndex = 0;
    for (int itemSetIndex = 0; itemSetIndex < this.getItemSetCount(); itemSetIndex++) {
      final int frequency = this.getFrequency(itemSetIndex);
      if (frequency < maxFrequency) {
        prunedList.setItemSet(newItemSetIndex, this.getItemSet(itemSetIndex));
        prunedList.setFrequency(newItemSetIndex++, frequency);
      }
    }

    return prunedList;
  }
}
