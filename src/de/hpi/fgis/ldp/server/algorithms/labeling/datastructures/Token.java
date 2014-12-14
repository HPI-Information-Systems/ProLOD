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

package de.hpi.fgis.ldp.server.algorithms.labeling.datastructures;

/**
 * 
 * Class for representing single tokens. It stores the string value and the position of the word in
 * the original text.
 * 
 * @author david.sonnabend
 */
public class Token {
  private final String value;
  private final int index;

  /**
   * The constructor initializes the token.
   * 
   * @param value The string represents the token.
   * @param index The token position in the original string.
   */
  public Token(String value, int index) {
    this.value = value;
    this.index = index;
  }

  @Override
  public String toString() {
    return this.value;
  }

  public int length() {
    return this.toString().length();
  }

  public int getIndex() {
    return this.index;
  }

  public boolean isEmpty() {
    return "".equals(this.value);
  }

  public boolean equals(Token t) {
    return this.value.equals(t.toString());
  }
}
