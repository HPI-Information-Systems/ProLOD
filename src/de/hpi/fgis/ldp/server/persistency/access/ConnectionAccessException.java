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

package de.hpi.fgis.ldp.server.persistency.access;

import java.security.ProviderException;
import java.sql.Connection;

import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool;

/**
 * A runtime exception which will be thrown due to unavailable {@link Connection} instances in a
 * {@link ConnectionPool}.
 * 
 * @author toni.gruetze
 */
public class ConnectionAccessException extends ProviderException {
  private static final long serialVersionUID = -1736588438748061254L;
  private final IDataSource source;

  /**
   * Constructs a {@link ConnectionAccessException} with the specified detail message. A detail
   * message is a String that describes this particular exception.
   * 
   * @param s the detail message.
   * @param source the data source which throws this exception
   */
  public ConnectionAccessException(String s, IDataSource source) {
    super(s);
    this.source = source;
  }

  /**
   * Creates a {@link ConnectionAccessException} with the specified detail message and cause.
   * 
   * @param message the detail message (which is saved for later retrieval by the
   *        Throwable.getMessage() method).
   * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
   *        (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
   */
  public ConnectionAccessException(String message, Throwable cause, IDataSource source) {
    super(message, cause);
    this.source = source;
  }

  /**
   * gets the data source
   * 
   * @return the data source
   */
  public IDataSource getSource() {
    return source;
  }
}
