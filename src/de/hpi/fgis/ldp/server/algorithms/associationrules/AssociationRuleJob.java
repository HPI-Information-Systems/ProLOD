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

package de.hpi.fgis.ldp.server.algorithms.associationrules;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.util.job.JobNameSource;
import de.hpi.fgis.ldp.server.util.job.MonitoredJob;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.ARSetModel;
import de.hpi.fgis.ldp.shared.data.AssociationRuleModel;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * this is the job implementation for long running association rule calculation tasks
 * 
 * @author toni.gruetze
 * 
 */
public class AssociationRuleJob extends MonitoredJob<ARSetModel> {
  private final Log logger;

  private final JobNameSource jobNameSource;
  private final Provider<AdvancedARF> finder;
  private final IMetaLoader metaLoader;

  private Cluster cluster;
  private int setSize;
  private boolean positiveRulesOnly;
  private double support;
  private double confidence;
  private double correlation;
  private int maxAmount;

  private ARSetModel result;

  private String ruleConfiguration;

  @Inject
  protected AssociationRuleJob(Log logger, JobNameSource jobNameSource,
      Provider<AdvancedARF> finder, IMetaLoader metaLoader) {
    this.logger = logger;
    this.jobNameSource = jobNameSource;
    this.finder = finder;
    this.metaLoader = metaLoader;
  }

  public void init(Cluster cluster, String ruleConfiguration) {
    this.init(cluster, 2, true, 0.001, 0.9, 0.5, 100, ruleConfiguration);
  }

  public void init(Cluster cluster) {
    this.init(cluster, "sp");
  }

  public void init(Cluster cluster, int setSize, boolean positiveRulesOnly, double support,
      double confidence, double correlation, int maxAmount, String ruleConfiguration) {
    this.cluster = cluster;
    this.setSize = setSize;
    this.positiveRulesOnly = positiveRulesOnly;
    this.support = support;
    this.confidence = confidence;
    this.correlation = correlation;
    this.maxAmount = maxAmount;
    this.ruleConfiguration = ruleConfiguration;
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

      this.result = new ARSetModel(this.findRules(progress));
    } catch (Exception cause) {
      logger.error("Unable to get Association rules " + cluster.getId(), cause);

      throw cause;
    } finally {
      progress.stopProgress();
    }
  }

  private ArrayList<AssociationRuleModel> findRules(IProgress progress) {
    progress.startProgress("Calculating association rules ...", 100);
    ArrayList<AssociationRule> ruleMap = new ArrayList<AssociationRule>();
    Vector<AssociationRuleModel> ruleModelMap = new Vector<AssociationRuleModel>();
    // run the Apriori Algorithm on the selected ClusterDetails with the
    // given parameters support and set size
    AdvancedARF arf = this.finder.get();
    arf.setCluster(cluster, progress.continueWithSubProgress(10), ruleConfiguration);
    arf.findLargeItemSets(support, setSize, progress.continueWithSubProgress(75), ruleConfiguration);
    if (positiveRulesOnly) {
      // all possible rules that satisfy the given confidence and
      // correlation coefficient are generated and added to the transport
      // Model.
      ruleMap = arf.generateAssociationRules(confidence, correlation);

    } else {
      // all possible NEGATIVE rules that satisfy the given confidence and
      // correlation coefficient are generated and added to the transport
      // Model.
      ruleMap = arf.generateNegativeRules(confidence, -correlation);
      // for(AssociationRule rule :ruleMap){
      // AssociationRuleModel ruleModel = new
      // AssociationRuleModel(rule.getConditionAsString(),
      // rule.getConsequenceAsString(), rule.getConfidence(),
      // rule.getCorrelationCoefficient());
      // ruleModelMap.add(ruleModel);
      // }
    }

    final TIntObjectHashMap<String> allSubjects = new TIntObjectHashMap<String>();
    for (AssociationRule rule : ruleMap) {
      TIntIterator subjects = rule.getSubjects().iterator();
      while (subjects.hasNext()) {
        int id = subjects.next();
        if (!allSubjects.containsKey(id)) {
          allSubjects.put(id, null);
        }
      }
    }
    final TIntObjectHashMap<String> allPredicates = new TIntObjectHashMap<String>();
    for (AssociationRule rule : ruleMap) {
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
    }

    this.metaLoader.fillTIDNameMap(allSubjects, ruleConfiguration, cluster.getDataSource());

    this.metaLoader.fillTransactionNameMap(allPredicates, ruleConfiguration,
        cluster.getDataSource());

    for (AssociationRule rule : ruleMap) {
      Vector<String> subjects = new Vector<String>(rule.getSubjects().size());
      TIntIterator subjectsIterator = rule.getSubjects().iterator();
      while (subjectsIterator.hasNext()) {
        int id = subjectsIterator.next();
        if (allSubjects.containsKey(id)) {
          subjects.add(allSubjects.get(id));
        }
      }

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
          new AssociationRuleModel(condition.toString(), consequence.toString(),
              rule.getConfidence(), rule.getCorrelationCoefficient(), subjects,
              rule.getFrequency(), rule.getSize());
      ruleModelMap.add(ruleModel);
    }

    ArrayList<AssociationRuleModel> rules = new ArrayList<AssociationRuleModel>();
    // FIXME from to functionality needed instead!
    int maxAmount = this.maxAmount;
    for (AssociationRuleModel tmp : ruleModelMap) {
      if (maxAmount-- > 0) {
        rules.add(tmp);
      } else {
        break;
      }
    }

    progress.stopProgress();

    return rules;
  }

  @Override
  public String getName() {
    return jobNameSource.getARJobName(this.cluster);
  }

  @Override
  public ARSetModel getResult() {
    return result;
  }
}
