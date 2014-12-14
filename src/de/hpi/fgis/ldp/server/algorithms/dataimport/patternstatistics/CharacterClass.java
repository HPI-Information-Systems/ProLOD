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

package de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics;

import java.util.regex.Pattern;

/**
 * This class represents a character class.
 * 
 */
public class CharacterClass {

  /**
   * The place holder character of this character class.
   */
  protected char placeholder;

  /**
   * Regular expression to identify a character class member.
   */
  protected String regularExpression;

  protected CharacterClass(char placeholder) {
    this.placeholder = placeholder;
    this.regularExpression = "";
  }

  protected CharacterClass(char placeholder, String regularExpression) {
    this.placeholder = placeholder;
    this.regularExpression = regularExpression;
  }

  protected void setRegularExpression(String regularExpression) {
    this.regularExpression = regularExpression;
  }

  protected String getRegularExpression() {
    return this.regularExpression;
  }

  /**
   * checks whether a character is member of this character class
   * 
   * @param character
   * @return
   */
  protected boolean isMember(char character) {
    return Pattern.matches(this.regularExpression, Character.toString(character));
  }

  protected char getPlaceholder() {
    return this.placeholder;
  }

}
