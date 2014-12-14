package de.hpi.fgis.ldp.server.algorithms.factgeneration;

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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.SuggestionSetModel;

/**
 * this is the job implementation for long running statement generation
 * 
 * @author ziawasch.abedjan
 * 
 */
public class SuggestionJob extends MonitoredJob<SuggestionSetModel> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final Provider<AdvancedARF> finder;
  private final IMetaLoader metaLoader;

  private Cluster cluster;
  private int setSize;
  private double support;
  private double correlation;
  private int maxAmount;

  private SuggestionSetModel result;

  @Inject
  protected SuggestionJob(Log logger, JobNameSource jobNameSource, Provider<AdvancedARF> finder,
      IMetaLoader metaLoader) {
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

  private SuggestionSetModel generateFacts(IProgress progress) {
    progress.startProgress("Calculating Suggestions ...", 100);

    // DataColumn<Subject> subject = new DataColumn<Subject>("Subject", true);
    // DataColumn<Predicate> predicate = new DataColumn<Predicate>("Predicate", true);
    // DataColumn<ObjectValue> object = new DataColumn<ObjectValue>("Object", true);

    // TODO return model

    // run the Apriori Algorithm in the SO configuration
    logger.info("Retrieve object rules");
    AdvancedARF objectARF = this.finder.get();
    objectARF.setCluster(cluster, progress.continueWithSubProgress(10), "so");

    objectARF.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "so");
    ArrayList<AssociationRule> objectRules = objectARF.generateAssociationRules(0.3, 0.0);

    // if (objectRules.isEmpty()) {
    // return new SuggestionSetModel(new ArrayList<AssociationRuleModel>(),
    // new ArrayList<AssociationRuleModel>(), new ArrayList<Subject>());
    // }
    // run the Apriori Algorithm in the SP configuration

    logger.info("Retrieve Predicate rules");
    AdvancedARF predicatesARF = this.finder.get();
    predicatesARF.setCluster(cluster, progress.continueWithSubProgress(10), "sp");

    predicatesARF.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), "sp");
    ArrayList<AssociationRule> predicateRules = predicatesARF.generateAssociationRules(0.3, 0.0);

    // if (predicateRules.isEmpty()) {
    // return new SuggestionSetModel(new ArrayList<AssociationRuleModel>(),
    // new ArrayList<AssociationRuleModel>(), new ArrayList<Subject>());
    // }

    final TIntObjectHashMap<String> relevantSubjectsForObjects = new TIntObjectHashMap<String>();
    // final TIntObjectHashMap<String> relevantSubjectsForPredicates = new
    // TIntObjectHashMap<String>();
    final TIntObjectHashMap<String> allObjects = new TIntObjectHashMap<String>();
    for (AssociationRule rule : objectRules) {
      TIntIterator condition = rule.getCondition().iterator();
      while (condition.hasNext()) {
        int id = condition.next();
        if (!allObjects.containsKey(id)) {
          allObjects.put(id, null);
        }
      }
      TIntIterator consequence = rule.getConsequence().iterator();
      while (consequence.hasNext()) {
        int id = consequence.next();
        if (!allObjects.containsKey(id)) {
          allObjects.put(id, null);
        }
      }
      retrieveRelevantSubjects(relevantSubjectsForObjects, rule, objectARF);

    }

    final TIntObjectHashMap<String> allPredicates = new TIntObjectHashMap<String>();
    for (AssociationRule rule : predicateRules) {
      TIntIterator condition = rule.getCondition().iterator();
      while (condition.hasNext()) {
        int id = condition.next();
        if (!allPredicates.containsKey(id)) {
          allPredicates.put(id, null);
        }
      }
      TIntIterator consequence = rule.getConsequence().iterator();
      while (consequence.hasNext()) {
        int id = consequence.next();
        if (!allPredicates.containsKey(id)) {
          allPredicates.put(id, null);
        }
      }
      retrieveRelevantSubjects(relevantSubjectsForObjects, rule, predicatesARF);

    }

    // for (int id : relevantSubjectsForPredicates.keys()){
    // if (!relevantSubjectsForObjects.containsKey(id)){
    // relevantSubjectsForObjects.remove(id);
    // }
    // }

    // TODO fill Sugestionresult;
    this.metaLoader.fillTransactionNameMap(allObjects, "so", cluster.getDataSource());
    this.metaLoader.fillTIDNameMap(relevantSubjectsForObjects, "sp", cluster.getDataSource());
    this.metaLoader.fillTransactionNameMap(allPredicates, "sp", cluster.getDataSource());
    // relevant subjects
    Vector<Subject> subjects = new Vector<Subject>(relevantSubjectsForObjects.size());
    int maxCount = 0;
    for (int id : relevantSubjectsForObjects.keys()) {
      if (maxCount >= maxAmount) {
        break;
      }
      ++maxCount;
      subjects.add(new Subject(id, relevantSubjectsForObjects.get(id)));
    }
    // build objects
    Vector<AssociationRuleModel> objectRuleModel = new Vector<AssociationRuleModel>();
    for (AssociationRule rule : objectRules) {
      ArrayList<String> condition = new ArrayList<String>(rule.getCondition().size());
      TIntIterator conditionit = rule.getCondition().iterator();
      while (conditionit.hasNext()) {
        int id = conditionit.next();
        if (allObjects.containsKey(id)) {
          condition.add(allObjects.get(id));
        }
      }

      ArrayList<String> consequence = new ArrayList<String>(rule.getConsequence().size());
      TIntIterator consequenceit = rule.getConsequence().iterator();
      while (consequenceit.hasNext()) {
        int id = consequenceit.next();
        if (allObjects.containsKey(id)) {
          consequence.add(allObjects.get(id));
        }
      }

      AssociationRuleModel ruleModel =
          new AssociationRuleModel(condition.get(0).toString(), consequence.get(0).toString(),
              rule.getConfidence(), rule.getCorrelationCoefficient(), new Vector<String>(0),
              rule.getFrequency(), rule.getSize());
      objectRuleModel.add(ruleModel);
    }

    // build predicates
    Vector<AssociationRuleModel> predicateRuleModel = new Vector<AssociationRuleModel>();
    for (AssociationRule rule : predicateRules) {
      ArrayList<String> condition = new ArrayList<String>(rule.getCondition().size());
      TIntIterator conditionit = rule.getCondition().iterator();
      while (conditionit.hasNext()) {
        int id = conditionit.next();
        if (allPredicates.containsKey(id)) {
          condition.add(allPredicates.get(id));
        }
      }

      ArrayList<String> consequence = new ArrayList<String>(rule.getConsequence().size());
      TIntIterator consequenceit = rule.getConsequence().iterator();
      while (consequenceit.hasNext()) {
        int id = consequenceit.next();
        if (allPredicates.containsKey(id)) {
          consequence.add(allPredicates.get(id));
        }
      }

      AssociationRuleModel ruleModel =
          new AssociationRuleModel(condition.get(0).toString(), consequence.get(0).toString(),
              rule.getConfidence(), rule.getCorrelationCoefficient(), new Vector<String>(0),
              rule.getFrequency(), rule.getSize());
      predicateRuleModel.add(ruleModel);
    }
    return new SuggestionSetModel(predicateRuleModel, objectRuleModel, subjects);
  }

  private void retrieveRelevantSubjects(TIntObjectHashMap<String> allSubjects,
      AssociationRule rule, AdvancedARF objectARF) {
    TIntSet subjectsForBoth = new TIntHashSet(rule.getSubjects());
    // retrieve subjects only based on object rules
    TIntIterator subjectsForCondition =
        objectARF.getTIDlist(rule.getCondition().toArray(), 200).iterator();
    while (subjectsForCondition.hasNext()) {
      int id = subjectsForCondition.next();
      if (!subjectsForBoth.contains(id)) {
        if (!allSubjects.containsKey(id)) {
          allSubjects.put(id, null);
        }
      }
    }

  }

  @Override
  public String getName() {
    return jobNameSource.getARJobName(this.cluster);
  }

  @Override
  public SuggestionSetModel getResult() {
    return result;
  }
}
