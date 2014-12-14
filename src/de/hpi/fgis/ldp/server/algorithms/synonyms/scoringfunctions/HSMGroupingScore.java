package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public class HSMGroupingScore extends AbstractCoefficientComputer {

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {
    double score = (double) unionFrequency / (double) Math.min(firstFrequency, secondFrequency);
    return score;
  }

  @Override
  public String name() {
    return "HSM Grouping Score";
  }

}
