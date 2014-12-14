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

package de.hpi.fgis.ldp.exec.optimize;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.algorithms.emulation.DBUsageEmulation;

public class EmulateDBUsage {
  /**
   * @param args
   */
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final DBUsageEmulation main = injector.getInstance(DBUsageEmulation.class);
    // TODO
    // DUMP_DIR = "./";

    if (args == null || args.length <= 0) {
      main.runForAll();
    } else {
      main.runFor(args);
    }
  }
}
