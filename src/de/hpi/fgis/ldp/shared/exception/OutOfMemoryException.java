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
 * this exception shows that the request couldn't be processed completely
 * 
 * @author toni.gruetze
 * 
 */
public class OutOfMemoryException extends RPCException {
  private static final long serialVersionUID = -868348391730184973L;

  /**
   * hide default constructor
   */
  protected OutOfMemoryException() {
    // nothing to do
  }

  /**
   * creates a new {@link OutOfMemoryException}
   */
  public OutOfMemoryException(String msg) {
    super(msg);
  }
}
