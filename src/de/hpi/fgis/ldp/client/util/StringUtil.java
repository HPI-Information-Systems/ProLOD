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

package de.hpi.fgis.ldp.client.util;

/**
 * this class offers string formatting algorithms
 */
public class StringUtil {
  private static StringUtil INSTANCE = new StringUtil();

  // // TODO java.util.regex isn't yet supported by gwt
  // private final Pattern urlPattern =
  // Pattern.compile("^\\s*[^/\\s]+://(www\\.)?([^/\\s]+)\\.[^\\./\\s]+(/.*)?/([^/\\s]+)(/)?\\s*$");

  /**
   * get the singleton instance
   * 
   * @return the instance
   */
  public static StringUtil getInstance() {
    return StringUtil.INSTANCE;
  }

  /**
   * shortens the url to a form which is easier to read. e.g.
   * http://dbpedia.org/resource/Albert_Einstein will be translated to dbpedia:Albert_Einstein
   * 
   * @param url the url to be shortened
   * @return the shortened url
   */
  public String shortenURL(final String origUrl) {
    // // TODO java.util.regex isn't yet supported by gwt
    // final Matcher matcher = this.urlPattern.matcher( origUrl );
    // if(matcher.matches())
    // return new
    // StringBuilder(matcher.group(2)).append(':').append(matcher.group(4)).toString();
    //
    // return origUrl;
    final String url = origUrl.trim();
    final String[] urlParts = url.split("/");
    if (urlParts.length >= 4
        && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://"))) {

      String domain = urlParts[2];
      if (domain.startsWith("www")) {
        domain = domain.substring(domain.indexOf('.') + 1);
      }
      // remove top-level-domain
      domain = domain.substring(0, domain.lastIndexOf('.'));

      String entity = urlParts[urlParts.length - 1];
      if (entity == null || "".equals(entity)) {
        return origUrl;
      }

      return new StringBuilder(domain).append(':').append(entity).toString();
    }

    return origUrl;
  }

  /**
   * remove characters for text which produces problems in html pages/xml data (&amp;, &lt; &gt;
   * &#039; &quot;).
   * 
   * @param original the original text (<b>with</b>: &amp;, &lt; &gt; &#039; &quot;).
   * @return the modified text (<b>w/o</b>: &amp;, &lt; &gt; &#039; &quot;).
   */
  public String removeHTMLTags(final String original) {
    return this.forXML(original.toCharArray());
  }

  private String forXML(final char[] text) {
    final StringBuilder result = new StringBuilder();
    for (char currentChar : text) {
      switch (currentChar) {
        case '&':
          result.append("&amp;");
          break;
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        case '\'':
          result.append("&#039;");
          break;
        case '\"':
          result.append("&quot;");
          break;
        default:
          result.append(currentChar);
      }
    }

    return result.toString();
  }
}
