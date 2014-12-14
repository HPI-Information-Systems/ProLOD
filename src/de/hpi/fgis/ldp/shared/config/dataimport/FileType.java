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

package de.hpi.fgis.ldp.shared.config.dataimport;

/**
 * represents a supported file type
 * 
 * @author toni.gruetze
 * 
 */
public enum FileType {
  NT(".nt"), NT_BZ2(".nt.bz2"), NT_GZ(".nt.gz");
  private final String extension;

  /**
   * gets the file type extension
   * 
   * @return the file type extension
   */
  public String getExtension() {
    return extension;
  }

  /**
   * creates a new fileType
   * 
   * @param extension the file type extension
   */
  private FileType(String extension) {
    this.extension = extension;
  }

  /**
   * determines the type of the given file
   * 
   * @param fileName the name of the file
   * @return the type of the file
   */
  public static FileType getType(String fileName) {
    for (FileType currentType : FileType.values()) {
      if (fileName.endsWith(currentType.getExtension())) {
        return currentType;
      }
    }
    return null;
  }
}
