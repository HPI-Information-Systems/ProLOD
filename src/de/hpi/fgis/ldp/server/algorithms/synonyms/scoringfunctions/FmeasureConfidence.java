package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public class FmeasureConfidence extends AbstractCoefficientComputer {

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {
    double leftConfidence = ((double) firstFrequency - unionFrequency) / firstFrequency;
    double rightConfidence = ((double) secondFrequency - unionFrequency) / secondFrequency;
    if (leftConfidence == 0.0 && rightConfidence == 0.0) {
      return 0.0;
    }

    return 2 * leftConfidence * rightConfidence / (leftConfidence + rightConfidence);
  }

  @Override
  public String name() {
    return "F-Measure of Confidences";
  }

}
