/*-
 * Copyright 2012 by: Hasso Plattner Institute for Software Systems Engineering 
 * Prof.-Dr.-Helmert-Str. 2-3
 * 14482 Potsdam, Germany
 * Potsdam District Court, HRB 12184
 * Authorized Representative Managing Director: Prof. Dr. Christoph Meinel
 * http://www.hpi-web.de/
 * 
 * Information Systems Group, http://www.hpi-web.de/naumann/
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.algorithms.clustering.similarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.server.datastructures.impl.AbstractSchema;

/**
 * This comparator employs the Jaccard-Distance as measure for entity similarity. Moreover, it
 * computes the mean schema of a set of entities (or cluster) by selecting frequently occurring
 * attributes, using a threshold.
 * 
 * @author hefenbrock
 */
public class ThresholdMeans extends JaccardSimilarity {

  @Override
  public ISchema computeMean(Iterable<IEntity> entities) {
    // count occurrences of each attribute
    Map<Integer, Integer> attributeCounts = new HashMap<Integer, Integer>();
    int numEntities = 0;
    for (IEntity entity : entities) {
      numEntities++;
      for (Integer attribute : entity.schema()) {
        if (attributeCounts.containsKey(attribute)) {
          attributeCounts.put(attribute,
              Integer.valueOf(attributeCounts.get(attribute).intValue() + 1));
        } else {
          attributeCounts.put(attribute, Integer.valueOf(1));
        }
      }
    }
    // determine threshold
    int threshold = numEntities / 2;
    // filter attributes
    List<Integer> attributes = new ArrayList<Integer>();
    for (Integer attribute : attributeCounts.keySet()) {
      if (attributeCounts.get(attribute).intValue() >= threshold) {
        attributes.add(attribute);
      }
    }
    // IEntity demands the list to be sorted ascending
    Collections.sort(attributes);
    // convert to primitive array
    final int[] mean = new int[attributes.size()];
    for (int i = 0; i < mean.length; i++) {
      mean[i] = attributes.get(i).intValue();
    }

    return new AbstractSchema() {

      @Override
      public Integer get(int index) {
        return Integer.valueOf(mean[index]);
      }

      @Override
      public int size() {
        return mean.length;
      }

    };
  }

  @Override
  public String toString() {
    return super.toString() + ", mean schema algorithm: Threshold";
  }

}
