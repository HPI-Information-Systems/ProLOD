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

package de.hpi.fgis.ldp.exec.partitioning;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class Partition {

  /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final Partition main = injector.getInstance(Partition.class);
    // TODO
    // DUMP_DIR = "./";

    if (args == null || args.length <= 0) {
      System.err.println("Unknown schema to get partitionned");
      // args = new String[] {"BLUB", "DBPEDIA3", "DBPEDIA4", "DBPEDIA5",
      // "DBPEDIA6", "DBPEDIA7", "DRUGBANK", "LINKEDMDB",
      // "SEMANTIC_BIBLE", "NEW_TEST", "TEST" };
    } else if (args.length <= 1) {
      main.runFor(args[0], null);
    } else {
      main.runFor(args[0], args[1]);
    }
  }

  private final ISchemaLoader schemaLoader;
  private final IClusterLoader clusterLoader;
  private final IClusterStorage clusterStorage;
  private final Provider<DebugProgress> debugProcess;
  private final Log logger;

  @Inject
  private Partition(ISchemaLoader schemaLoader, IClusterLoader clusterLoader,
      IClusterStorage clusterStorage, Provider<DebugProgress> debugProcess, Log logger) {
    this.schemaLoader = schemaLoader;
    this.clusterLoader = clusterLoader;
    this.clusterStorage = clusterStorage;
    this.debugProcess = debugProcess;
    this.logger = logger;
  }

  private void runFor(String schema, String userView) throws SQLException {
    DataSource source = new DataSource(schema);
    if (userView != null) {
      source = source.asUserView(userView);
    }
    Cluster root = this.schemaLoader.getRootCluster(source, debugProcess.get());

    List<Cluster> clusters = this.clusterLoader.getClusters(root, debugProcess.get());

    IProgress partitioningProgress = debugProcess.get();
    partitioningProgress.startProgress("creating partitions ...", clusters.size());
    for (Cluster cluster : clusters) {
      try {
        this.clusterStorage.createClusterPartition(cluster, IgnoringProgress.INSTANCE);
        partitioningProgress.continueProgress();
      } catch (SQLException ex) {
        logger.error("unable to create partition for cluster " + cluster.getId() + "@"
            + cluster.getDataSource().getLabel() + ":" + cluster.getDataSource().getUserView(), ex);
      }
    }
    partitioningProgress.stopProgress();
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
