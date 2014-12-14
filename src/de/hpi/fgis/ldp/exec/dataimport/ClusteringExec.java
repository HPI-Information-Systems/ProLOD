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

import org.apache.commons.logging.Log;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.exec.guice.DefaultModule;
import de.hpi.fgis.ldp.exec.optimize.OptimizeDB;
import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterAlgorithmRepository;
import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.emulation.DBUsageEmulation;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.progress.DebugProgress;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.RecursiveKMeansConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class ClusteringExec {
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    final Injector injector = Guice.createInjector(new DefaultModule());
    final ClusteringExec main = injector.getInstance(ClusteringExec.class);

    System.err.println("parameters: <schema name>");

    main.runFor(args[0]);
    OptimizeDB.main(new String[] {args[1]});
  }

  @Inject
  private Log logger;
  private final IClusterStorage clusterStorage;
  private final ClusterAlgorithmRepository clusterAlgorithmRepository;
  private final DBUsageEmulation usageEmulation;
  private final ISchemaStorage schemaStorage;
  private final Provider<DebugProgress> debugProcess;

  /**
   * @param clusterLoader
   */
  @Inject
  public ClusteringExec(ISchemaStorage schemaStorage, IClusterStorage clusterStorage,
      ClusterAlgorithmRepository algorithmRepository, DBUsageEmulation usageEmulation,
      Provider<DebugProgress> debugProcess) {
    this.schemaStorage = schemaStorage;
    this.clusterStorage = clusterStorage;
    this.usageEmulation = usageEmulation;
    this.clusterAlgorithmRepository = algorithmRepository;
    this.debugProcess = debugProcess;
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

    progress.continueProgressAt(710, "Cleanup successful!");
    // ===[initial clustering&labeling]===

    logger.info("Starting clustering & labeling.");

    RecursiveKMeansConfig clusterConfig = new RecursiveKMeansConfig();

    clusterConfig.setNumberOfClusters(20, 10, 5, 5, 5);
    clusterConfig.setAbortOnError(0.35);
    clusterConfig.setAbortOnSize(1000);

    IClusterAlgorithm clustering = clusterAlgorithmRepository.getAlgorithm(clusterConfig);

    final Cluster rootCluster = new Cluster(new DataSource(schemaName));
    final Session session =
        clustering.cluster(rootCluster, clusterConfig, progress.continueWithSubProgress(90));
    rootCluster.setChildSessionID(session.getId());

    // progress 80%
    progress.continueProgressAt(800, "Clustering and labeling finished!");

    schemaStorage.publishRootSession(session, progress.continueWithSubProgress(10));

    // progress 81%
    progress.continueProgressAt(810, "Root cluster published!");

    // progress of 15% of the overall process
    IProgress partitioningProgress = progress.continueWithSubProgress(150);
    partitioningProgress.startProgress("creating partitions ...", session.getEntityClusters()
        .size());
    for (EntityCluster cluster : session.getEntityClusters()) {
      this.clusterStorage.createClusterPartition(cluster.getMetaData(),
          partitioningProgress.continueWithSubProgress(1));
    }
    partitioningProgress.stopProgress();

    // progress 96%
    progress.continueProgressAt(960, "Cluster partitions created!");

    this.usageEmulation.runFor(rootCluster.getDataSource().getLabel());

    progress.continueProgressAt(1000);

    progress.stopProgress();

    System.err.println("end: " + new Date());
  }

}
