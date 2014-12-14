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

package de.hpi.fgis.ldp.server.datastructures.array;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class enables the access to 2-dimensional integer arrays (represented by 1-dimensional
 * arrays in memory).
 * 
 */
public class IntArray2D implements Serializable {
  private static final long serialVersionUID = -689510012838548576L;
  private final int[] values;
  private final int sizeOfDim1;
  private final int sizeOfDim2;

  /**
   * Create a new 2 dimensional int array, which is mapped to a one dimensional one
   * (<b>sizeOfDim1</b>*<b>sizeOfDim2</b> has to be smaller than 2^31 - 1).
   * 
   * @param sizeOfDim1 Size of 1st dimension.
   * @param sizeOfDim2 Size of 2nd dimension.
   * @param defaultValue Default value of the elements.
   * @throws IllegalArgumentException if <b>sizeOfDim1</b> or <b>sizeOfDim2</b> is less than 1.
   */
  public IntArray2D(final int sizeOfDim1, final int sizeOfDim2, final int defaultValue)
      throws IllegalArgumentException {
    if (sizeOfDim1 <= 0) {
      throw (new IllegalArgumentException(
          "Error! Illegal size of 1st dimension specified (given size: \"" + sizeOfDim1 + "\")!"));
    }
    if (sizeOfDim2 <= 0) {
      throw (new IllegalArgumentException(
          "Error! Illegal size of 2nd dimension specified (given size: \"" + sizeOfDim2 + "\")!"));
    }

    this.values = new int[sizeOfDim1 * sizeOfDim2];
    this.sizeOfDim1 = sizeOfDim1;
    this.sizeOfDim2 = sizeOfDim2;

    // if defaultValue equals 0, no changes (default of int is 0)
    if (defaultValue != 0) {
      Arrays.fill(this.values, defaultValue);
    }
  }

  /**
   * Get the value at the given position.
   * 
   * @param indexDim1 index in 1st dimension.
   * @param indexDim2 index in 2nd dimension.
   * @return Value of the element at the given position.
   * @throws IndexOutOfBoundsException if one of the given indices is out of bound.
   */
  public int getValue(final int indexDim1, final int indexDim2) throws IndexOutOfBoundsException {
    // // range check -> performance problem?
    // if(this.sizeOfDim1>=indexDim1||0>indexDim1)
    // throw(new IndexOutOfBoundsException("..."));
    // if(this.sizeOfDim2>=indexDim2||0>indexDim2)
    // throw(new IndexOutOfBoundsException("..."));
    return this.values[indexDim1 * this.sizeOfDim2 + indexDim2];
  }

  /**
   * Set the value at the given position.
   * 
   * @param indexDim1 index in 1st dimension.
   * @param indexDim2 index in 2nd dimension.
   * @param value New value for the element at the given position.
   * @throws IndexOutOfBoundsException if one of the given indices is out of bound.
   */
  public synchronized void setValue(final int indexDim1, final int indexDim2, final int value)
      throws IndexOutOfBoundsException {
    // // range check -> performance problem?
    // if(this.sizeOfDim1>=indexDim1||0>indexDim1)
    // throw(new IndexOutOfBoundsException("..."));
    // if(this.sizeOfDim2>=indexDim2||0>indexDim2)
    // throw(new IndexOutOfBoundsException("..."));
    this.values[indexDim1 * this.sizeOfDim2 + indexDim2] = value;
  }

  /**
   * Get the Size of the 1st dimension.
   * 
   * @return Size of the 1st dimension.
   */
  public int sizeOfDim1() {
    return this.sizeOfDim1;
  }

  /**
   * Get the Size of the 2nd dimension.
   * 
   * @return Size of the 2nd dimension.
   */
  public int sizeOfDim2() {
    return this.sizeOfDim2;
  }

  /**
   * ======================================== sort methods -> java.util.Arrays
   * 
   * @author Josh Bloch
   * @author Neal Gafter
   * @author John Rose
   * @version 1.71, 04/21/06
   * @since 1.2 ========================================
   */

  /**
   * sort the array by the items in the column <code>indexDim2</code>
   * 
   * @param indexDim2 the index of the column to be sorted
   */
  public void sortDim1(final int indexDim2) {
    this.sort1(0, this.sizeOfDim1, indexDim2);
  }

  /**
   * search in the <code>key</code> in the column <code>indexDim2</code> and get it's index.<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   * 
   * @param key the key to get index for
   * @param indexDim2 the index of the column
   * @return the index of the row
   */
  public int binarySearchDim1(final int key, final int indexDim2) {
    return this.binarySearch0(0, this.sizeOfDim1, key, indexDim2);
  }

  // Like public version, but without range checks.

  /**
   * search in the <code>key</code> in the column <code>indexDim2</code> and get it's index.<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   * 
   * @param fromIndex the smallest possible index
   * @param toIndex the biggest (exclusive) possible index
   * @param key the key to get index for
   * @param indexDim2 the index of the column
   * @return the index of the row
   */
  private int binarySearch0(final int fromIndex, final int toIndex, final int key,
      final int indexDim2) {
    int low = fromIndex;
    int high = toIndex - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      int midVal = this.getValue(mid, indexDim2);

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      }
      else {
        return mid; // key found
      }
    }
    return -(low + 1); // key not found.
  }

  /**
   * Sorts the specified sub-array of integers into ascending order.<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   */
  private void sort1(final int off, final int len, final int indexDim2) {
    // Insertion sort on smallest arrays
    if (len < 7) {
      for (int i = off; i < len + off; i++) {
        for (int j = i; j > off && this.getValue(j - 1, indexDim2) > this.getValue(j, indexDim2); j--) {
          this.swap(j, j - 1);
        }
      }
      return;
    }

    // Choose a partition element, v
    int m = off + (len >> 1); // Small arrays, middle element
    if (len > 7) {
      int l = off;
      int n = off + len - 1;
      if (len > 40) { // Big arrays, pseudomedian of 9
        int s = len / 8;
        l = this.med3(l, l + s, l + 2 * s, indexDim2);
        m = this.med3(m - s, m, m + s, indexDim2);
        n = this.med3(n - 2 * s, n - s, n, indexDim2);
      }
      m = this.med3(l, m, n, indexDim2); // Mid-size, med of 3
    }
    int v = this.getValue(m, indexDim2);

    // Establish Invariant: v* (<v)* (>v)* v*
    int a = off, b = a, c = off + len - 1, d = c;
    while (true) {
      while (b <= c && this.getValue(b, indexDim2) <= v) {
        if (this.getValue(b, indexDim2) == v) {
          this.swap(a++, b);
        }
        b++;
      }
      while (c >= b && this.getValue(c, indexDim2) >= v) {
        if (this.getValue(c, indexDim2) == v) {
          this.swap(c, d--);
        }
        c--;
      }
      if (b > c) {
        break;
      }
      this.swap(b++, c--);
    }

    // Swap partition elements back to middle
    int s, n = off + len;
    s = Math.min(a - off, b - a);
    this.vecswap(off, b - s, s);
    s = Math.min(d - c, n - d - 1);
    this.vecswap(b, n - s, s);

    // Recursively sort non-partition-elements
    if ((s = b - a) > 1) {
      this.sort1(off, s, indexDim2);
    }
    if ((s = d - c) > 1) {
      this.sort1(n - s, s, indexDim2);
    }
  }

  /**
   * Swaps x[a] with x[b].<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   */
  private void swap(final int a, final int b) {
    for (int indexDim2 = 0; indexDim2 < this.sizeOfDim2; indexDim2++) {
      final int t = this.getValue(a, indexDim2);
      this.setValue(a, indexDim2, this.getValue(b, indexDim2));
      this.setValue(b, indexDim2, t);
    }
  }

  /**
   * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   */
  private void vecswap(int a, int b, final int n) {
    for (int i = 0; i < n; i++, a++, b++) {
      this.swap(a, b);
    }
  }

  /**
   * Returns the index of the median of the three indexed integers.<br/>
   * <br/>
   * inspired by: sort methods of java.util.Arrays <br/>
   * author Josh Bloch, Neal Gafter and John Rose <br/>
   * version 1.71, 04/21/06 <br/>
   */
  private int med3(final int a, final int b, final int c, final int indexDim2) {
    return (this.getValue(a, indexDim2) < this.getValue(b, indexDim2) ? (this
        .getValue(b, indexDim2) < this.getValue(c, indexDim2) ? b
        : this.getValue(a, indexDim2) < this.getValue(c, indexDim2) ? c : a) : (this.getValue(b,
        indexDim2) > this.getValue(c, indexDim2) ? b : this.getValue(a, indexDim2) > this.getValue(
        c, indexDim2) ? c : a));
  }

}
