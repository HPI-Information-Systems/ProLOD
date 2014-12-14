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

import gnu.trove.map.hash.TIntObjectHashMap;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * This interface provides meta information to database structures (e.g. names of subject- or
 * predicate-id's)
 * 
 * @author toni.gruetze
 */
public interface IMetaLoader {
  /**
   * fills the names of a map with TID's (transaction id's)
   * 
   * @param allTIDs a map with all TIDs to get names for
   * @param source the source to load data from
   */
  public abstract void fillTIDNameMap(TIntObjectHashMap<String> allTIDs, String ruleConfiguration,
      DataSource source);

  /**
   * retrieves the subject pairs related to an object and two synonym predicates
   * 
   * @param allTIDs a map with all TIDs to get names for
   * @param source the source to load data from
   */
  public abstract Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>> getSynonymDetails(
      int[] predicatePair, int objectID, DataSource source);

  /**
   * fills the names of a map with predicate id's
   * 
   * @param predicates a map with the the predicate id's to get names for
   * @param source the source to load data from
   */
  public abstract void fillTransactionNameMap(TIntObjectHashMap<String> predicates,
      String ruleConfiguration, DataSource source);
}
