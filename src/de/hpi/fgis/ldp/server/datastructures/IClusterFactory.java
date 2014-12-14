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

package de.hpi.fgis.ldp.server.datastructures;

import java.util.List;

import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * The cluster factory produces a list of EntityCluster objects.
 * 
 * @author daniel.hefenbrock
 * 
 */
public interface IClusterFactory {

  /**
   * Gets list of clusters.
   * 
   * @param progress the progress feedback instance
   * @return the list of clusters
   */
  public List<EntityCluster> getEntityClusters(final IProgress progress);

}
