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

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.AssociationRule;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.BasketList;
import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IItemSetLoader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * This Class is for finding Association Rules using a derived apriori implementation. It offeres
 * customizable methods for finding positive and negative Association Rules.
 * 
 * @author ziawasch.abedjan
 * 
 */

public class AdvancedARF {
  private Cluster cluster;
  @Inject
  private IItemSetLoader ruleLoader;
  @Inject
  private IEntitySchemaLoader schemaLoader;

  @Inject
  private Log logger;

  public AdvancedARF() {
    // nothing to do
  }

  /**
   * Constructor. Initializes the prospective Baskets.
   * 
   * @param args
   */
  public void setCluster(Cluster cluster, IProgress progress, String ruleConfiguration) {
    this.cluster = cluster;
    this.setAllBaskets(progress, ruleConfiguration);
  }

  private IBasketList baskets;

  private IItemSetList largeOneItemSets;

  private IItemSetList smallItemSets;

  private final ArrayList<AssociationRule> ruleMap = new ArrayList<AssociationRule>();

  private final ArrayList<IItemSetList> largeItemSets = new ArrayList<IItemSetList>();

  private final ArrayList<AssociationRule> negativeRuleMap = new ArrayList<AssociationRule>();

  /**
   * This Method finds Large (1 to maxSetSize)-Itemsets holding a support of s. This is done by a
   * modified version of Apriori.
   * 
   * @param minSupp
   * @param maxSetSize
   * @return
   */
  public ArrayList<IItemSetList> findLargeItemSets(double minSupp, int maxSetSize,
      IProgress progress, String ruleConfiguration) {
    progress.startProgress("Searching for item sets ...", 10 + (10 * maxSetSize));
    this.largeItemSets.clear();
    // List of candidate large Itemsets, index+1 referess to the set size of
    // sets in this list
    IItemSetList[] candidateLIS = new IItemSetList[maxSetSize];

    if (this.baskets == null) {
      return null;
    }
    // Deriving minimum Frequency of itemSets from the support and Basket
    // count
    int minFrequency = (int) (this.baskets.getBasketCount() * minSupp);

    if (minFrequency <= 0) {
      minFrequency = 1;
    }
    // Find large 1-itemSets
    this.getOneItemSets(minFrequency, progress.continueWithSubProgress(10), ruleConfiguration);
    if (this.largeOneItemSets == null) {
      return null;
    }
    // usedBaskets are refreshed after every iteration
    IBasketList usedBaskets = this.baskets;

    // list of single predicates with support above minSupp
    candidateLIS[0] = this.largeOneItemSets;

    // mainloop for generating candidate lists per itemset size till
    // maxSetSize
    // This is done iteratively by candidate lists with candidates with the
    // lower set size, starting with those havin setSize=1
    for (int setSize = 1; setSize <= maxSetSize; setSize++) {
      logger.debug(new Date() + ": ===[set size: " + setSize + "]===");
      logger.debug(new Date() + ": ===[counting frequencies]===");

      final int candidateCount = candidateLIS[setSize - 1].getItemSetCount();

      IProgress itemSetProgress = progress.continueWithSubProgress(10);
      itemSetProgress.startProgress("Checking item sets ...", 110);
      // This loop iterates through all candidates of the current
      // candidate list of itemsets containing setSize number of items
      for (int candidateIndex = 0; candidateIndex < candidateCount; candidateIndex++) {
        final int[] currentItemSet = candidateLIS[setSize - 1].getItemSet(candidateIndex);
        final int basketCount = usedBaskets.getBasketCount();
        // For every candidate its support is determined by walking
        // through all baskets that are already used.
        for (int basketIndex = 0; basketIndex < basketCount; basketIndex++) {
          if (usedBaskets.containsAll(basketIndex, currentItemSet)) {
            candidateLIS[setSize - 1].increaseFrequency(candidateIndex);
            // mark basket as used, after any iteration unused
            // baskets are ignored for further iterations
            usedBaskets.setUsedBasket(basketIndex);

          }
        }
        if (candidateIndex % 100 == 0) {
          itemSetProgress.continueProgressAt((int) (candidateIndex * 100.0 / candidateCount));
          logger.debug((int) (candidateIndex * 100.0 / candidateCount) + "%");
        }
      }
      itemSetProgress.stopProgress();
      logger.debug(new Date() + ": ===[prune]===");

      logger.debug("#itemsets: " + candidateLIS[setSize - 1].getItemSetCount());

      if (setSize == 2) {
        // SmallItemSets are stored for finding negative rules
        this.smallItemSets = candidateLIS[setSize - 1].getSmallItemSets(minFrequency);
        if (this.smallItemSets != null) {
          logger.debug("#SmallItemSets: " + this.smallItemSets.getItemSetCount());
        }
      }
      // candidates that have a support under minFrequency that is derived
      // from minSupp are removed
      // prunedCandidates contain all those candidates that hold
      // minFrequency
      IItemSetList prunedCandidates = candidateLIS[setSize - 1].prune(minFrequency);
      if (prunedCandidates != null) {
        logger.debug(" -[pruned]-> " + prunedCandidates.getItemSetCount());
      } else {
        logger.debug(" -[pruned]-> 0");
        // if no candidate of the current setSize holds the minFrequency
        // stop the iteration
        break;
      }

      if (usedBaskets != null) {
        // logs
        logger.debug("#baskets: " + usedBaskets.getBasketCount());
        usedBaskets = usedBaskets.prune();
        logger.debug(" -[pruned]-> " + usedBaskets.getBasketCount());
      } else {
        logger.debug(" -> 0");
      }

      // store the pruned candidates as large itemsets
      if (setSize == 1) {
        this.largeOneItemSets = prunedCandidates;
      } else {
        this.largeItemSets.add(prunedCandidates);
      }
      if (setSize < maxSetSize) {
        logger.debug(new Date() + ": ===[generating candidates]===");
        // Generating new Candidates of Size: setSize
        candidateLIS[setSize] = prunedCandidates.generateCandidates();
        if (candidateLIS[setSize] == null) {
          break;
        }
      }
    }

    progress.stopProgress();
    return this.largeItemSets;
  }

  public IItemSetList getLargeOneItemSets() {
    return largeOneItemSets;
  }

  /**
   * This method generates negative association Rules that hold Confidence of minconf and have a
   * linear dependency of maxRoh using property pairs in SmallItemSets Using this method requires
   * calling findlargeitemSets(minSupp, 2)
   * 
   * @param minConf
   * @param maxRoh
   * @return
   */
  public ArrayList<AssociationRule> generateNegativeRules(double minConf, double maxRoh) {
    this.negativeRuleMap.clear();
    // if there are no smallItemsets, there can be no relevant negative rule
    // because the predicate pairs found are all to large.
    if (this.smallItemSets == null) {
      return this.negativeRuleMap;
    }
    // iterates through all smallitemsets
    for (int i = 0; i < this.smallItemSets.getItemSetCount(); i++) {
      int isSupp = this.smallItemSets.getFrequency(i);
      int[] itemSet = this.smallItemSets.getItemSet(i);
      if (itemSet.length == 2) {

        // split itemSet in its two parts
        int A = itemSet[0];
        int B = itemSet[1];
        int countA = -1;
        int countB = -1;
        // search through all large 1-itemsets for getting the
        // frequencies of A and B
        for (int j = 0; j < this.largeOneItemSets.getItemSetCount(); j++) {
          // index [0] is required becase getitemSet returns for
          // 1-Itemsets an array too
          if (this.largeOneItemSets.getItemSet(j)[0] == A) {
            countA = this.largeOneItemSets.getFrequency(j);
          } else if (this.largeOneItemSets.getItemSet(j)[0] == B) {
            countB = this.largeOneItemSets.getFrequency(j);
          }
          // if both frequencies are found break
          if (countB > -1 && countA > -1) {
            break;
          }
        }

        // determine the correlation coefficient roh of the itemset
        double roh = this.computeRoh(countA, countB, isSupp);
        if (roh < maxRoh) {
          // determine the confidence currentConf of the rule A->B
          double currentConf = (double) (countA - isSupp) / countA;
          // create a new rule if currentConf> minConf
          if (currentConf >= minConf) {
            AssociationRule rule =
                new AssociationRule(A, B, currentConf, roh, this.getSubjectlist(A, B), countA
                    - isSupp, itemSet.length);
            this.negativeRuleMap.add(rule);
          }
          // determine the confidence currentConf of the rule B->A
          currentConf = (double) (countB - isSupp) / countB;

          if (currentConf >= minConf) {
            AssociationRule rule =
                new AssociationRule(B, A, currentConf, roh, this.getSubjectlist(B, A), countB
                    - isSupp, itemSet.length);
            this.negativeRuleMap.add(rule);
          }
        }
      }
    }
    return this.negativeRuleMap;
  }

  /**
   * This Method finds all Subjects related to a rule a-> not b. Therefore it finds all baskets
   * containing the ID a and not b. The Result is a Vector of IDs with maximum Size of 50 IDs.
   * 
   * @param a
   * @param b
   * @return
   */
  private TIntList getSubjectlist(int a, int b) {
    TIntList subjects = new TIntArrayList();
    // iterate through all baskets for such that contain a and b
    for (int i = 0; i < this.baskets.getBasketCount(); i++) {
      if (this.baskets.containsAll(i, new int[] {a}) && !this.baskets.containsAll(i, new int[] {b})) {
        subjects.add(this.baskets.getBasketID(i));
      }
      if (subjects.size() > 50) {
        break;
      }
    }

    return subjects;
  }

  /**
   * This method finds all Subjects related to a Set of propertys (itemSet). Therefore it finds all
   * baskets containing the itemSet. The Result is a Vector of IDs with maximum Size of 50 IDs.
   * 
   * @param subjects
   * @param itemSet
   * @return
   */
  public TIntList getTIDlist(int[] itemSet) {
    return getTIDlist(itemSet, 50);
  }

  /**
   * This method finds all Subjects related to a Set of propertys (itemSet). Therefore it finds all
   * baskets containing the itemSet. The Result is a Vector of IDs with maximum Size of sampleSize
   * IDs.
   * 
   * @param subjects
   * @param sampleSize
   * 
   * @return
   */
  public TIntList getTIDlist(int[] itemSet, int sampleSize) {
    TIntList subjects = new TIntArrayList();
    // iterate through all baskets for such that contain itemSet
    for (int b = 0; b < this.baskets.getBasketCount(); b++) {
      if (this.baskets.containsAll(b, itemSet)) {
        subjects.add(Integer.valueOf(this.baskets.getBasketID(b)));
      }
      if (subjects.size() > sampleSize) {
        break;
      }
    }
    return subjects;
  }

  /**
   * This method generates Associationrules applying a given minimum Confidence minConf and minimum
   * Correaltion Coefficient roh. Therefore all large Itemsets from the field largeitemSets. So this
   * method should only be called after calling findLargeItemSets.
   * 
   * @param minConf
   * @param minRoh
   * @return
   */
  public ArrayList<AssociationRule> generateAssociationRules(double minConf, double minRoh) {
    double roh = 0;
    // iterating through ItemSetLists of different size
    for (IItemSetList itemSetList : this.largeItemSets) {
      for (int i = 0; i < itemSetList.getItemSetCount(); i++) {
        int isSupp = itemSetList.getFrequency(i);
        int[] itemSet = itemSetList.getItemSet(i);
        // If itemSet has two elements special Support retrieval methods
        // are executed
        if (itemSet.length == 2) {
          // split itemSet in its two parts for its size is 2
          int A = itemSet[0];
          int B = itemSet[1];
          int countA = -1;
          int countB = -1;

          // determine the support of A and B
          for (int j = 0; j < this.largeOneItemSets.getItemSetCount(); j++) {

            if (this.largeOneItemSets.getItemSet(j)[0] == A) {
              countA = this.largeOneItemSets.getFrequency(j);
            } else if (this.largeOneItemSets.getItemSet(j)[0] == B) {
              countB = this.largeOneItemSets.getFrequency(j);
            }
            if (countB > -1 && countA > -1) {
              break;
            }
          }

          // compute the correlation Coefficient of A and B
          roh = this.computeRoh(countA, countB, isSupp);

          // check wether the correlation coefficient holds minRoh
          if (roh > minRoh) {
            // compute confidence currentConf of the rule A -> B
            double currentConf = (double) isSupp / countA;
            // if A->B holds minConf store the rule A->B
            if (currentConf >= minConf) {
              AssociationRule rule =
                  new AssociationRule(A, B, currentConf, roh, this.getTIDlist(itemSet), isSupp,
                      itemSet.length);
              this.ruleMap.add(rule);
            }
            // compute confidence currentConf of the rule B->A
            currentConf = (double) isSupp / countB;
            // if B->A holds minConf store the rule B->A
            if (currentConf >= minConf) {
              AssociationRule rule =
                  new AssociationRule(B, A, currentConf, roh, this.getTIDlist(itemSet), isSupp,
                      itemSet.length);
              this.ruleMap.add(rule);
            }

          }

        } else {
          // if itemset.length > 2 powerset of the given itemset is
          // generated and any subset of item set is checked for
          // association rules
          PowerSetUtility su = new PowerSetUtility(itemSet);
          HashSet<TIntSet> powerSet = su.getPowerSet();
          // iterate through all subsets of itemSet
          for (TIntSet subSet : powerSet) {
            // ignore the itemSet itsself as a subset
            if (this.superSet(subSet.toArray(new int[subSet.size()]), itemSet)) {
              continue;
            }
            TIntList setA = new TIntArrayList();
            TIntList setB = new TIntArrayList();
            // for any subSet setA find its complement setB
            for (int itemValue : itemSet) {
              Integer item = Integer.valueOf(itemValue);
              if (subSet.contains(item)) {
                setA.add(item);
              } else {
                setB.add(item);
              }
            }
            setA.sort();
            setB.sort();
            int countA = -1;
            int countB = -1;
            // Finding Count of the Subsets
            if (setA.size() > 1) {
              for (int j = 0; j < itemSetList.getItemSetCount(); j++) {
                // the actual large itemSet setA is determined
                // if it is superset of an itemSet within the
                // large itemsets having size of setA
                if (this.superSet(setA.toArray(new int[setA.size()]),
                    this.largeItemSets.get(setA.size() - 2).getItemSet(j))) {
                  countA = this.largeItemSets.get(setA.size() - 2).getFrequency(j);
                }
                if (countA > -1) {
                  break;
                }
              }

            } else {
              for (int j = 0; j < this.largeOneItemSets.getItemSetCount(); j++) {
                // look for the minFrequency of the single
                // element in setA
                if (this.largeOneItemSets.getItemSet(j)[0] == setA.get(0)) {
                  countA = this.largeOneItemSets.getFrequency(j);
                  if (countA > -1) {
                    break;
                  }
                }
              }

              if (setB.size() > 1) {
                for (int j = 0; j < itemSetList.getItemSetCount(); j++) {
                  // the actual large itemSet setB is
                  // determined if it is superset of an
                  // itemSet within the large itemsets having
                  // size of setB
                  if (this.superSet(setB.toArray(new int[setB.size()]),
                      this.largeItemSets.get(setB.size() - 2).getItemSet(j))) {
                    countB = this.largeItemSets.get(setB.size() - 2).getFrequency(j);
                  }
                  if (countB > -1) {
                    break;
                  }
                }

              } else {
                for (int j = 0; j < this.largeOneItemSets.getItemSetCount(); j++) {
                  // look for the minFrequency of the single
                  // element in setA
                  if (this.largeOneItemSets.getItemSet(j)[0] == setB.get(0)) {
                    countB = this.largeOneItemSets.getFrequency(j);
                    if (countB > -1) {
                      break;
                    }
                  }
                }
              }
              // check wether the correlation coefficient of setA
              // and setB holds minRoh
              roh = this.computeRoh(countA, countB, isSupp);
              if (roh > minRoh) {
                // compute confidence currentConf of the rule
                // setA -> setB
                double currentConf = (double) isSupp / countA;
                // if the confidence is higher than minConf
                // create rule setB->setA
                if (currentConf >= minConf) {
                  this.ruleMap.add(new AssociationRule(setA, setB, currentConf, roh, this
                      .getTIDlist(itemSet), isSupp, itemSet.length));
                }
                // compute confidence currentConf of the rule
                // setB -> setA
                currentConf = (double) isSupp / countB;
                // if the confidence is higher than minConf
                // create rule setB->setA
                if (currentConf >= minConf) {
                  AssociationRule rule =
                      new AssociationRule(setB, setA, currentConf, roh, this.getTIDlist(itemSet),
                          isSupp, itemSet.length);
                  this.ruleMap.add(rule);
                }
              }

            }
          }
        }
      }
    }
    return this.ruleMap;
  }

  /**
   * This method computes the Correlation Coefficient roh depending on the count values of two
   * objects and the count value of both.
   * 
   * @param countA
   * @param countB
   * @param countBoth
   * @return
   */
  private double computeRoh(double countA, double countB, double countBoth) {
    double roh = 0;
    double n = this.baskets.getBasketCount();
    roh =
        (n * countBoth - countA * countB)
            / Math.sqrt(countA * (n - countA) * countB * (n - countB));
    return roh;
  }

  /**
   * This method checks whether a given superSet contains all Elements of the given subSet.
   * 
   * @param superSet
   * @param subSet
   * @return
   */
  private boolean superSet(int[] superSet, int[] subSet) {
    boolean contained = false;
    // check wether every item of subSet is also in superSet. if any is not
    // return false
    for (int item : subSet) {
      for (int element : superSet) {
        if (item == element) {
          contained = true;
        }
      }
      if (!contained) {
        return false;
      }
      contained = false;
    }
    return true;
  }

  /**
   * This method initializes the baskets for the algorithm by a RuleDataLoader, depending on the
   * given cluster id.
   */
  private void setAllBaskets(IProgress progress, String ruleConfiguration) {
    if (this.schemaLoader == null) {
      throw new IllegalStateException("Please inject a IEntitySchemaLoader instance!");
    }

    try {
      IEntitySchemaList entities;
      // FIXME handle this.baskets==null;
      if (this.cluster.getId() < 0) {
        entities =
            schemaLoader.getEntityList(this.cluster.getDataSource(), ruleConfiguration, progress);
      } else {
        entities = schemaLoader.getEntityList(this.cluster, ruleConfiguration, progress);
      }

      if (entities != null) {
        this.baskets = new BasketList(entities);
      }
    } catch (SQLException e) {
      logger.error("Unable to get basket list from DB!", e);
      this.baskets = null;
    }
  }

  /**
   * This method loads 1-candidates from the Database related to the current cluster.
   * largeOneItemSets is filled initially here.
   * 
   * @param minFrequency
   */
  private void getOneItemSets(final int minFrequency, IProgress progress, String ruleConfiguration) {
    if (this.ruleLoader == null) {
      throw new IllegalStateException("Please inject a IItemSetLoader instance!");
    }
    try {
      this.largeOneItemSets =
          ruleLoader.getOneItemSets(this.cluster, ruleConfiguration, minFrequency, progress);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * This Method gets a mapping from the itemsetSize to the frequency of those within the baskets.
   * This is needed for GUI statistics.
   * 
   * @return
   */
  public HashMap<String, String> getLargeItemSetStats() {
    HashMap<String, String> map = new HashMap<String, String>();
    for (IItemSetList itemSL : this.largeItemSets) {
      map.put(itemSL.getItemSetSize() + "", itemSL.getItemSetCount() + "");
    }
    return map;
  }

  public int getBasketCount() {
    return this.baskets.getBasketCount();
  }

}
