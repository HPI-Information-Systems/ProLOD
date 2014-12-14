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

import java.sql.SQLException;
import java.util.Date;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.exec.optimize.OptimizeDB;
import de.hpi.fgis.ldp.server.algorithms.dataimport.ImportJob;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

public class ImportExec {
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final ImportExec main = injector.getInstance(ImportExec.class);

    System.err.println("parameters: <path to dump file> <schema name> (<label>) (replace)");

    main.runFor(args[0], args[1], (args.length > 2) ? args[2] : args[1], (args.length > 2));
    OptimizeDB.main(new String[] {args[1]});
  }

  private final ImportJob importJob;
  private final Provider<DebugProgress> debugProcess;

  /**
   * @param clusterLoader
   */
  @Inject
  public ImportExec(ImportJob importJob, Provider<DebugProgress> debugProcess) {
    this.importJob = importJob;
    this.debugProcess = debugProcess;
  }

  /**
   * @param args
   * @throws SQLException
   */
  public void runFor(String sourceFile, String schemaName, String label, boolean replace)
      throws Exception {
    System.err.println("start: " + new Date());

    System.err.println("label: " + label);
    System.err.println("schema: " + schemaName);
    System.err.println("file: " + sourceFile);

    IProgress progress = debugProcess.get();

    this.importJob.init(progress);
    this.importJob.init(schemaName, label, sourceFile, null, replace);
    this.importJob.execute();

    System.err.println("end: " + new Date());
  }

}
