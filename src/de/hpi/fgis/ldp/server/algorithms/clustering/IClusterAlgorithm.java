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

package de.hpi.fgis.ldp.server.algorithms.clustering;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.clustering.IClusterConfig;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * this interface represents a clustering algorithm
 * 
 * @author toni.gruetze
 * 
 */
public interface IClusterAlgorithm {
  /**
   * this method clusters a given parent cluster with the specified configuration
   * 
   * @param parent the parent cluster to be subclustered
   * @param clusterConfig the configuration to be used
   * @param progress the progress feedback instance
   * @return the session id of the subcluster session
   */
  public Session cluster(Cluster parent, IClusterConfig clusterConfig, IProgress progress)
      throws Exception;
}
