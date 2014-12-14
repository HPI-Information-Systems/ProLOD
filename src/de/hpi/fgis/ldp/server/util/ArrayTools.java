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

package de.hpi.fgis.ldp.server.util;

import java.util.Arrays;

/**
 * contains helpful tools for arrays
 */
public class ArrayTools {

  /**
   * sorts the array and removes doubled elements
   * 
   * @param original the original list (will be sorted)
   * @return the sorted and trimed list
   */
  public static int[] sortAndTrim(final int[] original) {
    // make copy before sorting
    int[] copy = new int[original.length];
    System.arraycopy(original, 0, copy, 0, original.length);
    Arrays.sort(original);
    int valueCount = 0;
    int lastValue = Integer.MIN_VALUE;
    for (int index = 0; index < original.length; index++) {
      if (lastValue != original[index]) {
        valueCount++;
        lastValue = original[index];
      }
    }

    if (valueCount >= original.length) {
      return original;
    }

    final int[] newResult = new int[valueCount];
    lastValue = Integer.MIN_VALUE;
    int newIndex = 0;
    for (int index = 0; index < original.length; index++) {
      if (lastValue != original[index]) {
        newResult[newIndex] = original[index];
        lastValue = original[index];
        newIndex++;
      }
    }

    return newResult;
  }

  /**
   * compares a subset of a array with another array
   * 
   * @param a array do use for subset comparison
   * @param a2 second array (should be equal to the subset of the first array)
   * @param startIndex start index of the subset in the first array
   * @param length length of the subset of the first array
   * @return <code>true</code>, if <code>a2</code> equals the subset of <code>a</code>, otherwise
   *         <code>false</code>.
   */
  public static boolean equals(final int[] a, final int[] a2, final int startIndex, final int length) {
    if (a == a2) {
      return true;
    }
    if (a == null || a2 == null) {
      return false;
    }

    if (a.length < startIndex + length) {
      return false;
    }

    if (a2.length < startIndex + length) {
      return false;
    }

    for (int i = startIndex; i < length; i++) {
      if (a[i] != a2[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * checks that all elements of <code>sortedChild</code> is a subset of the specified part of
   * <code>sortedParent</code>.
   * 
   * @param sortedParent the parent array do use for subset search
   * @param fromIndex the start index of the subset in <code>sortedParent</code>.
   * @param toIndex the (exclusive) end index of the subset in <code>sortedParent</code>.
   * @param sortedChild the child set which should be a subset of the specified
   *        <code>sortedParent</code> subset.
   * @return <code>true</code>, if all elements of <code>sortedChild</code> are part of the subset
   *         of <code>sortedParent</code>, otherwise <code>false</code>.
   */
  public static boolean isSubSet(final int[] sortedParent, final int fromIndex, final int toIndex,
      final int[] sortedChild) {

    final int numOfChildren = sortedChild.length;
    final int numOfParents = toIndex - fromIndex;
    if (numOfParents < numOfChildren) {
      return false;
    }

    int newFromIndex = fromIndex;
    for (int childIndex = 0; childIndex < numOfChildren; childIndex++) {
      final int currentItem = sortedChild[childIndex];

      // search for the current item and store it as the new start index
      // in the <code>sortedParent</code> subset (the next child will be
      // bigger because <code>sortedChild</code> is sorted)
      newFromIndex = Arrays.binarySearch(sortedParent, newFromIndex, toIndex, currentItem);
      if (newFromIndex < 0) {
        // (numOfParents-parentIndex)<numOfhildrenToFind)
        return false;
      }
    }
    return true;
  }
}
