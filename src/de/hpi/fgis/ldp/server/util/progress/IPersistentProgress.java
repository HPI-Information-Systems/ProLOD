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

package de.hpi.fgis.ldp.server.util.progress;

public interface IPersistentProgress<T> {

  public String getMessage();

  public double getProgress();

  public boolean isFinished();

  public String getDetailMessage();

  public Throwable getException();

  public IPersistentProgress<T> getSubProgress();

  public T getResult();

  public long getIdentifier();
}
