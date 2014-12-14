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

import de.hpi.fgis.ldp.server.util.job.UnspecifiedJob;

public class UnspecifiedJobProgress<T> implements IPersistentProgress<T> {
  private UnspecifiedJob<T> job = null;
  private long identifier;

  public void init(long identifier, UnspecifiedJob<T> job) {
    this.job = job;
    this.identifier = identifier;
  }

  @Override
  public String getDetailMessage() {
    return null;
  }

  @Override
  public Throwable getException() {
    return this.job.getThrowable();
  }

  @Override
  public String getMessage() {
    return this.job.getDescription();
  }

  @Override
  public double getProgress() {
    return -1;
  }

  @Override
  public T getResult() {
    return this.job.getResult();
  }

  @Override
  public IPersistentProgress<T> getSubProgress() {
    return null;
  }

  @Override
  public boolean isFinished() {
    return !this.job.isAlive();
  }

  @Override
  public long getIdentifier() {
    return this.identifier;
  }
}
