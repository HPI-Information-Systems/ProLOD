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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class represents a extended character class. There is a character set which extends the
 * member space.
 * 
 */
public class ExtensibleCharacterClass extends CharacterClass {

  /**
   * HashSet which contains the extended members.
   */
  private final Set<Character> members = new HashSet<Character>();

  protected ExtensibleCharacterClass(char placeholder) {
    super(placeholder);
  }

  protected ExtensibleCharacterClass(char placeholder, String regularExpression) {
    super(placeholder, regularExpression);
  }

  protected ExtensibleCharacterClass(char placeholder, Collection<Character> characters) {
    super(placeholder);
    addMemberCollection(characters);
  }

  protected ExtensibleCharacterClass(char placeholder, String regularExpression,
      Collection<Character> characters) {
    super(placeholder, regularExpression);
    addMemberCollection(characters);
  }

  /**
   * Checks if a character is in the member set or if the character matches the regular expression.
   */
  @Override
  protected boolean isMember(char character) {
    return this.members.contains(Character.valueOf(character))
        || Pattern.matches(this.regularExpression, Character.toString(character));
  }

  /**
   * Adds a new character to this character class.
   * 
   * @param character
   */
  protected void addMember(char character) {
    this.members.add(Character.valueOf(character));
  }

  /**
   * Adds a Collection of characters to this character class.
   * 
   * @param characters
   */
  protected void addMemberCollection(Collection<Character> characters) {
    this.members.addAll(characters);
  }

  /**
   * 
   * @return the member collection with all characters which extends this character class.
   */
  protected Set<Character> getAllMembers() {
    return this.members;
  }
}
