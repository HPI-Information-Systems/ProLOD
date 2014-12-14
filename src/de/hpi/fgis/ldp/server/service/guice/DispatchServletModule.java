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

package de.hpi.fgis.ldp.server.service.guice;

import com.google.inject.servlet.ServletModule;

import de.hpi.fgis.ldp.server.service.ImportServlet;

public class DispatchServletModule extends ServletModule {

  @Override
  public void configureServlets() {
    // NOTE: the servlet context will probably need changing
    serve("*/prolod/dispatch").with(CacheDispatchServiceServlet.class);
    serve("*.gupld").with(ImportServlet.class);
  }

}
