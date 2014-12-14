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

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * Loads basic schema information for entities in the DB
 * 
 * @author toni.gruetze
 * 
 */
public interface IEntitySchemaLoader {
  /**
   * Get all entities of the database.<br>
   * A entity is defined as a sorted (ascending) set of predicate id's of a subject id / entity.<br>
   * A entity list is a sorted (ascending in subject id's) set of entities
   * 
   * @param source the data source to get entities from
   * @param progress the progress feedback instance
   * @return all entities of the database or <code>null</code> if no matching entities were found
   */
  public abstract IEntitySchemaList getEntityList(DataSource source, String ruleConfiguration,
      final IProgress progress) throws SQLException;

  /**
   * Get all entities of the database.<br>
   * A entity is defined as a sorted (ascending) set of predicate id's of a subject id / entity.<br>
   * A entity list is a sorted (ascending in subject id's) set of entities
   * 
   * @param the data source to get entities from
   * @param minPropOccurance the minimal amount of occurrence of the properties for each entity to
   *        be loaded
   * @param maxPropFrequency the maximal frequency of the properties of each entity to be loaded
   * @param progress the progress feedback instance
   * @return all entities of the database or <code>null</code> if no matching entities were found
   */
  public abstract IEntitySchemaList getEntityList(final DataSource source,
      String ruleConfiguration, final int minPropOccurance, final double maxPropFrequency,
      final IProgress progress) throws SQLException;

  /**
   * Get the entities within a cluster.
   * 
   * @param clusterID the cluster id to get entities for
   * @param progress the progress feedback instance
   * @return the entities of the cluster or <code>null</code> if no matching entities were found
   */
  public abstract IEntitySchemaList getEntityList(final Cluster cluster, String ruleConfiguration,
      final IProgress progress) throws SQLException;

  /**
   * @TODO
   * 
   * @param source
   * @param progress
   * @return
   * @throws SQLException
   */
  public abstract Map<Integer, String> getClasses(final DataSource source, final IProgress progress)
      throws SQLException;

  /**
   * Get ontology classes and labels.
   * 
   * @param clusterID the cluster id to get entities for
   * @param progress the progress feedback instance
   * @return the entities of the cluster or <code>null</code> if no matching entities were found
   */
  public abstract Map<Integer, String> getClasses(final Cluster cluster, final IProgress progress)
      throws SQLException;

  /**
   * @TODO
   * 
   * @param source
   * @param progress
   * @return
   * @throws SQLException
   */
  public abstract Map<Integer, Integer> getClassHierarchy(final DataSource source,
      final IProgress progress) throws SQLException;

  /**
   * @TODO
   * 
   * @param source
   * @param progress
   * @return
   * @throws SQLException
   */
  public abstract TIntIntHashMap getPredicateDomain(final DataSource source,
      final IProgress progress) throws SQLException;

  /**
   * @TODO
   * 
   * @param source
   * @param progress
   * @return
   * @throws SQLException
   */
  public abstract Integer getRootClass(final DataSource source, final IProgress progress)
      throws SQLException;

  /**
   * @TODO
   * 
   * @param source
   * @param progress
   * @return
   * @throws SQLException
   */
  public abstract Map<Integer, ArrayList<Integer>> getEntityClassList(final DataSource source,
      final IProgress progress, Map<Integer, String> classes) throws SQLException;

  public abstract TIntIntHashMap getInstanceCounts(final DataSource source, final IProgress progress)
      throws SQLException;

  /**
   * Returns a mapping between the ids in the maintable and the ids in the cluster table
   * 
   * @param source
   * @param progress
   * @param string
   * @return
   * @throws SQLException
   */
  public abstract TIntIntHashMap getClusterIdMappings(final DataSource source,
      final IProgress progress, String string) throws SQLException;

  public abstract TIntIntHashMap getPredicateIDMappings(final DataSource source,
      final IProgress progress, String string) throws SQLException;

  public abstract TIntIntHashMap getPredicateCounts(final DataSource source,
      final IProgress progress, TIntIntHashMap predicateToSourceClass) throws SQLException;

  public abstract TIntObjectHashMap<String> getRemovedProperties(DataSource dataSource,
      IProgress progress, int s_id);

  public abstract Map<String, String> getAddedProperties(DataSource dataSource, IProgress progress,
      int s_id);

  // /**
  // * Get the entities within a cluster session.
  // *
  // * @param sessionID
  // * the cluster session id to get entities for
  // * @param progress
  // * the progress feedback instance
  // * @return the entities of the cluster session
  // */
  // public abstract IEntitySchemaList getEntityListFromSession(final int
  // sessionID, final IProgress progress) throws SQLException;
}
