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

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * Loads initial item set information to enable association rule detection
 * 
 * @author toni.gruetze
 * 
 */
public interface IItemSetLoader {

  /**
   * get the itemsets in the database with the length "1"
   * 
   * @param cluster the data source
   * @param minCount the minimal count of occurrence
   * @param progress the progress feedback instance
   * @return the itemsets in the database
   */
  public abstract IItemSetList getOneItemSets(final Cluster cluster, String ruleConfiguration,
      final int minCount, final IProgress progress) throws SQLException;

}
