package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public class MinConfidenceScore extends AbstractCoefficientComputer {

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {
    double leftConfidence = ((double) firstFrequency - unionFrequency) / firstFrequency;
    double rightConfidence = ((double) secondFrequency - unionFrequency) / secondFrequency;
    return Math.min(leftConfidence, rightConfidence);
  }

  @Override
  public String name() {
    return "Minimum Confidence";
  }

}
