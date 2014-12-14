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

import net.customware.gwt.dispatch.shared.ActionException;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * this is the base class for all exceptions used as a subject of communication between client and
 * server
 * 
 * @author toni.gruetze
 * 
 */
public class RPCException extends ActionException implements IsSerializable {
  private static final long serialVersionUID = -7824676541988987703L;

  public RPCException() {
    super();
  }

  public RPCException(String message, Throwable cause) {
    super(message, cause);
  }

  public RPCException(String message) {
    super(message);
  }

  public RPCException(Throwable cause) {
    super(cause);
  }
}
