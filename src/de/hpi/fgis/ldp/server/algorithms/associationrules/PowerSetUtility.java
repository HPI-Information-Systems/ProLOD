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

package de.hpi.fgis.ldp.server.algorithms.associationrules;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This Class provides an algorithm for generating the Powerset for a given set
 * 
 * @author ziawasch.abedjan
 * 
 */
public class PowerSetUtility {
  private final TIntList base;
  private final HashSet<TIntSet> power = new HashSet<TIntSet>();

  public PowerSetUtility(TIntList itemSet) {
    this.base = itemSet;
    this.computeSubSets();
  }

  /**
   * Constructor. Starts computing subsets
   * 
   * @param itemSet
   */
  public PowerSetUtility(int[] itemSet) {
    this.base = new TIntArrayList();
    for (int element : itemSet) {
      this.base.add(element);
    }
    this.computeSubSets();
  }

  /**
   * Computes all subsets recursive and adds them to the powerset
   */
  private void computeSubSets() {
    for (int i = 0; i < this.base.size(); i++) {
      TIntSet v = new TIntHashSet();
      v.add(this.base.get(i));
      this.power.add(v);
    }
    this.getSubsets(1);
  }

  /**
   * gets Subsets of a given Size
   * 
   * @param i subset size
   */
  private void getSubsets(int i) {
    if (i < 1) {
      return;
    } else if (i > 1) {
      if (i > this.base.size()) {
        return;
      }
      this.generateHigherOrderSubSets(i);
    }
    this.getSubsets(++i);
  }

  /**
   * Generates Subsets that are bigger than the given size
   * 
   * @param n subset size
   */
  private void generateHigherOrderSubSets(int n) {
    List<TIntSet> list1 = PowerSetUtility.getList(this.power, 1);
    List<TIntSet> list2 = PowerSetUtility.getList(this.power, n - 1);
    for (int x = 0; x < list2.size(); x++) {
      for (int y = 0; y < list1.size(); y++) {
        TIntSet s = new TIntHashSet();
        if (!this.getListOfObjectsFromASetContainedInAList(list2, x).contains(
            this.getListOfObjectsFromASetContainedInAList(list1, y).get(0))) {
          TIntList l1 = this.getListOfObjectsFromASetContainedInAList(list2, x);
          l1.add(this.getListOfObjectsFromASetContainedInAList(list1, y).get(0));
          for (int z = 0; z < l1.size(); z++) {
            s.add(l1.get(z));
          }
          if (!this.power.contains(s)) {
            this.power.add(s);
          }
        }
      }
    }
  }

  /**
   * meta method for casting found subset lists into a list of Objects
   * 
   * @param list2
   * @param x
   * @return
   */
  private TIntList getListOfObjectsFromASetContainedInAList(List<TIntSet> list2, int x) {
    int[] localObjectArray = (list2.get(x)).toArray();
    TIntList l = new TIntArrayList();

    for (int element : localObjectArray) {
      l.add(element);
    }
    return l;
  }

  /**
   * Gets a list with all sets of a given Size
   * 
   * @param power2
   * @param size
   * @return
   */
  private static List<TIntSet> getList(HashSet<TIntSet> power2, int size) {
    List<TIntSet> list = new ArrayList<TIntSet>();
    for (TIntSet s : power2) {
      if (s.size() == size) {
        list.add(s);
      }
    }
    return list;
  }

  /**
   * This method returns the current powerset
   * 
   * @return
   */
  public HashSet<TIntSet> getPowerSet() {
    return this.power;
  }
}
