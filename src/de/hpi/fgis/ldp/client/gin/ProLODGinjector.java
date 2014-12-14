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

package de.hpi.fgis.ldp.client.gin;

import net.customware.gwt.presenter.client.place.PlaceManager;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

import de.hpi.fgis.ldp.client.mvp.AppPresenter;

public interface ProLODGinjector {
  AppPresenter getAppPresenter();

  PlaceManager getPlaceManager();

  @GinModules({ProLODClientModule.class})
  public static interface Normal extends ProLODGinjector, Ginjector {
  }

  @GinModules({ProLODClientModule.Admin.class})
  public static interface Admin extends ProLODGinjector, Ginjector {
  }
}
