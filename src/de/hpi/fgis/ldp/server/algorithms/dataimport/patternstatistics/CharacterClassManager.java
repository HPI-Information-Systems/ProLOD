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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * This class manage all character classes.
 * 
 * 
 */
public class CharacterClassManager {

  private static final char UNKNOWN_CHARACTER = '?';

  private final int minTextLength;

  @Inject
  protected CharacterClassManager(@Named("profiling.minTextLength") int minTextLength) {
    this.minTextLength = minTextLength;
  }

  /**
   * Map of keywords with the pointing to the corresponding place holder String.
   */
  private final Map<String, String> keywordMap = new HashMap<String, String>();

  /**
   * Which preconditions shall exist for the given place holder. (before MONTH -> no a-z or A-Z)
   */
  private final Map<String, String> preCondition = new HashMap<String, String>();

  /**
   * Which postconditions shall exist for the given place holder. (after MONTH -> no a-z or A-Z)
   */
  private final Map<String, String> postCondition = new HashMap<String, String>();
  private List<String> sortedKeyList;

  /**
   * List of all character classes.
   */
  private final List<CharacterClass> allCharClasses = new LinkedList<CharacterClass>();

  /**
   * Adds a list of keywords with the corresponding placeholder string to the CharacterClassManager
   * object
   * 
   * @param placeholder String which is used in the pattern
   * @param keywordList List of keywords that will be replaced by the placeholder String
   * @param pre Precondition (regex)
   * @param post Postcondition (regex)
   * @throws DataFormatException if a keyword is a substring of the placeholder String
   */
  public void addKeywords(String placeholder, List<String> keywordList, String pre, String post)
      throws DataFormatException {
    for (String keyword : keywordList) {

      this.keywordMap.put(keyword, placeholder);

      if (pre == null) {
        pre = "^.*$";
      }

      if (post == null) {
        post = "^.*$";
      }

      this.preCondition.put(placeholder, pre);
      this.postCondition.put(placeholder, post);

      for (String listedPlaceholder : this.keywordMap.values()) {
        if (listedPlaceholder.indexOf(keyword) > -1) {
          throw new DataFormatException(
              "It's not allowed to use keywords which are substrings of any placeholder: \nKeyword: "
                  + keyword + "\nPlaceholder: " + listedPlaceholder);
        }
      }
    }

    this.sortedKeyList = null;
  }

  public void addKeywords(String placeholder, List<String> keywordList) throws DataFormatException {
    this.addKeywords(placeholder, keywordList, null, null);
  }

  public String getPreCondition(String keyword) {
    return this.preCondition.get(this.keywordMap.get(keyword));
  }

  public String getPostCondition(String keyword) {
    return this.postCondition.get(this.keywordMap.get(keyword));
  }

  protected List<String> getKeywords() {
    if (null == this.sortedKeyList) {
      this.sortedKeyList = new LinkedList<String>(this.keywordMap.keySet());

      // keyword list has to be sorted because longer keywords has to be
      // checked first
      // example: 'January' has to be replaced before we can look for
      // 'Jan'
      Collections.sort(this.sortedKeyList, new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
          return s2.length() - s1.length();
        }
      });
    }

    return this.sortedKeyList;
  }

  protected String getFormattedPlaceholder(String keyword) {
    // encrypts the length of the given placeholder into the String so that
    // we can ignore this part in the character transformation phase
    return "[" + this.keywordMap.get(keyword).length() + "]" + this.keywordMap.get(keyword);
  }

  /**
   * Adds a new character class.
   * 
   * @param characterClass
   */
  public void addCharacterClass(CharacterClass characterClass) {
    this.allCharClasses.add(characterClass);
  }

  /**
   * Removes a character class specified on the basis of the placeholder character.
   * 
   * @param placeholder
   */
  public void removeCharacterClass(char placeholder) {
    this.allCharClasses.remove(this.getCharacterClassId(placeholder));
  }

  /**
   * Gets the character class by the given placeholder character.
   * 
   * @param placeholder
   * @return
   */
  public CharacterClass getCharacterClass(char placeholder) {
    return this.allCharClasses.get(this.getCharacterClassId(placeholder));
  }

  /**
   * Gets the placeholder character on the basis of the given character.
   * 
   * @param character
   * @return
   */
  protected char getPlaceholder(char character) {
    for (CharacterClass characterClass : this.allCharClasses) {
      if (characterClass.isMember(character)) {
        return characterClass.getPlaceholder();
      }
    }
    return CharacterClassManager.UNKNOWN_CHARACTER;
  }

  private static String replaceKeyword(String nativeString, String keyword, String placeholder,
      String preCondition, String postCondition) {
    Pattern pattern = Pattern.compile("(.*)(" + keyword + ")(.*)");

    Stack<String> postStack = new Stack<String>();
    StringBuffer buffer = new StringBuffer();
    Matcher matcher = pattern.matcher(nativeString);
    String pre = nativeString;

    while (matcher.find()) {
      pre = matcher.group(1);
      String between = matcher.group(2);
      String post = matcher.group(3);
      postStack.push(post);

      if (pre.matches(preCondition) && post.matches(postCondition)) {
        postStack.push(placeholder);
      } else {
        postStack.push(between);
      }

      matcher = pattern.matcher(pre);
    }

    buffer.append(pre);

    while (!postStack.isEmpty()) {
      buffer.append(postStack.pop());
    }

    return buffer.toString();
  }

  /**
   * 
   * @param value
   * @return
   */
  public Pair<String, String> getPattern(String value) {
    if (value == null) {
      return new Pair<String, String>("", "");
    }

    if (value.length() > this.minTextLength) {
      return new Pair<String, String>();
    }

    // looks for backslashes (first argument: regular expression -> that's
    // why the backslash is escaped)
    // four '\' represents two '\' in a String and these two '\' represents
    // one '\' in a regular expression
    // every '\' is replaced by '\\'
    value = value.replaceAll("\\\\", "\\\\\\\\");
    // every '[' is replaced by '\['
    // here the backslashes are needed for escaping the bracket in the
    // regular expression
    value = value.replaceAll("\\[", "\\\\[");

    // replace every keyword in the String by its placeholder
    for (String keyword : this.getKeywords()) {
      value =
          CharacterClassManager.replaceKeyword(value, keyword,
              this.getFormattedPlaceholder(keyword), this.getPreCondition(keyword),
              this.getPostCondition(keyword));
    }

    // is true if we found an '\' (the escape character) and the next
    // character is no control character ('\' or '['); otherwise false
    boolean nextIsEscaped = false;
    // is true if a not escaped '[' was read and is set to false when the
    // corresponding ']' is read
    boolean offsetIsRead = false;
    // stores the number of the next steps in which the character won't be
    // transformed because it is part of a placeholder
    int currentOffset = 0;
    // result String - a pattern
    StringBuffer result = new StringBuffer();
    // result String - a normalized pattern
    StringBuffer resultNormalized = new StringBuffer();
    // local variable for storing the length of a placeholder (framed by '['
    // and ']')
    StringBuffer offset = new StringBuffer();
    // saves the last appended character
    Character lastChar = null;
    for (char c : value.toCharArray()) {

      // currentOffset is greater than 0 -> the currently read characters
      // are part of a placeholder
      if (currentOffset > 0) {
        // currentOffset is decremented -> character is read
        --currentOffset;
        // ...and forwarded to the result- and the resultNormalized
        // StringBuffer
        result.append(c);
        resultNormalized.append(c);
        // ignore lastChar if a keyword-placeholder is read
        lastChar = null;
        // no special case has to be handled
        continue;
      }

      // last character was an '\' -> current character is a control
      // character ('\' or '[') that is written to the result StringBuffer
      // without special case handling
      if (nextIsEscaped) {
        // write it to result StringBuffer
        result.append(this.getPlaceholder(c));
        // checks whether the current character is a repeating one or
        // the lastChar was ignored
        // capital letters and lower letters are treated equally
        if (lastChar == null
            || !lastChar.equals(Character.valueOf(Character.toLowerCase(this.getPlaceholder(c))))) {
          resultNormalized.append(Character.valueOf(Character.toLowerCase(this.getPlaceholder(c))));
          lastChar = Character.valueOf(Character.toLowerCase(this.getPlaceholder(c)));
        }
        // disabled character escaping
        nextIsEscaped = false;
        // a '[' was read
      } else if (offsetIsRead) {
        // the corresponding ']' was read
        if (c == ']') {
          // reading the offset is finished -> offsetIsRead can be set
          // to false;
          offsetIsRead = false;
          // parse offset into an Integer
          currentOffset = Integer.parseInt(offset.toString());
          // create a new StringBuffer object (delete the old one)
          offset = new StringBuffer();
          // the corresponding ']' wasn't read, yet
        } else {
          // store the character (actually it is a digit) in the
          // offset StringBuffer
          offset.append(c);
        }
        // no character escaping is enabled and no offset read section
        // was started
      } else {
        // an escape character '\' was read
        if (c == '\\') {
          // activate escaping for the next character
          nextIsEscaped = true;
          // an opening bracket '[' (which isn't escaped) was read, so
          // an offset read section starts
        } else if (c == '[') {
          // set offsetIsRead to true -> indicates that the next
          // values are digits, which describes the length of the next
          // placeholder
          offsetIsRead = true;
          // common case: simple character was read with no special
          // context
        } else {
          // forward character to result StringBuffer
          result.append(this.getPlaceholder(c));
          // checks whether the current character is a repeating one
          // or the lastChar was ignored
          // capital letters and lower letters are treated equally
          if (lastChar == null
              || !lastChar.equals(Character.valueOf(Character.toLowerCase(this.getPlaceholder(c))))) {
            resultNormalized
                .append(Character.valueOf(Character.toLowerCase(this.getPlaceholder(c))));
            lastChar = Character.valueOf(Character.toLowerCase(this.getPlaceholder(c)));
          }
        }
      }
    }

    // return result-Pair with the pattern-String as first element und the
    // normalized pattern-String as second element
    return new Pair<String, String>(result.toString(), resultNormalized.toString());
  }

  /**
   * 
   * @param character
   * @return
   */
  public String getAllMembers(char character) {
    return this.getCharacterClass(this.getPlaceholder(character)).getRegularExpression();
  }

  /**
   * 
   * @param character
   * @return
   */
  protected int getCharacterClassId(char character) {
    int count = 0;
    for (CharacterClass charClass : this.allCharClasses) {
      if (charClass.isMember(character)) {
        return count;
      }
      count++;
    }
    return -1;
  }
}
