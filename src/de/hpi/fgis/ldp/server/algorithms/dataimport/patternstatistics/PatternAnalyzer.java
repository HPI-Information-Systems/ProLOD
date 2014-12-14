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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * This class encapsulates the whole functionality being necessary for evaluating the object data.
 * 
 */
public class PatternAnalyzer {

  public static String urlstartpattern = null;

  // /**
  // * Database representation of an unknown datatype
  // */
  // public static final int UNKNOWN = 0; // 00000000
  //
  // /**
  // * Database representation of the OTHER datatype
  // */
  // public static final int OTHER = 1; // 00000001
  //
  // /**
  // * Database representation of the STRING datatype
  // */
  // public static final int STRING = 3; // 00000011
  //
  // /**
  // * Database representation of the TEXT datatype
  // */
  // public static final int TEXT = 7; // 000000111
  //
  // /**
  // * Database representation of the INTEGER datatype
  // */
  // public static final int INTEGER = 11; // 000001011
  //
  // /**
  // * Database representation of the FLOAT datatype
  // */
  // public static final int DECIMAL = 19; // 000010011
  //
  // /**
  // * Database representation of the DATE datatype
  // */
  // public static final int DATE = 35; // 000100011
  //
  // /**
  // * Database representation of the LINK datatype
  // */
  // public static final int LINK = 67; // 001000011
  //
  // /**
  // * Database representation of the INTERNAL_LINK datatype
  // */
  // public static final int INTERNAL_LINK = 195; // 011000011
  //
  // /**
  // * Database representation of the EMPTY datatype
  // */
  // public static final int EMPTY_VALUE = 259; // 100000011
  //
  // private static final String STR_UNKNOWN = "Unknown";
  // private static final String STR_OTHER = "Other";
  // private static final String STR_STRING = "String";
  // private static final String STR_TEXT = "Text";
  // private static final String STR_INTEGER = "Integer";
  // private static final String STR_DECIMAL = "Decimal";
  // private static final String STR_DATE = "Date";
  // private static final String STR_LINK = "External link";
  // private static final String STR_INTERNAL_LINK = "Internal link";
  // private static final String STR_EMPTY_VALUE = "Empty";

  private final CharacterClassManager characterClassManager;

  @Inject
  protected PatternAnalyzer(CharacterClassManager characterClassManager) {
    this.characterClassManager = characterClassManager;

    // sets all needed character classes
    initCharacterClassManager();
  }

  /**
   * Initializes the {@link CharacterClassManager}
   */
  private void initCharacterClassManager() {

    List<String> monthNames = new LinkedList<String>();
    monthNames.add("January");
    monthNames.add("Jan");
    monthNames.add("February");
    monthNames.add("Feb");
    monthNames.add("March");
    monthNames.add("Mar");
    monthNames.add("April");
    monthNames.add("Apr");
    monthNames.add("May");
    monthNames.add("June");
    monthNames.add("Jun");
    monthNames.add("July");
    monthNames.add("Jul");
    monthNames.add("August");
    monthNames.add("Aug");
    monthNames.add("September");
    monthNames.add("Sep");
    monthNames.add("October");
    monthNames.add("Oct");
    monthNames.add("November");
    monthNames.add("Nov");
    monthNames.add("December");
    monthNames.add("Dec");

    List<String> imageFileExtentions = new LinkedList<String>();
    imageFileExtentions.add("\\.jpg|\\.JPG|\\.png|\\.PNG|\\.gif|\\.GIF");

    List<String> specialCharacters = new LinkedList<String>();
    specialCharacters.add("%[0-9][0-9]");

    List<String> template = new LinkedList<String>();
    template.add("Template:|template:|Image:|image:");

    List<String> urlStart = new LinkedList<String>();
    urlStart.add("^[a-zA-Z]{2,6}://");

    try {
      characterClassManager
          .addKeywords("MONTH", monthNames, "^.*[^a-zA-Z]$|^$", "^[^a-zA-Z].*$|^$");
      characterClassManager.addKeywords("IMAGEFILEEXT", imageFileExtentions);
      characterClassManager.addKeywords("UEC", specialCharacters);
      characterClassManager.addKeywords("TEMPLATE", template, "^$", null);
      characterClassManager.addKeywords("URLSTART", urlStart);
    } catch (DataFormatException e) {
      // can't happen right now
      e.printStackTrace();
    }

    // lower case letters a-z
    characterClassManager.addCharacterClass(new CharacterClass('a', "[a-z]"));

    // upper case letters A-Z
    characterClassManager.addCharacterClass(new CharacterClass('A', "[A-Z]"));

    // numerals 0-9
    characterClassManager.addCharacterClass(new CharacterClass('9', "[0-9]"));

    // special characters like $, �, �, & etc.
    characterClassManager.addCharacterClass(new CharacterClass('$', "�$%&/\\������"));

    // whitespace
    characterClassManager.addCharacterClass(new CharacterClass(' ', "\\s"));

    // punctuation marks like _!? etc.
    characterClassManager.addCharacterClass(new CharacterClass('!',
        "\"|'|_|!|\\?|\\(|\\)|\\[|\\]|:"));

    // -
    characterClassManager.addCharacterClass(new CharacterClass('-', "-"));

    // .
    characterClassManager.addCharacterClass(new CharacterClass('.', "\\."));

    // ,
    characterClassManager.addCharacterClass(new CharacterClass(',', ","));

    // /
    characterClassManager.addCharacterClass(new CharacterClass('/', "/"));
  }

  // /**
  // * getDatatypeName(value) returns the string representation of the given
  // * database representation of the datatype
  // *
  // * @param value
  // * the representation of a datatype
  // * @return a string representation of the datatype
  // */
  // public static String getDatatypeName(int value) {
  // return Datatype.getName(value);
  // // switch (value) {
  // // case UNKNOWN:
  // // return STR_UNKNOWN;
  // // case OTHER:
  // // return STR_OTHER;
  // // case STRING:
  // // return STR_STRING;
  // // case TEXT:
  // // return STR_TEXT;
  // // case INTEGER:
  // // return STR_INTEGER;
  // // case DECIMAL:
  // // return STR_DECIMAL;
  // // case DATE:
  // // return STR_DATE;
  // // case LINK:
  // // return STR_LINK;
  // // case INTERNAL_LINK:
  // // return STR_INTERNAL_LINK;
  // // case EMPTY_VALUE:
  // // return STR_EMPTY_VALUE;
  // // default:
  // // return STR_UNKNOWN;
  // // }
  // }

  /**
   * getPattern(data) returns the pattern of the given object data.
   * 
   * @param data Original data of object column.
   * @return Pattern of the given data.
   */
  public Pair<String, String> getPattern(String data) {
    return characterClassManager.getPattern(data);
  }

  /**
   * getDatatype(pattern) returns the datatype by using the pattern representation of the data.
   * 
   * @param pattern the pattern which will be translated into the equivalent datatype
   * @return datatype that corresponds to the given pattern
   */
  public Datatype getDatatype(String pattern) {

    if (urlstartpattern == null) {
      urlstartpattern = getPattern("http://").getFirstElem();
    }

    // TODO idea - customized datatypes using regular expressions

    // complex date pattern
    // TODO are there already libraries for detecting date formats?
    String datePattern =
        "^9{1,2}[.|\\-|/]9{1,2}[.|-|/]9999" + "|9{1,2}[.|\\-|/]9{1,2}[.|\\-|/]99"
            + "|99[.|\\-|/]9{1,2}[.|\\-|/]9{1,2}" + "|9999[.|\\-|/]9{1,2}[.|\\-|/]9{1,2}"
            + "|9{1,2}[.|\\-|/]\\s*MONTH\\s*[.|\\-|/]99"
            + "|9{1,2}[.|\\-|/]\\s*MONTH\\s*[.|\\-|/]9999"
            + "|9{1,2}[.|\\-|/]\\s*MONTH\\s*[.|\\-|/]9999" + "|9{1,2}aa\\s*MONTH\\s*9999" + "$";
    // if (pattern == null) {
    // return TEXT;
    // } else if (Pattern.matches("^\\s*$", pattern)) {
    // return EMPTY_VALUE;
    // } else if (pattern.startsWith(urlstartpattern)) {
    // return LINK;
    // } else if (Pattern.matches(datePattern, pattern)) {
    // return DATE;
    // } else if
    // (Pattern.matches("^-?? ??((9*?)|(9{1,3}+(,9{3})*?))\\.9+?$",
    // pattern)) {
    // return DECIMAL;
    // } else if (Pattern.matches("^(-?? ??9+?)|(-?? ??9{1,3}+(,9{3})*?)$",
    // pattern)) {
    // return INTEGER;
    // } else {
    // return STRING;
    // }
    if (pattern == null) {
      return Datatype.TEXT;
    } else if (Pattern.matches("^\\s*$", pattern)) {
      return Datatype.EMPTY_VALUE;
    } else if (pattern.startsWith(urlstartpattern)) {
      return Datatype.LINK;
    } else if (Pattern.matches(datePattern, pattern)) {
      return Datatype.DATE;
    } else if (Pattern.matches("^-?? ??((9*?)|(9{1,3}+(,9{3})*?))\\.9+?$", pattern)) {
      return Datatype.DECIMAL;
    } else if (Pattern.matches("^(-?? ??9+?)|(-?? ??9{1,3}+(,9{3})*?)$", pattern)) {
      return Datatype.INTEGER;
    } else {
      return Datatype.STRING;
    }
  }
}
