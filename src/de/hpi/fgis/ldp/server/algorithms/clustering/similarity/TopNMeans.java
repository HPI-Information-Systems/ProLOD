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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.server.datastructures.impl.AbstractSchema;

/**
 * The TopNMeans comparator employs the Jaccard-Distance as entity similarity measure. It computes a
 * mean schema of a cluster having the following properties: - The size N of the mean schema is the
 * average size of all schemas in the given cluster. - The attributes of the mean schema are the top
 * N attributes from the given cluster.
 * 
 * We expect this comparator to yield better means than the simpler ThresholdMeans comparator.
 * 
 * @author hefenbrock
 */
public class TopNMeans extends JaccardSimilarity {

  @Override
  public ISchema computeMean(Iterable<IEntity> entities) {
    // count occurrences of each attribute
    // and compute average schema size
    Map<Integer, Integer> attributeCounts = new HashMap<Integer, Integer>();
    int numEntities = 0;
    int numAttributes = 0;
    for (IEntity entity : entities) {
      numEntities++;
      for (Integer attribute : entity.schema()) {
        numAttributes++;
        if (attributeCounts.containsKey(attribute)) {
          attributeCounts.put(attribute,
              Integer.valueOf(attributeCounts.get(attribute).intValue() + 1));
        } else {
          attributeCounts.put(attribute, Integer.valueOf(1));
        }
      }
    }
    numAttributes = 0 == numEntities ? 0 : numAttributes / numEntities;
    // find top N attributes (with N = avg. schema size)
    int[] topCounts = new int[numAttributes];
    final int[] topAttributes = new int[numAttributes];
    for (Integer attribute : attributeCounts.keySet()) {
      int count = attributeCounts.get(attribute).intValue();
      int i = topCounts.length - 2;
      while (i >= 0 && topCounts[i] < count) {
        topCounts[i + 1] = topCounts[i];
        topAttributes[i + 1] = topAttributes[i];
        i--;
      }
      if (!(i + 1 >= 0 && topCounts[i + 1] < count)) {
        continue;
      }
      topCounts[i + 1] = count;
      topAttributes[i + 1] = attribute.intValue();
    }
    // IEntity demands the list to be sorted ascending
    Arrays.sort(topAttributes);

    return new AbstractSchema() {

      @Override
      public Integer get(int index) {
        return Integer.valueOf(topAttributes[index]);
      }

      @Override
      public int size() {
        return topAttributes.length;
      }

    };
  }

  @Override
  public String toString() {
    return super.toString() + ", mean schema algorithm: TopN";
  }
}
