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

package de.hpi.fgis.ldp.server.persistency.storage;

import java.sql.SQLException;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * enables the creation of data base schemata
 * 
 * @author toni.gruetze
 * 
 */
public interface ISchemaStorage {
  /**
   * creates a new schema to the database
   * 
   * @param schemaName name of the schema to create
   * @param label label of the new schema
   * @param progress the progress feedback instance
   * @return the internal used schema name
   */
  public String createSchema(final String schemaName, final String label, final IProgress progress)
      throws SQLException;

  // /**
  // * saves a new label of an existing schema to the database
  // *
  // * @param schema
  // * the schema/data source to be renamed
  // * @param newLabel
  // * new label of the schema
  // * @param progress
  // * the progress feedback instance
  // * @return the root cluster
  // */
  // public Cluster relabelSchema(final DataSource schema, final String
  // newLabel,
  // final IProgress progress) throws SQLException;

  /**
   * publishes the root session id
   * 
   * @param rootSession the session to be used as root for this schema
   * @param progress the progress feedback instance
   */
  public void publishRootSession(Session rootSession, final IProgress progress) throws SQLException;

  /**
   * drops a hole schema from database
   * 
   * @param schemaName name of the schema to drop
   * @param progress the progress feedback instance
   */
  public void dropSchema(final String schemaName, final IProgress progress) throws SQLException;
}
