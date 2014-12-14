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

package de.hpi.fgis.ldp.server.algorithms.dataimport;

import java.io.File;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.algorithms.clustering.ClusterAlgorithmRepository;
import de.hpi.fgis.ldp.server.algorithms.clustering.IClusterAlgorithm;
import de.hpi.fgis.ldp.server.algorithms.emulation.DBUsageEmulation;
import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.persistency.storage.IDataImport;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.config.clustering.OntologyConfig;
import de.hpi.fgis.ldp.shared.config.clustering.RecursiveKMeansConfig;
import de.hpi.fgis.ldp.shared.config.dataimport.FileType;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * this is the job implementation for long running import tasks
 * 
 * @author toni.gruetze
 * 
 */
public class ImportJob extends MonitoredJob<Cluster> {

  private DataSource dataSource;
  private String schema;
  private String label;
  private String fileName;
  private FileType fileType;
  private boolean replace = false;
  private Cluster rootCluster;
  private Cluster ontologyRootCluster;

  @Inject
  private Log logger;

  private final JobNameSource jobNameSource;
  private final ISchemaStorage schemaStorage;
  private final IDataImport dataImport;
  private final Provider<ImportEntryIterable> iterableProvider;
  private final IClusterStorage clusterStorage;
  private final IClusterLoader clusterLoader;
  private final ClusterAlgorithmRepository clusterAlgorithmRepository;

  private final DBUsageEmulation usageEmulation;

  private final String defaultUserView;
  private final String ontologyUserView;

  @Inject
  protected ImportJob(ISchemaStorage schemaStorage, IDataImport dataImport,
      Provider<ImportEntryIterable> iterableProvider, IClusterStorage clusterStorage,
      IClusterLoader clusterLoader, JobNameSource jobNameSource,
      ClusterAlgorithmRepository algorithmRepository, DBUsageEmulation usageEmulation,
      @Named("db.defaultUserView") String defaultUserView,
      @Named("db.ontologyUserView") String ontologyUserView) {
    this.schemaStorage = schemaStorage;
    this.dataImport = dataImport;
    this.iterableProvider = iterableProvider;
    this.clusterStorage = clusterStorage;
    this.clusterLoader = clusterLoader;
    this.jobNameSource = jobNameSource;
    this.usageEmulation = usageEmulation;
    this.clusterAlgorithmRepository = algorithmRepository;
    this.defaultUserView = defaultUserView;
    this.ontologyUserView = ontologyUserView;
  }

  @Inject
  void setReplaceSchema(@Named("import.replaceSchemas") boolean replace) {
    this.replace = replace;
  }

  public void init(String schema, String label, String fileName, FileType fileType) {
    this.init(schema, label, fileName, fileType, this.replace);
  }

  public void init(String schema, String label, String fileName, FileType fileType, boolean replace) {
    this.dataSource = new DataSource(schema);
    this.schema = schema.toUpperCase();
    this.label = label;
    this.fileName = fileName;
    this.fileType = fileType;
    this.replace = replace;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.MonitoredJob#execute()
   */
  @Override
  public void execute() throws Exception {
    final IProgress progress = this.getProgress();
    progress.startProgress("Importing ...", 1000);

    if (this.replace) {
      // drop existing schema
      try {
        this.schemaStorage.dropSchema(this.schema, progress.continueWithSubProgress(1));
      } catch (Exception e) {
        // ignore
        logger.warn(e.getMessage());
      }
    }

    // create import table structure (subject, predicate, NormPattern,
    // Pattern, Datatype, parsed value)
    // read from file
    // analyze pattern
    // create insert statement (subject, predicate, NormPattern, Pattern,
    // Datatype, parsed value)
    this.schema =
        this.schemaStorage.createSchema(this.schema, this.label,
            progress.continueWithSubProgress(1));

    // 0.2%
    progress.continueProgressAt(2, "Schema successfully created!");

    this.dataImport.setSchemaName(this.schema);

    final ImportEntryIterable importIterable = this.iterableProvider.get();
    importIterable.setPath(this.fileName);
    importIterable.setFileType(this.fileType);
    importIterable.setProgress(progress.continueWithSubProgress(498));
    try {
      this.dataImport.store(importIterable);
    } catch (SQLException e) {
      logger.warn("Unable to read and import file " + this.fileName);
      throw (e);
    }

    // progress 50%
    progress.continueProgressAt(500, "Temporary import data successfully created!");
    // create dictionaries (mqts?): subject, predicate, object, NormPattern,
    // Pattern (Datatype)
    // create indices on dictionaries
    this.dataImport.createMetaTables(progress.continueWithSubProgress(50));
    // progress 55%
    progress.continueProgressAt(550, "Dictionaries successfully created!");

    // create maintable mqt with subject_id, predicate_id, NormPattern_id,
    // Pattern_id, Datatype, parsed value, internal link(subject_id)
    // create indices on maintable
    this.dataImport.createMainTable(progress.continueWithSubProgress(100));
    // progress 65%
    progress.continueProgressAt(650, "Maintable successfully created!");

    // create other MQTs (links, linked subjects, textdata)
    // create indices on mqts (auf textdata achten)
    this.dataImport.createMaterializedViews(progress.continueWithSubProgress(50));
    // progress 70%
    progress.continueProgressAt(700, "Materialized views successfully created!");

    // create cluster tables
    this.dataImport.createClusterTables(progress.continueWithSubProgress(5));
    // progress 70.5%
    progress.continueProgressAt(705, "Cluster tables successfully created!");

    // drop temp tables
    this.dataImport.cleanup(progress.continueWithSubProgress(5));
    // progress 71%
    progress.continueProgressAt(710, "Cleanup successful!");

    // ===[initial clustering&labeling]===

    logger.info("Starting clustering & labeling.");

    Cluster rootCluster = new Cluster(this.dataSource.asUserView(this.defaultUserView));

    RecursiveKMeansConfig clusterConfig = RecursiveKMeansConfig.getDefaultConfig();

    createClustering(clusterConfig, progress, rootCluster);

    OntologyConfig ontologyClusterConfig = OntologyConfig.getDefaultConfig();

    // change user view
    Cluster ontologyRootCluster = new Cluster(this.dataSource.asUserView(this.ontologyUserView));

    this.ontologyRootCluster =
        createClustering(ontologyClusterConfig, progress, ontologyRootCluster);

    // TODO what if no rootCluster in ontology? fallback to kmeans

    progress.stopProgress();
  }

  private Cluster createClustering(IClusterConfig clusterConfig, final IProgress progress,
      Cluster rootCluster) throws Exception, SQLException {
    IClusterAlgorithm clustering = clusterAlgorithmRepository.getAlgorithm(clusterConfig);

    final Session session =
        clustering.cluster(rootCluster, clusterConfig, progress.continueWithSubProgress(90));
    if ((session != null) && (session.getEntityClusters() != null)
        && (session.getEntityClusters().size() > 0)) {
    }
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

    return rootCluster;
  }

  private Cluster getRootCluster() {
    final DataSource schema = new DataSource(this.schema);
    return new Cluster(schema);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.AbstractJob#cleanup()
   */
  @Override
  public void cleanup() {
    // delete tmp file
    File f = new File(this.fileName);
    if (!f.delete()) {
      logger.error("Unable to delete temporary file for \"" + this.schema + "\" (" + this.fileName
          + ")");
    } else {
      logger.debug("Temporary file for \"" + this.schema + "\" (" + this.fileName
          + ") successfully deleted!");
    }
    // TODO evtl. stop progress
    super.cleanup();
  }

  @Override
  public String getName() {
    return jobNameSource.getJobName(this.getRootCluster());
  }

  @Override
  public Cluster getResult() {
    return rootCluster;
  }
}
