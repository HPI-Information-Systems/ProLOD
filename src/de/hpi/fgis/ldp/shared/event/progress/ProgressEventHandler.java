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

package de.hpi.fgis.ldp.shared.event.progress;

import com.google.gwt.event.shared.EventHandler;

/**
 * represents a handler for progress update events
 * 
 * @author toni.gruetze
 *
 */
public interface ProgressEventHandler<ResultType> extends EventHandler {
  /**
   * captures a progress update event
   * 
   * @param event the progress event to capture
   */
  public void onProgressUpdate(ProgressEvent<ResultType> event);
}
