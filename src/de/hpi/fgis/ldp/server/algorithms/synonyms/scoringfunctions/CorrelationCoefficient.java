package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public class CorrelationCoefficient extends AbstractCoefficientComputer {

  @Override
  public double computeCoefficient(int firstFrequency, int secondFrequency, int unionFrequency,
      int totalCount) {
    double roh = 0;
    roh =
        ((double) totalCount * unionFrequency - (double) firstFrequency * secondFrequency)
            / Math.sqrt((double) firstFrequency * (totalCount - firstFrequency) * secondFrequency
                * (totalCount - secondFrequency));
    return -roh;
  }

  @Override
  public String name() {
    return "Reverted Correlation coefficient";
  }

}
