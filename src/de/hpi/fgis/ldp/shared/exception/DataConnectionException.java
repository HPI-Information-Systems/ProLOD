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

package de.hpi.fgis.ldp.shared.exception;

/**
 * this exception occurs if the connection to the data source couldn't be established
 * 
 * @author toni.gruetze
 * 
 */
public class DataConnectionException extends RPCException {
  private static final long serialVersionUID = -670376625624133020L;

  /**
   * hide default constructor
   */
  protected DataConnectionException() {
    // nothing to do
  }

  /**
   * creates a new {@link DataConnectionException}
   * 
   * @param sourceType the type of the data source
   * @param sourceSchema the schema in the data source
   */
  public DataConnectionException(String sourceType, String sourceSchema) {
    super("Unable to connect to the " + sourceType + " data source (schema: " + sourceSchema + ")!");

  }
}
