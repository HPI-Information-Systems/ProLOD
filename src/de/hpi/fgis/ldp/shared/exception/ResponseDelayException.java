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
 * this is the exception class for not yet finished processes, which need a bit mor execution time
 * 
 * @author toni.gruetze
 * 
 */
public class ResponseDelayException extends RPCException {
  private static final long serialVersionUID = 4715719277631379783L;
  private long progressIdentifier;

  /**
   * hide default constructor
   */
  protected ResponseDelayException() {
    // nothing to do
  }

  /**
   * creates a new {@link ResponseDelayException} with the given process id
   * 
   * @param progressIdentifier the process id
   */
  public ResponseDelayException(long progressIdentifier) {
    super("The request seems to take a moment, please be patient!");
    this.progressIdentifier = progressIdentifier;
  }

  /**
   * gets the process id
   * 
   * @param progressIdentifier the process id
   */
  public long getProgressIdentifier() {
    return progressIdentifier;
  }
}
