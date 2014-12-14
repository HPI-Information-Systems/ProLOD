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

package de.hpi.fgis.ldp.exec.dataimport;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class TestPublishRootSession {
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final TestPublishRootSession main = injector.getInstance(TestPublishRootSession.class);
    main.run();

  }

  private final ISchemaStorage sStorage;
  private final Provider<DebugProgress> debugProcess;

  /**
   * @param clusterLoader
   */
  @Inject
  public TestPublishRootSession(ISchemaStorage sStorage, Provider<DebugProgress> debugProcess) {
    this.sStorage = sStorage;
    this.debugProcess = debugProcess;
  }

  public void run() throws Exception {
    DataSource source = new DataSource("LINKEDMDB").asUserView("toni");
    Session rootSession = new Session(source, -1);
    this.sStorage.publishRootSession(rootSession, debugProcess.get());
  }

}
