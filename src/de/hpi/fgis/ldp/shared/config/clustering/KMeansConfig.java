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

/**
 * this class represents a cluster configuration for a
 * {@link de.hpi.fgis.ldp.server.algorithms.clustering.kmeans.KMeans} Cluster Run (with
 * {@link IClusterConfig.KMeansID})
 * 
 * @author toni.gruetze
 * 
 */
public class KMeansConfig implements IClusterConfig {
  private static final long serialVersionUID = 4184852752286096235L;
  private int numOfClusters;

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig#getAlgorithmID()
   */
  @Override
  public Class<?> getAlgorithmID() {
    return IClusterConfig.KMeansID.class;
  }

  /**
   * gets the number of clusters
   * 
   * @return the number of clusters
   */
  public int getNumberOfClusters() {
    return this.numOfClusters;
  }

  /**
   * sets the number of clusters
   * 
   * @param numOfClusters the number of clusters
   */
  public void setNumberOfClusters(int numOfClusters) {
    this.numOfClusters = numOfClusters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KMeansConfig [numberOfClusters=" + this.numOfClusters + "]";
  }
}
