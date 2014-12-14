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
 * this interface provides a job with a specific result
 * 
 * @author toni.gruetze
 *
 * @param <T> the result type of this job
 */
public interface IJob<T> {
  /**
   * gets the result of the job
   * 
   * @return the result
   */
  public abstract T getResult();

  /**
   * does some cleanup tasks after the task
   */
  public abstract void cleanup();
}
