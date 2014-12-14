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

package de.hpi.fgis.ldp.server.algorithms.labeling;

import java.util.ArrayList;
import java.util.List;

import de.hpi.fgis.ldp.server.algorithms.labeling.datastructures.Token;

/**
 * This class provides some static methods to tokenize strings.
 * 
 * @author david.sonnabend
 */
public class StringTokenizer {
  private final static StringTokenizer INSTANCE = new StringTokenizer();

  /**
   * gets a {@link StringTokenizer} instance.
   * 
   * @return a {@link StringTokenizer} instance.
   */
  public static StringTokenizer getInstance() {
    return StringTokenizer.INSTANCE;
  }

  private StringTokenizer() {
    // hide default constructor
  }

  /**
   * Tokenizes the given string. The whitespace character is used as delimiter.
   * 
   * @param string The string to tokenize
   * @return List of @see Token
   */
  public List<Token> tokenize(String string) {
    return this.tokenize(string, false);
  }

  /**
   * Tokenizes the given string. The whitespace character is used as delimiter.
   * 
   * @param string The string to tokenize
   * @param ignoreCase Determines if the resulted tokens are case sensitive or not
   * @return List of @see Token
   */
  public List<Token> tokenize(String string, boolean ignoreCase) {
    return this.tokenize(string, ignoreCase, " ");
  }

  /**
   * Tokenizes the given string.
   * 
   * @param string The string to tokenize
   * @param ignoreCase Determines if the resulted tokens are case sensitive or not
   * @param delimeterRegex The delimiting regular expression
   * @return List of @see Token
   */
  public List<Token> tokenize(String string, boolean ignoreCase, String delimeterRegex) {
    List<Token> tokenList = new ArrayList<Token>();
    if (ignoreCase) {
      string = string.toLowerCase();
    }

    String[] stringParts = string.split(delimeterRegex);
    for (int i = 0; i < stringParts.length; ++i) {
      tokenList.add(new Token(stringParts[i], i));
    }

    return tokenList;
  }

  /**
   * Split a token into nGrams.
   * 
   * @param token The token to split.
   * @param nGramSize The length of the subtokens.
   * @return List of @see Token
   */
  public List<Token> nGramTokenize(Token token, int nGramSize) {
    return this.nGramTokenize(token.toString(), nGramSize, false);
  }

  /**
   * Tokenizes the given string into nGrams.
   * 
   * @param string The string to tokenize.
   * @param nGramSize The length of the tokens.
   * @param ignoreWhiteSpace Determines if whitespaces should be irgnored.
   * @return List of @see Token
   */
  public List<Token> nGramTokenize(String string, int nGramSize, boolean ignoreWhiteSpace) {
    return this.nGramTokenize(string, nGramSize, ignoreWhiteSpace, false);
  }

  /**
   * Tokenizes the given string into nGrams.
   * 
   * @param string The string to tokenize.
   * @param nGramSize The length of the tokens.
   * @param ignoreWhiteSpace Determines if whitespaces should be irgnored.
   * @param ignoreCase Determines if the case should be ignored - if true, all letters will be
   *        transformed to lower case.
   * @return List of @see Token
   */
  public List<Token> nGramTokenize(String string, int nGramSize, boolean ignoreWhiteSpace,
      boolean ignoreCase) {
    List<Token> nGrams = new ArrayList<Token>();
    if (ignoreCase) {
      string = string.toLowerCase();
    }
    if (ignoreWhiteSpace) {
      string = string.replaceAll(" ", "");
    }

    if (string.length() < nGramSize) {
      nGrams.add(new Token(string, 0));
    }
    for (int startPos = 0; startPos <= (string.length() - nGramSize); ++startPos) {
      String nGram = string.substring(startPos, startPos + nGramSize);
      nGrams.add(new Token(nGram, startPos));
    }

    return nGrams;
  }

}
