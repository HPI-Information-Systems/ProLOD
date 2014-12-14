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

package de.hpi.fgis.ldp.server.algorithms.factgeneration;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.ObjectValue;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;

/**
 * this is the job implementation for long running statement generation
 * 
 * @author ziawasch.abedjan
 * 
 */
public class FactGenerationJob extends MonitoredJob<IDataTable> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final EntityEnricher entityEnricher;
  private final Provider<AdvancedARF> finder;
  private final IMetaLoader metaLoader;

  private Cluster cluster;
  private int setSize;
  private double support;
  private double correlation;
  private int maxAmount;

  private IDataTable result;

  @Inject
  protected FactGenerationJob(Log logger, JobNameSource jobNameSource,
      Provider<AdvancedARF> finder, IMetaLoader metaLoader, EntityEnricher entityEnricher) {
    this.logger = logger;
    this.jobNameSource = jobNameSource;
    this.finder = finder;
    this.metaLoader = metaLoader;
    this.entityEnricher = entityEnricher;
  }

  public void init(Cluster cluster) {
    this.init(cluster, 2, 0.001, 0.0, 100);
  }

  public void init(Cluster cluster, int setSize, double support, double correlation, int maxAmount) {
    this.cluster = cluster;
    this.setSize = setSize;
    this.support = support;
    this.correlation = correlation;
    this.maxAmount = maxAmount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.util.job.MonitoredJob#execute()
   */
  @Override
  public void execute() throws Exception {
    final IProgress progress = this.getProgress();
    logger.debug("Managing Fact generation request for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {

      this.result = this.generateFacts(progress);
    } catch (Exception cause) {
      logger.error("Unable to get new facts " + cluster.getId(), cause);

      throw cause;
    } finally {
      progress.stopProgress();
    }
  }

  private IDataTable generateFacts(IProgress progress) {
    progress.startProgress("Calculating Facts ...", 100);

    DataColumn<Subject> subject = new DataColumn<Subject>("Subject", true);
    DataColumn<Predicate> predicate = new DataColumn<Predicate>("Predicate", true);
    DataColumn<ObjectValue> object = new DataColumn<ObjectValue>("Object", true);

    DataTable resultTable = new DataTable(subject);
    resultTable.addColumn(predicate);
    resultTable.addColumn(object);
    // TODO return model

    // run the Apriori Algorithm in the SO configuration
    logger.info("Retrieve object rules");
    AdvancedARF objectARF = this.finder.get();
    objectARF.setCluster(cluster, progress.continueWithSubProgress(10), "so");

    objectARF.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "so");
    ArrayList<AssociationRule> objectRules = objectARF.generateAssociationRules(0.6, 0.0);

    if (objectRules.isEmpty()) {
      return resultTable;
    }
    // run the Apriori Algorithm in the SP configuration

    logger.info("Retrieve Predicate rules");
    AdvancedARF predicatesARF = this.finder.get();
    predicatesARF.setCluster(cluster, progress.continueWithSubProgress(10), "sp");

    predicatesARF.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "sp");
    ArrayList<AssociationRule> predicateRules = predicatesARF.generateAssociationRules(0.0, 0.0);

    entityEnricher.init(predicateRules, objectRules, cluster);

    logger.info("Launch fact generation");
    TIntList newFactIDs = entityEnricher.enrichData(progress);

    final TIntObjectHashMap<String> allPredicates = new TIntObjectHashMap<String>();

    final TIntObjectHashMap<String> allObjects = new TIntObjectHashMap<String>();
    final TIntObjectHashMap<String> allSubjects = new TIntObjectHashMap<String>();
    TIntIterator factIterator = newFactIDs.iterator();
    while (factIterator.hasNext()) {
      allSubjects.put(factIterator.next(), null);
      if (factIterator.hasNext()) {
        allPredicates.put(factIterator.next(), null);
      }
      if (factIterator.hasNext()) {
        allObjects.put(factIterator.next(), null);
      }
    }

    this.metaLoader.fillTIDNameMap(allObjects, "op", cluster.getDataSource());
    this.metaLoader.fillTIDNameMap(allSubjects, "sp", cluster.getDataSource());
    this.metaLoader.fillTransactionNameMap(allPredicates, "sp", cluster.getDataSource());

    factIterator = newFactIDs.iterator();
    int row = 0;
    while (factIterator.hasNext()) {
      int sID = factIterator.next();
      subject.setElement(row, new Subject(sID, allSubjects.get(sID)));
      if (factIterator.hasNext()) {
        int pID = factIterator.next();
        predicate.setElement(row, new Predicate(pID, allPredicates.get(pID)));
      }
      if (factIterator.hasNext()) {
        int oID = factIterator.next();
        object.setElement(row, new ObjectValue(oID, allObjects.get(oID)));
      }
      ++row;
    }
    return resultTable;
  }

  @Override
  public String getName() {
    return jobNameSource.getARJobName(this.cluster);
  }

  @Override
  public IDataTable getResult() {
    return result;
  }
}
