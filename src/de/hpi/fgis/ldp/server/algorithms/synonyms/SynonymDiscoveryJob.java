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

package de.hpi.fgis.ldp.server.algorithms.synonyms;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.CorrelatedPair;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions.CorrelationCoefficient;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Pair;
import de.hpi.fgis.ldp.shared.data.SynonymPairModel;
import de.hpi.fgis.ldp.shared.data.SynonymSetModel;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;

/**
 * this is the job implementation for long running association rule calculation tasks
 * 
 * @author toni.gruetze
 * 
 */
public class SynonymDiscoveryJob extends MonitoredJob<SynonymSetModel> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final Provider<AdvancedARF> finder;
  private final IMetaLoader metaLoader;

  private Cluster cluster;
  private int setSize;
  private double support;
  private double correlation;
  private int maxAmount;

  private SynonymSetModel result;

  @Inject
  protected SynonymDiscoveryJob(Log logger, JobNameSource jobNameSource,
      Provider<AdvancedARF> finder, IMetaLoader metaLoader) {
    this.logger = logger;
    this.jobNameSource = jobNameSource;
    this.finder = finder;
    this.metaLoader = metaLoader;
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
    logger.debug("Managing association rule request for cluster " + cluster.getId() + " to "
        + cluster.getLabel());

    try {

      this.result = new SynonymSetModel(this.findSynonyms(progress));
    } catch (Exception cause) {
      logger.error("Unable to get Synonyms " + cluster.getId(), cause);

      throw cause;
    } finally {
      progress.stopProgress();
    }
  }

  private ArrayList<SynonymPairModel> findSynonyms(IProgress progress) {
    progress.startProgress("Calculating synonyms ...", 100);
    Vector<SynonymPairModel> synonymPairMap = new Vector<SynonymPairModel>();
    ArrayList<SynonymPairModel> synonyms = new ArrayList<SynonymPairModel>();
    // run the Apriori Algorithm in the OP configuration to identify range
    // similar predicates
    // given parameters support and set size
    AdvancedARF rangeARF = this.finder.get();
    rangeARF.setCluster(cluster, progress.continueWithSubProgress(10), "op");
    ArrayList<IItemSetList> candidates =
        rangeARF.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "op");

    if (candidates == null || candidates.isEmpty()) {
      return synonyms;
    }
    // run the Apriori Algorithm in the SP configuration to identify
    // exclusive occuring predicates
    // given parameters support and set size
    AdvancedARF arf = this.finder.get();

    arf.setCluster(cluster, progress.continueWithSubProgress(10), "sp");

    ArrayList<IItemSetList> schemaPatterns =
        arf.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "sp");
    int entityCount = arf.getBasketCount();

    logger.info("Look for exclusivity");
    CorrelationEvaluator matchingEvaluator =
        new CorrelationEvaluator(candidates.get(0), correlation, entityCount,
            new CorrelationCoefficient());

    ArrayList<CorrelatedPair> pairs =
        matchingEvaluator.getMatchingValues(schemaPatterns.get(0), arf.getLargeOneItemSets());

    int[] items = matchingEvaluator.getItems();

    final TIntObjectHashMap<String> allPredicates = new TIntObjectHashMap<String>();
    for (int item : items) {
      allPredicates.put(item, null);

    }

    final TIntObjectHashMap<String> allObjects = new TIntObjectHashMap<String>();
    for (CorrelatedPair pair : pairs) {
      pair.setObjects(rangeARF);
      TIntIterator objects = pair.getObjects().iterator();
      while (objects.hasNext()) {
        int id = objects.next();
        if (!allObjects.containsKey(id)) {
          allObjects.put(id, null);
        }
      }
    }

    this.metaLoader.fillTIDNameMap(allObjects, "op", cluster.getDataSource());
    this.metaLoader.fillTransactionNameMap(allPredicates, "sp", cluster.getDataSource());

    for (CorrelatedPair pair : pairs) {
      TIntIterator objectsIterator = pair.getObjects().iterator();
      DataColumn<String> subjectColumn = new DataColumn<String>("Subject", true);
      DataColumn<String> predicateColumn = new DataColumn<String>("Predicate", true);
      DataColumn<String> objColumn = new DataColumn<String>("Object", true);
      DataTable dt = new DataTable(subjectColumn);
      dt.addColumn(predicateColumn);
      dt.addColumn(objColumn);
      int[] pairItems = pair.getItems();
      String firstPredicate = allPredicates.get(pairItems[0]);
      String secondPredicate = allPredicates.get(pairItems[1]);
      int rowIndex = 0;
      int objectCount = 0;
      while (objectsIterator.hasNext() && objectCount < 5) {
        ++objectCount;
        int id = objectsIterator.next();
        String objectValue = allObjects.get(id);
        Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>> resultPair =
            this.metaLoader.getSynonymDetails(pair.getItems(), id, cluster.getDataSource());

        for (String subjectString : resultPair.getFirstElem().valueCollection()) {
          subjectColumn.setElement(rowIndex, subjectString);
          predicateColumn.setElement(rowIndex, firstPredicate);
          objColumn.setElement(rowIndex, objectValue);

          ++rowIndex;
          if (rowIndex % 5 == 0) {
            break;
          }
        }
        // SECOND PREDICATRES VALUES
        for (String subjectString : resultPair.getSecondElem().valueCollection()) {
          subjectColumn.setElement(rowIndex, subjectString);
          predicateColumn.setElement(rowIndex, secondPredicate);
          objColumn.setElement(rowIndex, objectValue);

          ++rowIndex;
          if (rowIndex % 5 == 0) {
            break;
          }
        }
      }

      SynonymPairModel synonymModel =
          new SynonymPairModel(firstPredicate, secondPredicate, pair.getCCoefficient(),
              pair.getFrequency(), dt);
      synonymPairMap.add(synonymModel);
    }

    // FIXME from to functionality needed instead!
    int maxAmount = this.maxAmount;
    for (SynonymPairModel tmp : synonymPairMap) {
      if (maxAmount-- > 0) {
        synonyms.add(tmp);
      } else {
        break;
      }
    }

    progress.stopProgress();

    return synonyms;
  }

  @Override
  public String getName() {
    return jobNameSource.getARJobName(this.cluster);
  }

  @Override
  public SynonymSetModel getResult() {
    return result;
  }
}
