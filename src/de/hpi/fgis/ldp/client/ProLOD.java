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

package de.hpi.fgis.ldp.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import de.hpi.fgis.ldp.client.gin.ProLODGinjector;
import de.hpi.fgis.ldp.client.mvp.AppPresenter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ProLOD implements EntryPoint {

  // debug
  protected static int counter = 0;

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {
    // check if the page contains the admin div
    RootPanel panel = RootPanel.get("admin");
    if (panel != null) {
      // if it does, activate admin mode
      injector = GWT.create(ProLODGinjector.Admin.class);
    } else {
      // deactivate admin mode
      injector = GWT.create(ProLODGinjector.Normal.class);

      // search for default entry point
      panel = RootPanel.get("entry_point");

      if (panel == null) {
        // no entry point found -> take default (whole body element)
        Log.error("No entry point found in HTML file!");
        panel = RootPanel.get();
      }
    }

    final AppPresenter appPresenter = injector.getAppPresenter();
    appPresenter.go(panel);

    injector.getPlaceManager().fireCurrentPlace();
  }

  private ProLODGinjector injector;

}
