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
 * For Questions please contact:
 *  * Christoph B??hm <christoph.boehm@hpi.uni-potsdam.de>, or
 *  * Felix Naumann <felix.naumann@hpi.uni-potsdam.de>
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.shared.config.clustering;

/**
 * this class represents a cluster configuration for a
 * {@link de.hpi.fgis.ldp.server.algorithms.clustering.ontology.Ontology} Cluster Run (with
 * {@link IClusterConfig.OntologyID})
 * 
 * @author toni.gruetze
 * 
 */
public class OntologyConfig implements IClusterConfig {
  private static final long serialVersionUID = -2546028220096886078L;
  private int numOfClusters;

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig#getAlgorithmID()
   */
  @Override
  public Class<?> getAlgorithmID() {
    return IClusterConfig.OntologyID.class;
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

  /**
   * creates a new instance of the default clustering config
   * 
   * @return the default config
   */
  public static OntologyConfig getDefaultConfig() {
    OntologyConfig clusterConfig = new OntologyConfig();

    clusterConfig.setNumberOfClusters(10);

    return clusterConfig;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OntologyConfig [numberOfClusters=" + this.numOfClusters + "]";
  }
}
