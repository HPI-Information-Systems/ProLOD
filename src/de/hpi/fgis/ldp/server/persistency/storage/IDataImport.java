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

import de.hpi.fgis.ldp.server.datastructures.ImportTuple;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * enables the import of large data set to a empty schema
 * 
 * @author toni.gruetze
 * 
 */
public interface IDataImport {
  /**
   * stores all elements of <code>elements</code> in the database
   * 
   * @param entries the entries to be stored
   * @return number of read tuples
   */
  public int store(Iterable<ImportTuple> entries) throws SQLException;

  /**
   * sets the schema name for the data to be stored
   * 
   * @param schema the schema name
   */
  public void setSchemaName(final String schema);

  public void createMetaTables(IProgress progress) throws SQLException;

  public void createMainTable(IProgress progress) throws SQLException;

  public void createMaterializedViews(IProgress progress) throws SQLException;

  public void createClusterTables(IProgress progress) throws SQLException;

  public void cleanup(IProgress progress) throws SQLException;
}
