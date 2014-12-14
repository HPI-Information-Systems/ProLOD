package de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures;

import gnu.trove.list.TIntList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.AdvancedARF;

public class CorrelatedPair extends FPattern {

  private double cCoefficient;
  private TIntList objects;

  public CorrelatedPair(int[] i, int f, double supp, double ccoefficient) {
    super(i, f, supp);
    setCCoefficient(ccoefficient);
  }

  public void setCCoefficient(double ccoefficient) {
    this.cCoefficient = ccoefficient;

  }

  public double getCCoefficient() {
    return cCoefficient;

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(super.toString());
    sb.append("| score = ");
    sb.append(cCoefficient);
    return sb.toString();
  }

  public void setObjects(AdvancedARF rangeARF) {
    this.objects = rangeARF.getTIDlist(items);

  }

  public TIntList getObjects() {
    return objects;
  }

}
