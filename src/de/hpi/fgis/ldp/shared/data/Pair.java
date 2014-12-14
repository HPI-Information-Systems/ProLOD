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

package de.hpi.fgis.ldp.shared.data;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * this class represents a pair of two generic instances
 * 
 * @author matthias.pohl
 * 
 * @param <F> the type of the first element
 * @param <S> the type of the second element
 */
public class Pair<F, S> implements IsSerializable {

  private F firstElem;
  private S secondElem;

  /**
   * Creates a new pair w/o initial elements
   */
  public Pair() {
    this(null, null);
  }

  /**
   * Creates a new pair with initial elements
   * 
   * @param first the initial value of the first element of the pair
   * @param second the initial value of the second element of the pair
   */
  public Pair(F first, S second) {
    this.setFirstElem(first);
    this.setSecondElem(second);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Pair[" + this.getFirstElem() + ", " + this.getSecondElem() + "]";
  }

  /**
   * Gets the first element of the pair.
   * 
   * @return the first element of the pair.
   */
  public F getFirstElem() {
    return this.firstElem;
  }

  /**
   * sets the first element of the pair
   * 
   * @param firstElem the first element of the pair
   */
  public void setFirstElem(F firstElem) {
    this.firstElem = firstElem;
  }

  /**
   * gets the second element of the pair
   * 
   * @return the second element of the pair
   */
  public S getSecondElem() {
    return this.secondElem;
  }

  /**
   * sets the second element of the pair
   * 
   * @param secondElem the second element of the pair
   */
  public void setSecondElem(S secondElem) {
    this.secondElem = secondElem;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.firstElem == null) ? 0 : this.firstElem.hashCode());
    result = prime * result + ((this.secondElem == null) ? 0 : this.secondElem.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(java.lang.Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    Pair<?, ?> other = (Pair<?, ?>) obj;
    if (this.firstElem == null) {
      if (other.firstElem != null) {
        return false;
      }
    } else if (!this.firstElem.equals(other.firstElem)) {
      return false;
    }
    if (this.secondElem == null) {
      if (other.secondElem != null) {
        return false;
      }
    } else if (!this.secondElem.equals(other.secondElem)) {
      return false;
    }
    return true;
  }
}
