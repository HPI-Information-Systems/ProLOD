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

package de.hpi.fgis.ldp.server.util.exception;

import net.customware.gwt.dispatch.shared.Action;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.persistency.access.ConnectionAccessException;
import de.hpi.fgis.ldp.shared.exception.DataConnectionException;
import de.hpi.fgis.ldp.shared.exception.OutOfMemoryException;
import de.hpi.fgis.ldp.shared.exception.RPCException;

public class ExceptionFactory {
  private final Log logger;

  @Inject
  protected ExceptionFactory(Log logger) {
    this.logger = logger;
  }

  /**
   * this method encapsulates the given exception to a client readable form
   * 
   * @param t the exception
   * @param action the action on which this exception occured
   * @return the encapsulated exception
   */
  public RPCException encapsulateException(Throwable t) {
    return this.encapsulateException(t, null);
  }

  /**
   * this method encapsulates the given exception to a client readable form
   * 
   * @param t the exception
   * @param action the action on which this exception occured
   * @return the encapsulated exception
   */
  public RPCException encapsulateException(Throwable t, Action<?> action) {
    if (t instanceof OutOfMemoryError) {
      logger.error("Unable to allocate enough memory to execute \""
          + ((action == null) ? "job" : action.getClass().getName()) + "\": " + t.getMessage(), t);
      return new OutOfMemoryException(t.getMessage());
    }

    if (t instanceof ConnectionAccessException) {
      final ConnectionAccessException e = (ConnectionAccessException) t;
      logger.error("Unable to get Connection to data source \"" + e.getSource().getName() + "@"
          + e.getSource().getType() + "\" while executing \""
          + ((action == null) ? "job" : action.getClass().getName()) + "\": " + e.getMessage(), e);

      return new DataConnectionException(e.getSource().getType(), e.getSource().getName());

    }

    logger.error("Exception while executing "
        + ((action == null) ? "job" : action.getClass().getName()) + ": " + t.getMessage(), t);

    return new RPCException(t);
  }
}
