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

import java.sql.SQLException;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.persistency.access.ConnectionAccessException;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;

public class OptimizeDB {

  /**
   * @param args
   */
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final OptimizeDB main = injector.getInstance(OptimizeDB.class);

    if (args == null || args.length <= 0) {
      main.runForAll();
      // args = new String[] {"BLUB", "DBPEDIA3", "DBPEDIA4", "DBPEDIA5",
      // "DBPEDIA6", "DBPEDIA7", "DRUGBANK", "LINKEDMDB",
      // "SEMANTIC_BIBLE", "NEW_TEST", "TEST" };
    } else {
      main.runFor(args);
    }
  }

  private final ISQLDataSourceProvider sourceProvider;
  private final ISchemaLoader schemaLoader;
  private final Provider<DebugProgress> debugProcess;

  @Inject
  private OptimizeDB(ISQLDataSourceProvider sourceProvider, ISchemaLoader schemaLoader,
      Provider<DebugProgress> debugProcess) {
    this.sourceProvider = sourceProvider;
    this.schemaLoader = schemaLoader;
    this.debugProcess = debugProcess;
  }

  private void runForAll() {
    String[] schemas = null;
    try {
      List<Cluster> rootClusters = schemaLoader.getRootClusters(debugProcess.get());
      schemas = new String[rootClusters.size()];
      for (int schemaIndex = 0; schemaIndex < schemas.length; schemaIndex++) {
        schemas[schemaIndex] = rootClusters.get(schemaIndex).getDataSource().getLabel();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.runFor(schemas);
  }

  private void runFor(String[] schemas) {
    for (final String schema : schemas) {
      this.runFor(schema);
    }
  }

  private void runFor(String schema) {
    try {
      IDataSource source = this.sourceProvider.getConnectionPool(schema);
      source.getConnection();
      source.optimize();
      // TODO may also use this for db2 w/o ssh:
      // source.optimizeToStdErr();
    } catch (ConnectionAccessException e) {
      e.printStackTrace();
    }
  }

}
