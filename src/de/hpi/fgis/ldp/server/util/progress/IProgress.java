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

/**
 * enables the logging/output of complex progress information with subtasks.
 * 
 * @author toni.gruetze
 */
public interface IProgress {
  /**
   * starts a progress with unknown length
   * 
   * @param msg progress message
   */
  public void startProgress(final String msg);

  /**
   * starts a progress with known length
   * 
   * @param msg progress message
   * @param max max possible progress step count
   */
  public void startProgress(final String msg, final long max);

  /**
   * stops the progress
   */
  public void stopProgress();

  /**
   * continues the progress (one step done)
   */
  public void continueProgress();

  /**
   * continues the progress (one step done)
   * 
   * @param msg progress message
   */
  public void continueProgress(final String msg);

  /**
   * continues the progress at the given step
   * 
   * @param current current progress step
   */
  public void continueProgressAt(final long currentStep);

  /**
   * continues the progress at the given step
   * 
   * @param currentStep current progress step
   * @param msg the message to be append to the state message (may be null or empty)
   */
  public void continueProgressAt(final long currentStep, final String msg);

  /**
   * continues the progress with a subtask progress
   * 
   * @param size the estimated size of the current step. this means the estimated next continue step
   *        after this subtask.
   * @return a progress instance to log the progress of a subtask
   */
  public IProgress continueWithSubProgress(final long size);

  /**
   * continues the progress with a subtask progress
   * 
   * @param current current progress step
   * @param size the estimated size of the current step. this means the estimated next continue step
   *        after this subtask.
   * @return a progress instance to log the progress of a subtask
   */
  public IProgress continueWithSubProgressAt(final long current, final long size);
  // TODO the same for parallel sub tasks?
}
