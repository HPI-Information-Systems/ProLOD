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

package de.hpi.fgis.ldp.client.service;

import net.customware.gwt.presenter.client.Display;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * this interface defines a display instance which is able to present user feedback for
 * {@link AsyncCallback}s
 * 
 * @author toni.gruetze
 * 
 */
public interface CallbackDisplay extends Display {
  /**
   * Indicate to the display that processing is being done.
   */
  @Override
  void startProcessing();

  /**
   * Indicate to the display that processing has completed.
   */
  @Override
  void stopProcessing();

  /**
   * Indicate to the display that processing ended erroneous
   */
  void displayError();
}
