package de.hpi.fgis.ldp.server.algorithms.ontologyAligment;

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

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.OntologyAlignmentModel;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;

/**
 * this is the job implementation for long running statement generation
 * 
 * @author ziawasch.abedjan
 * 
 */
public class OntologyAlignmentJob extends MonitoredJob<OntologyAlignmentModel> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final IMetaLoader metaLoader;

  private Cluster cluster;

  private OntologyAlignmentModel result;

  private final IEntitySchemaLoader entityLoader;

  @Inject
  protected OntologyAlignmentJob(Log logger, JobNameSource jobNameSource,
      IEntitySchemaLoader entityLoader, IMetaLoader metaLoader) {
    this.logger = logger;
    this.jobNameSource = jobNameSource;
    this.metaLoader = metaLoader;
    this.entityLoader = entityLoader;
  }

  public void init(Cluster cluster) {
    this.cluster = cluster;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.MonitoredJob#execute()
   */
  @Override
  public void execute() throws Exception {
    final IProgress progress = this.getProgress();
    logger.debug("Retrieving Ontology Improvements for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {

      this.result = this.retrieveAlignment(progress);
    } catch (Exception cause) {
      logger.error("Unable to get new facts " + cluster.getId(), cause);

      throw cause;
    } finally {
      progress.stopProgress();
    }
  }

  private OntologyAlignmentModel retrieveAlignment(IProgress progress) {
    progress.startProgress("Retrieving alignment Proposals ...", 100);
    DataColumn<Predicate> existingschema =
        new DataColumn<Predicate>("Properties by the Ontology", true);
    DataColumn<Predicate> removedSchema = new DataColumn<Predicate>("Removed Properties", true);

    DataColumn<Predicate> inclusionProperties =
        new DataColumn<Predicate>("Suggested Properties", true);
    DataColumn<String> sourceClass = new DataColumn<String>("Source Class", true);
    DataColumn<String> sourceClassRemoved = new DataColumn<String>("Source Class", true);
    try {
      TIntIntHashMap cToSMapping =
          entityLoader.getClusterIdMappings(cluster.getDataSource(), progress, "cs");
      TIntIntHashMap predicateToSource =
          entityLoader.getPredicateDomain(cluster.getDataSource(), progress);
      int s_id = cToSMapping.get(cluster.getId());
      TIntObjectHashMap<String> predicate_ids = new TIntObjectHashMap<String>();
      TIntObjectHashMap<String> source_ids = new TIntObjectHashMap<String>();
      for (int predicate : predicateToSource.keys()) {
        if (predicateToSource.get(predicate) == s_id) {
          predicate_ids.put(predicate, null);
        }
      }
      for (int sourceType : predicateToSource.values()) {
        source_ids.put(sourceType, null);
      }
      metaLoader.fillTIDNameMap(predicate_ids, "sp", cluster.getDataSource());
      metaLoader.fillTIDNameMap(source_ids, "sp", cluster.getDataSource());
      int rowIndex = 0;
      for (int predicate_id : predicate_ids.keys()) {
        existingschema.setElement(rowIndex++, new Predicate(predicate_ids.get(predicate_id)));

      }

      rowIndex = 0;
      TIntObjectHashMap<String> removedProperties =
          entityLoader.getRemovedProperties(cluster.getDataSource(), progress, s_id);
      for (int predicate_id : removedProperties.keys()) {
        removedSchema.setElement(rowIndex, new Predicate(removedProperties.get(predicate_id)));
        sourceClassRemoved
            .setElement(rowIndex, source_ids.get(predicateToSource.get(predicate_id)));
        ++rowIndex;
      }

      rowIndex = 0;
      Map<String, String> addedProperties =
          entityLoader.getAddedProperties(cluster.getDataSource(), progress, s_id);
      for (String predicate : addedProperties.keySet()) {
        inclusionProperties.setElement(rowIndex, new Predicate(predicate));
        sourceClass.setElement(rowIndex, addedProperties.get(predicate));
        ++rowIndex;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return new OntologyAlignmentModel(existingschema, removedSchema, inclusionProperties,
        sourceClass, sourceClassRemoved);
  }

  @Override
  public String getName() {
    return jobNameSource.getARJobName(this.cluster);
  }

  @Override
  public OntologyAlignmentModel getResult() {
    return result;
  }
}
