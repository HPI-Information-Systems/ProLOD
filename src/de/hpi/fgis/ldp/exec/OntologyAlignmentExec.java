package de.hpi.fgis.ldp.exec;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;
import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.OntologyAnalyzer;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class OntologyAlignmentExec {

  public static void main(String[] args) throws Exception {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final OntologyAlignmentExec main = injector.getInstance(OntologyAlignmentExec.class);

    System.err.println("parameters: <schema name>");

    main.runFor("DBPEDIA39_DE");
  }

  @Inject
  private final Log logger;
  @Inject
  private final Provider<OntologyAnalyzer> jobSource;
  private final Provider<DebugProgress> debugProcess;
  private final Provider<AdvancedARF> finder;

  /**
   * @param clusterLoader
   */
  @Inject
  public OntologyAlignmentExec(Provider<OntologyAnalyzer> oAnalyzerJob, Log logger,
      Provider<DebugProgress> debugProcess, Provider<AdvancedARF> finder) {
    this.logger = logger;
    this.jobSource = oAnalyzerJob;
    this.debugProcess = debugProcess;
    this.finder = finder;
  }

  /**
   * @param args
   * @throws SQLException
   */
  public void runFor(String schemaName) throws Exception {
    System.err.println("start: " + new Date());

    System.err.println("schema: " + schemaName);

    IProgress progress = debugProcess.get();
    progress.startProgress("Clustering", 1000);

    OntologyAnalyzer oAnalyzer = jobSource.get();
    oAnalyzer.init(new DataSource(schemaName), 0.01, 0.5);
    oAnalyzer.identifyAllUnderSpecifications(progress);
    // ===[initial clustering&labeling]===

    // progress 80%

    progress.continueProgressAt(1000);

    progress.stopProgress();

    System.err.println("end: " + new Date());
  }

}
