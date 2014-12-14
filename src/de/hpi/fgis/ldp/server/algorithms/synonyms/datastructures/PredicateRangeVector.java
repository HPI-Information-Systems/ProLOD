package de.hpi.fgis.ldp.server.algorithms.synonyms.datastructures;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * This class manages the type vectors using a hashmap that maps types to their frequencies in the
 * range of the predicate
 * 
 * @author ziawasch
 *
 */
public class PredicateRangeVector {

  private String predicate;
  private Object2IntOpenHashMap<String> type2CountMap = new Object2IntOpenHashMap<String>();

  public PredicateRangeVector(String value) {
    setPredicate(value);
  }

  public void setType2CountMap(Object2IntOpenHashMap<String> type2CountMap) {
    this.type2CountMap = type2CountMap;
  }

  public Object2IntOpenHashMap<String> getType2CountMap() {
    return type2CountMap;
  }

  public void putNewMapping(String type, int count) {
    type2CountMap.put(type, count);
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getPredicate() {
    return predicate;
  }

}
