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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class LogProvider implements Provider<Log> {
  @Override
  public Log get() {
    return new Log4JLogger("ProLOD Logger");
  }
}
