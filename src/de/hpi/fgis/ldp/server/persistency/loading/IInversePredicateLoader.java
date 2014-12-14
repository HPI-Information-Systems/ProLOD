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

package de.hpi.fgis.ldp.server.persistency.loading;

import java.sql.SQLException;
import java.util.SortedSet;

import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.InversePredicateModel;

/**
 * Loads basic information to enable inverse predicate detection
 * 
 * @author toni.gruetze
 * 
 */
public interface IInversePredicateLoader {

  /**
   * Get the inverse predicates of the specified cluster
   * 
   * @param clusterID the id of the cluster to get inverse predicates for
   * @param minSupport the minimal support of the inverse predicate pair
   * @param progress the progress feedback instance
   * @return the inverse predicates in the specified cluster
   */
  // FIXME move to model
  public abstract SortedSet<InversePredicateModel> getInversePredicates(final Cluster cluster,
      final double minSupport, final IProgress progress) throws SQLException;
}
