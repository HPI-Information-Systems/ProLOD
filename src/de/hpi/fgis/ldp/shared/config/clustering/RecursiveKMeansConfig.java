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

package de.hpi.fgis.ldp.shared.config.clustering;

import java.util.Arrays;

/**
 * this class represents a cluster configuration for a
 * {@link de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.KMeans} Cluster Run (with
 * {@link IClusterConfig.KMeansID})
 * 
 * @author toni.gruetze
 * 
 */
public class RecursiveKMeansConfig implements IClusterConfig {
  private static final long serialVersionUID = 7906252576971385843L;
  private int[] numOfClusters;
  private double abortOnError = 0.35;
  private int abortOnSize = 100;

  /**
   * creates a new instance of the default clustering config
   * 
   * @return the default config
   */
  public static RecursiveKMeansConfig getDefaultConfig() {
    RecursiveKMeansConfig clusterConfig = new RecursiveKMeansConfig();

    clusterConfig.setNumberOfClusters(10, 5, 5, 5, 5);
    clusterConfig.setAbortOnError(0.35);
    clusterConfig.setAbortOnSize(100);

    return clusterConfig;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig#getAlgorithmID()
   */
  @Override
  public Class<?> getAlgorithmID() {
    return IClusterConfig.RecursiveKMeansID.class;
  }

  /**
   * gets the number of clusters
   * 
   * @return the number of clusters
   */
  public int[] getNumberOfClusters() {
    return this.numOfClusters;
  }

  /**
   * sets the number of clusters
   * 
   * @param numOfClusters the number of clusters
   */
  public void setNumberOfClusters(int... numOfClusters) {
    this.numOfClusters = numOfClusters;
  }

  /**
   * Divide clusters into subclusters if error is >= abortOnError
   */
  public double getAbortOnError() {
    return abortOnError;
  }

  /**
   * Divide clusters into subclusters if error is >= abortOnError
   */
  public void setAbortOnError(double abortOnError) {
    this.abortOnError = abortOnError;
  }

  /**
   * Divide clusters into subclusters if size is >= abortOnSize
   */
  public int getAbortOnSize() {
    return abortOnSize;
  }

  /**
   * Divide clusters into subclusters if size is >= abortOnSize
   */
  public void setAbortOnSize(int abortOnSize) {
    this.abortOnSize = abortOnSize;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KMeansConfig [numberOfClusters=" + Arrays.toString(this.numOfClusters) + "]";
  }
}
