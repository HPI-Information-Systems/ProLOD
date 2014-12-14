package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/**
 * Formula from Webtables paper
 * 
 * @author ziawasch
 *
 */
public class SameContextScore extends AbstractCoefficientComputer {

  private final Object2ObjectOpenHashMap<String, Object2DoubleOpenHashMap<String>> ruleMatrix;
  private final MinConfidenceScore minConfidenceScore = new MinConfidenceScore();

  public SameContextScore(Object2ObjectOpenHashMap<String, Object2DoubleOpenHashMap<String>> rules) {
    super();
    ruleMatrix = rules;
  }

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {

    // double negativeRelationScore =
    // minConfidenceScore.computeCoefficient(firstFrequency,
    // secondFrequency, unionFrequency, totalCount);
    if (unionFrequency > 10) {
      return 0.0;
    }
    double nominator = (double) firstFrequency * secondFrequency;
    double denominator = 0.000001;
    Object2DoubleOpenHashMap<String> firstRules = ruleMatrix.get(pair[0]);
    Object2DoubleOpenHashMap<String> secondRules = ruleMatrix.get(pair[1]);
    if (firstRules == null || secondRules == null) {
      return 0;
    }
    ObjectOpenHashSet<String> allConditions = new ObjectOpenHashSet<String>();
    allConditions.addAll(firstRules.keySet());
    allConditions.addAll(secondRules.keySet());
    for (String condition : allConditions) {
      double firstZVal = firstRules.getDouble(condition);
      double secondZVal = secondRules.getDouble(condition);
      denominator = denominator + (firstZVal - secondZVal) * (firstZVal - secondZVal);
    }

    return nominator / denominator;
  }

  @Override
  public String name() {

    return "Same Context Score";
  }

}
