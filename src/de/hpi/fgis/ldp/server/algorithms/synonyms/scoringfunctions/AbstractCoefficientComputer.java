package de.hpi.fgis.ldp.server.algorithms.synonyms.scoringfunctions;

public abstract class AbstractCoefficientComputer {
  protected int[] pair;

  public abstract double computeCoefficient(int firstFrequency, int secondFrequency,
      int unionFrequency, int totalCount);

  public void setPair(int[] pair) {
    this.pair = pair;
  }

  public abstract String name();

}
