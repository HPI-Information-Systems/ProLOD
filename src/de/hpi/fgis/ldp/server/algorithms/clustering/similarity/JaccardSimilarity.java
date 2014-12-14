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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;

/**
 * This abstract class can be used as a base class for comparators using the schema-based
 * Jaccard-Distance as measure for the similarity of two entities represented by their schema.
 * 
 * @author hefenbrock
 */
public abstract class JaccardSimilarity implements ISchemaComparator {

  @Override
  public double computeSim(ISchema a, ISchema b) {
    Iterator<Integer> ita = a.iterator();
    Iterator<Integer> itb = b.iterator();

    int all = 0;
    int common = 0;

    if (!ita.hasNext() && !itb.hasNext()) {
      return 1;
    }
    if (!ita.hasNext() || !itb.hasNext()) {
      return 0;
    }

    int ida = ita.next().intValue();
    int idb = itb.next().intValue();

    while (true) {
      if (ida == idb) {
        all++;
        common++;
        if (!ita.hasNext() || !itb.hasNext()) {
          break;
        }
        ida = ita.next().intValue();
        idb = itb.next().intValue();
      } else if (ida > idb) {
        all++;
        if (itb.hasNext()) {
          idb = itb.next().intValue();
        } else {
          all++;
          break;
        }
      } else if (ida < idb) {
        all++;
        if (ita.hasNext()) {
          ida = ita.next().intValue();
        } else {
          all++;
          break;
        }
      } else {
        break;
      }
    }

    while (ita.hasNext()) {
      all++;
      ita.next();
    }

    while (itb.hasNext()) {
      all++;
      itb.next();
    }

    return ((double) common) / all;
  }

  @Override
  public abstract ISchema computeMean(Iterable<IEntity> entities);

  @Override
  public String toString() {
    return "schema similarity measure: Jaccard distance";
  }

  @Override
  public double computeAvgError(final List<Set<IEntity>> clusters) {
    double[] errors = new double[clusters.size()];
    for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
      final Set<IEntity> entities = clusters.get(clusterIndex);
      errors[clusterIndex++] = this.computeAvgError(entities, this.computeMean(entities));
    }
    double sumError = 0;
    for (double error : errors) {
      sumError += error;
    }
    return sumError / errors.length;
  }

  @Override
  public double computeAvgError(final Collection<IEntity> clusterEntities, final ISchema mean) {
    double error = 0;
    for (IEntity entity : clusterEntities) {
      error += this.computeSim(entity.schema(), mean);
    }
    return 1.0 - (error / clusterEntities.size());
  }
  /*-
   // TODO remove
   //	public Map<Integer, Double> computeAvgError(ISchemaComparator cmp,
   //			Map<Integer, ISchema> meanSchemas) {
   //		if (null == meanSchemas) {
   //			meanSchemas = createClusterMeans(cmp);
   //		}
   //		Map<Integer, Double> result = new HashMap<Integer, Double>();
   //		Iterator<Integer> clusters = clusterIterator();
   //		while (clusters.hasNext()) {
   //			int clusterIndex = clusters.next();
   //			result.put(clusterIndex, computeAvgError(clusterIndex, 
   //					meanSchemas.get(clusterIndex), cmp));
   //		}
   //		return result;
   //	}
   //	
   //	public double computeAvgError(ISchemaComparator cmp) {
   //		Collection<Double> errors = computeAvgError(cmp, null).values();
   //		double sumError = 0;
   //		for (Double error : errors) {
   //			sumError += error;
   //		}
   //		return sumError/errors.size();
   //	}
   //	
   //	public double computeAvgError(int clusterIndex, ISchema mean, 
   //			ISchemaComparator cmp) {
   //		if (0 == clusters.get(clusterIndex).size()) {
   //			return 1.0;
   //		}
   //		double error = 0;
   //		for (IEntity entity : clusters.get(clusterIndex)) {
   //			error += cmp.computeSim(entity.schema(), mean);
   //		}
   //		return 1.0-(error/(double)clusters.get(clusterIndex).size());
   //	}
   //
   //	private Map<Integer, ISchema> createClusterMeans(ISchemaComparator cmp) {
   //		Map<Integer, ISchema> result = new HashMap<Integer, ISchema>();
   //		int clusterId = 0;
   //		for (Set<IEntity> cluster : clusters) {
   //			result.put(clusterId++, cmp.computeMean(cluster));
   //		}
   //		return result;
   //	}
   */
}
