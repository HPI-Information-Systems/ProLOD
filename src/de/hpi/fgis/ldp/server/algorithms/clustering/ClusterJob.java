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

package de.hpi.fgis.ldp.server.algorithms.clustering;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * this is the job implementation for long running clustering tasks
 * 
 * @author toni.gruetze
 * 
 */
public class ClusterJob extends MonitoredJob<Cluster> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final ClusterAlgorithmRepository clusterAlgorithmRepository;
  private final ISchemaStorage schemaStorage;
  private final IClusterStorage clusterStorage;

  private Cluster cluster;
  private IClusterConfig config;
  private Cluster result;

  @Inject
  protected ClusterJob(Log logger, JobNameSource jobNameSource,
      ClusterAlgorithmRepository clusterAlgorithmRepository, ISchemaStorage schemaStorage,
      IClusterStorage clusterStorage) {
    this.logger = logger;
    this.jobNameSource = jobNameSource;
    this.clusterAlgorithmRepository = clusterAlgorithmRepository;
    this.schemaStorage = schemaStorage;
    this.clusterStorage = clusterStorage;
  }

  public void init(Cluster cluster, IClusterConfig configuration) {
    this.config = configuration;
    this.cluster = cluster;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.MonitoredJob#execute()
   */
  @Override
  public void execute() throws Exception {
    logger.debug("Clustering called for cluster " + this.cluster + ". Using " + this.config + ".");

    final IProgress progress = this.getProgress();

    progress.startProgress("Starting cluster run...", 100);
    try {
      // choose Cluster algorithm & call cluster algorithm
      final Session session =
          this.clusterAlgorithmRepository.getAlgorithm(this.config).cluster(this.cluster,
              this.config, progress.continueWithSubProgress(89));

      this.cluster.setChildSessionID(session.getId());

      // progress 99%
      progress.continueProgressAt(89, "Clustering finished!");

      // publish root cluster session & create partitions
      if (this.cluster.getId() < 0) {
        // progress of 1% of the overall process
        schemaStorage.publishRootSession(session, progress.continueWithSubProgress(1));

        // progress 90%
        progress.continueProgressAt(90, "Root partition published!");

        // progress of 10% of the overall process
        IProgress partitioningProgress = progress.continueWithSubProgress(10);
        partitioningProgress.startProgress("Creating partitions ...", session.getEntityClusters()
            .size());
        for (EntityCluster cluster : session.getEntityClusters()) {
          this.clusterStorage.createClusterPartition(cluster.getMetaData(),
              partitioningProgress.continueWithSubProgress(1));
        }
        partitioningProgress.stopProgress();

        progress.continueProgressAt(100, "Partitions created!");
      } else {
        progress.continueProgressAt(100);
      }

      this.result = this.cluster;

      // progress finished
      this.result.setProgressIdentifier(null);
    } catch (Exception cause) {
      logger.error("Unable to (re-)cluster " + this.cluster.getId(), cause);

      throw cause;
    } finally {
      logger.debug("Finished clustering.");
      progress.stopProgress();
    }
  }

  @Override
  public String getName() {
    return jobNameSource.getJobName(this.cluster);
  }

  @Override
  public Cluster getResult() {
    return result;
  }
}
