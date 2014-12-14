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
import java.util.List;
import java.util.Set;

import de.hpi.fgis.ldp.server.datastructures.IEntity;
import de.hpi.fgis.ldp.server.datastructures.ISchema;

/**
 * Self-explanatory (for now).
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 * 
 */
public interface ISchemaComparator {

  public double computeSim(ISchema a, ISchema b);

  /**
   * Compute a mean for the given entities. The mean can then be compared with other entities.
   */
  public ISchema computeMean(Iterable<IEntity> entities);

  public double computeAvgError(final List<Set<IEntity>> clusters);

  public double computeAvgError(final Collection<IEntity> clusterEntities, final ISchema mean);

}
