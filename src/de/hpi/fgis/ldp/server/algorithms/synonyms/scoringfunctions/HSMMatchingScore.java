package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public class HSMMatchingScore extends AbstractCoefficientComputer {

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {
    // double maximumTotalFrequency = 50000.0 * (firstFrequency + secondFrequency) / totalCount;
    // if ( unionFrequency > maximumTotalFrequency) {
    // return 0;
    // }
    double corelationScore =
        (double) (firstFrequency - unionFrequency) * (secondFrequency - unionFrequency)
            / (firstFrequency + secondFrequency);
    return corelationScore;
  }

  @Override
  public String name() {
    return "HSM Matching Score";
  }

}
