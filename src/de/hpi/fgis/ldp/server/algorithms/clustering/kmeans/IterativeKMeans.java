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

package de.hpi.fgis.ldp.server.algorithms.clustering.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import de.hpi.fgis.ldp.server.algorithms.clustering.EntityClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * This is an extension to the standard K-Means implementation which iteratively increments K, the
 * number of clusters, from initially 2 to a specified maximum. Alternatively, it aborts once the
 * maximum cluster error of all clusters drops below a specified threshold.
 * 
 * @author hefenbrock
 * 
 */
public class IterativeKMeans extends KMeans {
  private double abortOnMaxError = 0;
  private AbortReason abort;

  public IterativeKMeans(DataSource source) {
    super(source);
  }

  // there are two abort reasons:
  // 1. max K is reached
  // 2. the maximum error of all clusters is less
  // than the specified threshold
  public enum AbortReason {
    maxK, maxError
  }

  @Override
  public EntityClusterFactory cluster() {
    // init 2 clusters
    this.initClusters(2);
    this.computeMeans();

    int k = 2;

    while (true) {

      this.logger.log(Level.CONFIG, "  K = " + k);

      // what follows is standard K-Means
      // TODO refactor.

      int iterations = 1;

      double procReassigned = 0;
      do {
        int reassigned = this.reassign();
        this.computeMeans();

        double oldProcReassigned = procReassigned;
        procReassigned = Math.round(100 * (reassigned * 100.0) / this.entities.size()) / 100.0;

        if (procReassigned == oldProcReassigned) {
          this.logger.log(Level.CONFIG, "    Ping-pong condition detected - aborting.");
          break;
        }

        this.logger.log(Level.CONFIG, "    Iteration " + iterations++ + " (" + procReassigned
            + "% reassigned (" + reassigned + "))");

      } while (KMeans.MIN_REASSIGNED < procReassigned && iterations <= KMeans.MAX_ITERATIONS);

      this.logger.log(Level.CONFIG, "  Clustering finished.");

      // check if the maximum number of clusters is reached
      if (k >= this.numClusters) {
        this.abort = AbortReason.maxK;
        break;
      }

      // find a cluster to split
      Iterator<Integer> clusters = this.factory.clusterIterator();
      double maxError = 0;
      double maxWError = 0;
      int splitClusterIndex = 0;
      while (clusters.hasNext()) {
        int clusterIndex = clusters.next().intValue();
        double error =
            this.comparator.computeAvgError(this.factory.getClusters().get(clusterIndex),
                this.means.get(clusterIndex));

        this.logger.log(Level.CONFIG,
            "    cluster " + clusterIndex + " (" + this.factory.size(clusterIndex) + ", error="
                + (Math.round(error * 100.0)) / 100.0 + ")");

        // punish small clusters
        double werror = error * Math.log(1 + this.factory.size(clusterIndex));
        if (werror >= maxWError) {
          maxWError = werror;
          splitClusterIndex = clusterIndex;
        }
        if (error > maxError) {
          maxError = error;
        }
      }

      if (maxError <= this.abortOnMaxError) {
        this.abort = AbortReason.maxError;
        break;
      }

      // split it
      this.logger.log(Level.CONFIG, "  Splitting cluster " + splitClusterIndex + " ("
          + this.factory.size(splitClusterIndex) + ")");
      this.split(splitClusterIndex);

      k++;
    }

    return this.factory;
  }

  private void split(int clusterIndex) {

    // get error distribution for the cluster
    long[] errDist =
        this.factory.computeErrorDistribution(clusterIndex, this.means.get(clusterIndex),
            this.comparator);

    // find a peak in the distribution after applying weights
    long bestError = 0;
    long bestWeight = 0;
    for (int err = 0; err < errDist.length; err++) {
      // apply weighting to the distribution: TODO: apply smoothing?
      // w = e * n^2 with d = error in [0; 100] and n = count
      long weightedError = err * errDist[err] * errDist[err];

      if (weightedError >= bestWeight) {
        bestWeight = weightedError;
        bestError = err;
      }
    }
    this.logger.log(Level.CONFIG, "  Selected error is: " + bestError);

    // find entities providing the calculated error
    Iterable<IEntity> entities = this.factory.entities(clusterIndex);
    List<IEntity> candidates = new ArrayList<IEntity>();
    for (IEntity entity : entities) {
      double error =
          1.0 - this.comparator.computeSim(entity.schema(), this.means.get(clusterIndex));
      int derror = (int) (error * 100);
      if (derror == bestError) {
        candidates.add(entity);
      }
    }

    // for now: select a random candidate.
    IEntity newSeed = candidates.get(this.rand.nextInt(candidates.size()));

    this.factory.removeEntity(clusterIndex, newSeed);
    this.factory.createNewCluster(newSeed);
    this.means.add(newSeed.schema());
  }

  public void setAbortOnMaxError(double error) {
    this.abortOnMaxError = error;
  }

  public AbortReason getAbortReason() {
    return this.abort;
  }

}
