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

package de.hpi.fgis.ldp.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {

  /**
   * Set the properties file with
   * 
   * @return
   */
  public static Properties loadProperties() {
    String propertiesFile = System.getProperty("prolod.properties");

    if (propertiesFile != null) {
      return loadFromFile(propertiesFile);
    } else {
      System.out.println("NO PROPERTIES FILE CONFIGURED!");
      System.out.println("use -Dprolod.properties=<file> as vm argument!");
      System.out.println("you can find a default config in src/de/hpi/fgis/ldp/config");
      throw new RuntimeException("System property \"prolod.properties\" not defined");
    }
  }

  private static Properties loadFromFile(String propertiesFile) {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(propertiesFile));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }
}
