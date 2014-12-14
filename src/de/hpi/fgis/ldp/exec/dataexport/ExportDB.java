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

package de.hpi.fgis.ldp.exec.dataexport;

import java.io.File;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.ObjectValue;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataColumn;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

public class ExportDB {
  private static String DUMP_DIR = "./";

  /**
   * @param args
   */
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final ExportDB main = injector.getInstance(ExportDB.class);
    // TODO
    // DUMP_DIR = "./";

    if (args == null || args.length <= 0) {
      main.runForAll();
      // args = new String[] {"BLUB", "DBPEDIA3", "DBPEDIA4", "DBPEDIA5",
      // "DBPEDIA6", "DBPEDIA7", "DRUGBANK", "LINKEDMDB",
      // "SEMANTIC_BIBLE", "NEW_TEST", "TEST" };
    } else {
      main.runFor(args);
    }
  }

  private final ISchemaLoader schemaLoader;
  private final IClusterLoader clusterLoader;
  private final Log logger;
  @Inject
  private Provider<DebugProgress> debugProcess;

  private final int bulkSize = 50000;

  @Inject
  private ExportDB(ISchemaLoader schemaLoader, IClusterLoader clusterLoader, Log logger) {
    this.schemaLoader = schemaLoader;
    this.clusterLoader = clusterLoader;
    this.logger = logger;
  }

  private void runForAll() {
    List<Cluster> allCluster = null;
    try {
      allCluster = schemaLoader.getRootClusters(debugProcess.get());
    } catch (SQLException e) {
      logger.error("Fehler!", e);
    }
    this.runFor(allCluster);
  }

  private void runFor(String[] sources) {
    List<Cluster> rootClusters = new ArrayList<Cluster>(sources.length);
    try {
      for (String source : sources) {
        rootClusters.add(schemaLoader.getRootCluster(new DataSource(source), debugProcess.get()));
      }
    } catch (SQLException e) {
      logger.error("Fehler!", e);
    }
    this.runFor(rootClusters);
  }

  private void runFor(List<Cluster> rootClusters) {
    logger.info("#######################");
    logger.info("exporting the following clusters: ");// rootCluster.getDataSource().getLabel()
    // + " (" +
    // rootCluster.getLabel()
    // + ")" + " -> " +
    // fileName + "");
    for (final Cluster cluster : rootClusters) {
      logger.info(cluster.getDataSource().getLabel() + " (" + cluster.getLabel() + ")");
    }
    logger.info("#######################");
    for (final Cluster cluster : rootClusters) {
      logger.info("===[initializing...]===");
      this.runFor(cluster);
      logger.info("========[done!]========");
    }
  }

  @SuppressWarnings("unchecked")
  private void runFor(Cluster rootCluster) {
    PrintStream fileOut = null;
    final String fileName = DUMP_DIR + rootCluster.getDataSource().getLabel() + ".nt";
    try {
      logger.info(" ==> starting run for: " + rootCluster.getDataSource().getLabel() + " ("
          + rootCluster.getLabel() + ")" + " -> " + fileName + "");
      if (new File(fileName).exists()) {
        logger.info(" ==> file already exists!");
        return;
      }
      final int entityCount = rootCluster.getSize();
      logger.info(" ==> cluster size: " + entityCount + " (" + rootCluster.getTripleCount()
          + " triples)");
      fileOut = new PrintStream(fileName);
      StringBuilder builder = new StringBuilder();
      int from = 0;
      while (true) {
        final IDataTable subjectTable =
            clusterLoader.getSortetSubjects(rootCluster, from, from + bulkSize,
                IgnoringProgress.INSTANCE);
        final int currentSubjectCount = subjectTable.getRowCount();

        final IDataColumn<Subject> subjectColumn = (IDataColumn<Subject>) subjectTable.getColumn(0);

        for (int subjectIndex = 0; subjectIndex < currentSubjectCount; subjectIndex++) {
          final Subject subject = subjectColumn.getElement(subjectIndex);

          if ((from + subjectIndex) % 100000 == 0) {
            logger.info("\t--> " + from + " --> " + subject.getLabel() + " ("
                + ((1D * (from + subjectIndex)) / entityCount) + "%)");
          }
          final IDataTable tripleTable =
              clusterLoader.getSubjectTriples(rootCluster, subject, IgnoringProgress.INSTANCE);
          final int currentTripleCount = tripleTable.getRowCount();

          final IDataColumn<Predicate> predicateColumn =
              (IDataColumn<Predicate>) tripleTable.getColumn(0);
          final IDataColumn<ObjectValue> objectColumn =
              (IDataColumn<ObjectValue>) tripleTable.getColumn(1);

          builder = new StringBuilder();
          builder.append('<').append(subject.getLabel()).append("> ");
          final String subjectString = builder.toString();
          for (int tripleIndex = 0; tripleIndex < currentTripleCount; tripleIndex++) {
            final Predicate predicate = predicateColumn.getElement(tripleIndex);
            final ObjectValue object = objectColumn.getElement(tripleIndex);

            builder = new StringBuilder(subjectString);
            builder.append('<').append(predicate.getLabel()).append("> ");
            builder.append('\"').append(object.getLabel()).append("\" .");

            // print line
            fileOut.println(builder.toString());
          }
        }

        from += currentSubjectCount;

        if (currentSubjectCount < bulkSize || currentSubjectCount <= 0) {
          break;
        }
      }
    } catch (Throwable e) {
      logger.error("Fehler!", e);
    } finally {
      logger.info(" ==> finishing run for: " + rootCluster.getDataSource().getLabel() + " ("
          + rootCluster.getLabel() + ")" + " -> " + fileName + "");
      if (fileOut != null) {
        fileOut.close();
      }
    }
  }

  private final static class IgnoringProgress implements IProgress {
    public final static IgnoringProgress INSTANCE = new IgnoringProgress();

    private IgnoringProgress() {}

    @Override
    public void continueProgress() {}

    @Override
    public void continueProgress(String msg) {}

    @Override
    public void continueProgressAt(long currentStep) {}

    @Override
    public void continueProgressAt(long currentStep, String msg) {}

    @Override
    public IProgress continueWithSubProgress(long size) {
      return this;
    }

    @Override
    public IProgress continueWithSubProgressAt(long current, long size) {
      return this;
    }

    @Override
    public void startProgress(String msg) {}

    @Override
    public void startProgress(String msg, long max) {}

    @Override
    public void stopProgress() {}
  }

}
