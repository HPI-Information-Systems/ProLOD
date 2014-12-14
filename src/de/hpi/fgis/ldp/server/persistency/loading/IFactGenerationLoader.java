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
 * * Ziawasch Abedjan
 *  * Christoph Böhm <christoph.boehm@hpi.uni-potsdam.de>, or
 * 
 * 
 * Licence: http://creativecommons.org/licenses/by-sa/3.0/
 * 
 */

package de.hpi.fgis.ldp.server.persistency.loading;

import java.sql.SQLException;

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
public interface IFactGenerationLoader {

  /**
   * Get the entities within a cluster.
   * 
   * @param clusterID the cluster id to get entities for
   * @param progress the progress feedback instance
   * @return the entities of the cluster or <code>null</code> if no matching entities were found
   */
  public abstract IEntitySchemaList getEntityList(final Cluster cluster, int existingObject,
      int missingObject, final IProgress progress) throws SQLException;

  public abstract IEntitySchemaList getEntityList(final DataSource source, int existingObject,
      int missingObject, final IProgress progress) throws SQLException;

  public abstract int[] getDesignators(final Cluster cluster, int object, final IProgress progress)
      throws SQLException;

  public abstract int[] getDesignators(final DataSource source, int object, final IProgress progress)
      throws SQLException;

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
