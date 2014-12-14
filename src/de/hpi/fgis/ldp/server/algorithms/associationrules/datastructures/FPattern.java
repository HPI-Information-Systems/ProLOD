package de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures;

import java.util.Arrays;

public class FPattern {
  protected int frequency;
  protected double support;
  protected int[] items;

  public FPattern(int[] i, int f, double supp) {
    items = new int[i.length];
    System.arraycopy(i, 0, items, 0, i.length);
    frequency = f;
    support = supp;
    // Arrays.sort(items);
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setSupport(double support) {

    this.support = support;
  }

  public double getSupport() {
    return support;
  }

  public void setItems(int[] items) {
    Arrays.sort(items);
    this.items = items;
  }

  public int[] getItems() {
    return items;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" | ");
    for (int item : items) {
      sb.append(item).append(" ");
    }
    sb.append("| frequency = ");
    sb.append(frequency);
    return sb.toString();

  }

  public int size() {

    return items.length;
  }

  public boolean containsThePattern(int[] items) {

    if (size() != items.length) {
      return false;
    }
    for (int i = 0; i < items.length; i++) {
      if (getItems()[i] != items[i]) {
        return false;
      }
    }
    return true;

  }

  public boolean containsThePattern(int predicate) {
    if (size() != items.length) {
      return false;
    }
    if (items[0] == predicate) {
      return true;
    }
    return false;
  }

}
