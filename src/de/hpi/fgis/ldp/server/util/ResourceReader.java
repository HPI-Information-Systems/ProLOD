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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class enables simple read access to resource files.
 * 
 * @author matthias.pohl
 * @author toni.gruetze
 * 
 */
public class ResourceReader {
  private static Logger logger = Logger.getLogger(ResourceReader.class.getPackage().getName());

  /**
   * gets the resource content of a plain text file as a {@link List} of {@link String}s
   * 
   * @param basePackage the basic package under the specified path (if this parameter is null it
   *        will be ignored)
   * @param path the path to the resource file
   * @param params the parameters to replace in the resource string
   * @return the resource content as {@link List} of {@link String}s
   */
  public List<String> getCommandsFromResource(final Package basePackage, final String path,
      final Object... params) {
    ArrayList<String> result = new ArrayList<String>();
    try {
      final InputStream resStream = this.generateInputStream(basePackage, path);
      BufferedReader br = new BufferedReader(new InputStreamReader(resStream));
      if (resStream == null) {
        logger.throwing(this.getClass().getName(), "getStringFromResource(String, Object...)",
            new FileNotFoundException("File not found!"));
      }

      StringBuilder sb = new StringBuilder();
      String newLine;
      while ((newLine = br.readLine()) != null) {
        newLine = newLine.trim();
        if (!"".equals(newLine) && !newLine.startsWith("--")) {
          // merge sql statements over n lines
          if (newLine.endsWith(";")) {
            sb.append(newLine.substring(0, newLine.length() - 1));
            result.add(String.format(sb.toString(), params));
            sb = new StringBuilder();
          } else {
            sb.append(newLine).append(" ");
          }
        }
      }
      if (sb.length() > 0) {
        result.add(String.format(sb.toString(), params));
      }

      br.close();
      if (resStream != null) {
        resStream.close();
      }
    } catch (IOException e) {
      logger.throwing(this.getClass().getName(), "getCommandsFromResource()", e);
    }

    if ("".equals(result)) {
      logger.warning("No content was found in '" + path + "'.");
    }

    return result;
  }

  /**
   * gets properties from a resource file
   * 
   * @param basePackage the basic package under the specified path (if this parameter is null it
   *        will be ignored)
   * @param path the path to the resource file
   * @return the properties
   */
  public Properties getPropertiesFromResource(final Package basePackage, final String path) {
    final InputStream resStream = this.generateInputStream(basePackage, path);
    final Properties properties = new Properties();

    try {
      properties.load(resStream);
    } catch (IOException e) {
      logger.throwing(this.getClass().getName(), "getPropertiesFromResource()", e);
    }

    return properties;
  }

  /**
   * gets the resource content of a plain text file as a {@link String}
   * 
   * @param basePackage the basic package under the specified path (if this parameter is null it
   *        will be ignored)
   * @param path the path to the resource file
   * @param params the parameters to replace in the resource string
   * @return the resource content string
   */
  public String getStringFromResource(Package basePackage, String path, Object... params) {
    String result = null;
    try {
      final InputStream resStream = this.generateInputStream(basePackage, path);
      if (resStream == null) {
        logger.throwing(this.getClass().getName(), "getStringFromResource(String, Object...)",
            new FileNotFoundException("File not found!"));
      }
      if (resStream != null) {
        byte[] b = new byte[resStream.available()];
        resStream.read(b);
        resStream.close();
        result = new String(b);
      }
    } catch (IOException e) {
      logger.throwing(this.getClass().getName(), "getStringFromResource()", e);
    }

    if ("".equals(result)) {
      logger.warning("No content was found in '" + path + "'.");
    }

    return String.format(result, params);
  }

  private InputStream generateInputStream(final Package basePackage, final String path) {
    String newPath = path;
    if (basePackage != null) {
      newPath =
          "/" + basePackage.getName().replace('.', '/') + (path.startsWith("/") ? "" : "/") + path;
    }
    // remove optional argument delimiter
    final String optionalPath = newPath.replaceAll("\\?", "");
    final InputStream resStream = ResourceReader.class.getResourceAsStream(optionalPath);

    if (resStream == null) {
      final String defaultPath = newPath.replaceAll("\\?[^\\?]*\\?", "");
      final InputStream defaultResStream = ResourceReader.class.getResourceAsStream(defaultPath);
      if (defaultResStream == null) {
        throw new IllegalAccessError("Unable to access specified source: \"" + path + "\"");
      }
      return defaultResStream;
    }

    return resStream;
  }

  /**
   * gets the resource content of a plain text file as a {@link String} w/o any comments at the
   * beginning of the file. <b>Attention!</b>To specify the beginning of the non-documentation
   * content, you have to enter two newlines in the resource file.
   * 
   * @param basePackage the basic package under the specified path (if this parameter is null it
   *        will be ignored)
   * @param path the path to the resource file
   * @param params the parameters to replace in the resource string
   * @return the resource content string
   */
  public String getUndocumentedStringFromResource(Package basePackage, String path,
      Object... params) {
    String fileContent = this.getStringFromResource(basePackage, path, params);

    if (fileContent == null) {
      return null;
    }
    // a simple hack to specify whether this file is formatted with windows
    // CR&LF or not
    String lineSeparator;
    if (fileContent.indexOf("\r\n") > -1) {
      lineSeparator = "\r\n";
    } else {
      lineSeparator = "\n";
    }

    String undocumentedString =
        fileContent.substring(
            fileContent.indexOf(lineSeparator + lineSeparator) + 2 * lineSeparator.length()).trim();

    if ("".equals(undocumentedString)) {
      logger.warning("No content was found in '" + path + "'.");
    }

    return undocumentedString;
  }
}
