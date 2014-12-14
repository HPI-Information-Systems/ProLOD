package de.hpi.fgis.ldp.server.algorithms.synonyms;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.CorrelatedPair;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions.AbstractCoefficientComputer;

/**
 * This class computes the correlation coefficient for items of frequent itemsets
 * 
 * @author ziawasch
 * 
 */
public class CorrelationEvaluator {

  public AbstractCoefficientComputer getScoreMetric() {
    return scoreMetric;
  }

  public void setScoreMetric(AbstractCoefficientComputer scoreMetric) {
    this.scoreMetric = scoreMetric;
  }

  // private TObjectIntHashMap<int[]> pairs = new TObjectIntHashMap<int[]>();
  private final TObjectIntHashMap<Entry<Integer, Integer>> pairs =
      new TObjectIntHashMap<Entry<Integer, Integer>>();
  private final TIntIntHashMap items = new TIntIntHashMap();
  private double minScore;
  private final int numBaskets;
  private AbstractCoefficientComputer scoreMetric;

  public CorrelationEvaluator(IItemSetList patterns, double minCoefficient, int baskets,
      AbstractCoefficientComputer scoreM) {
    for (int i = 0; i < patterns.getItemSetCount(); i++) {
      int[] newPair = patterns.getItemSet(i);
      pairs.put(new SimpleEntry<Integer, Integer>(newPair[0], newPair[1]), 0);
      items.put(newPair[0], 0);
      items.put(newPair[1], 0);
    }
    this.setMinConfidence(minCoefficient);
    this.numBaskets = baskets;
    this.scoreMetric = scoreM;
  }

  private void setMinConfidence(double minCoefficient) {
    this.minScore = minCoefficient;

  }

  public double getMinCoefficient() {
    return minScore;
  }

  public void setMinCoefficient(double minCoefficient) {
    this.minScore = minCoefficient;
  }

  public ArrayList<CorrelatedPair> getMatchingValues(IItemSetList patterns,
      IItemSetList singlePatterns) {
    ArrayList<CorrelatedPair> currentCPatterns = new ArrayList<CorrelatedPair>();
    // retrieve frequencies for pairs
    for (int i = 0, j = 0; i < patterns.getItemSetCount() && j < pairs.size(); i++) {
      Entry<Integer, Integer> currentPair =
          new SimpleEntry<Integer, Integer>(patterns.getItemSet(i)[0], patterns.getItemSet(i)[1]);
      if (pairs.contains(currentPair)) {
        ++j;
        pairs.put(currentPair, patterns.getFrequency(i));
      }
    }

    for (int i = 0, j = 0; i < singlePatterns.getItemSetCount() && j < items.size(); i++) {
      int[] currentPair = singlePatterns.getItemSet(i);
      if (items.contains(currentPair[0])) {
        ++j;
        items.put(currentPair[0], singlePatterns.getFrequency(i));
      }
    }

    for (Entry<Integer, Integer> pair : pairs.keySet()) {
      int first = pair.getKey();
      int second = pair.getValue();
      int[] combination = new int[] {first, second};
      int totalFrequency = pairs.get(pair);
      int firstFrequency = items.get(first);
      int secondFrequency = items.get(second);
      scoreMetric.setPair(combination);
      double coefficient =
          scoreMetric.computeCoefficient(firstFrequency, secondFrequency, totalFrequency,
              numBaskets);
      if (coefficient >= minScore) {
        currentCPatterns.add(new CorrelatedPair(combination, totalFrequency,
            (double) totalFrequency / numBaskets, coefficient));
      }
    }

    return currentCPatterns;
  }

  public int[] getItems() {
    return items.keys();
  }

}
