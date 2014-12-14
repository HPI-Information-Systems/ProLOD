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

import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * this class provides a job with a monitored execution and a specific result
 * 
 * @author toni.gruetze
 *
 * @param <T> the result type of this job
 */
public abstract class MonitoredJob<T> implements IJob<T> {
  private IProgress progressInstance;

  /**
   * initialized this monitored Job
   * 
   * @param progress the progress instance
   */
  public void init(IProgress progress) {
    this.progressInstance = progress;
  }

  /**
   * gets the progress instance
   * 
   * @return the progress instance
   */
  protected IProgress getProgress() {
    return this.progressInstance;
  }

  /**
   * executes this monitored Job
   * 
   * @throws Exception the exception which occurred
   */
  public abstract void execute() throws Exception;

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.IJob#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do as default
  }

  public abstract String getName();
}
