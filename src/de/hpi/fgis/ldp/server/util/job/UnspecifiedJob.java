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

package de.hpi.fgis.ldp.server.util.job;

/**
 * this class provides a job with a non-monitored execution but a specific result
 * 
 * @author toni.gruetze
 *
 * @param <T> the result type of this job
 */
public abstract class UnspecifiedJob<T> extends Thread implements IJob<T> {
  private T result = null;
  private Throwable throwable = null;
  private final String description;

  /**
   * creates a new Job
   * 
   * @param description the description of the job
   */
  public UnspecifiedJob(String description) {
    this.description = description;
    super.setDaemon(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.IJob#getResult()
   */
  @Override
  public T getResult() {
    return result;
  }

  /**
   * sets the result
   * 
   * @param result the result to set
   */
  protected void setResult(T result) {
    this.result = result;
  }

  /**
   * gets the exception thrown during the execution of the job
   * 
   * @return the exception
   */
  public Throwable getThrowable() {
    return throwable;
  }

  /**
   * sets the throwable
   * 
   * @param throwable the throwable to set
   */
  protected void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  /**
   * gets the job description
   * 
   * @return the job description
   */
  public String getDescription() {
    return description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.IJob#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do as default
  }
}
