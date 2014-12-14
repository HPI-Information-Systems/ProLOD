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

package de.hpi.fgis.ldp.server.persistency.access;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * holds information about the data source
 * 
 * @author toni.gruetze
 * 
 */
public interface IDataSource extends Closeable {
  /**
   * Gets the unique name of the content source (e.g. name of db schema)
   * 
   * @return the unique name of the content source
   */
  public String getName();

  /**
   * Sets the unique name of the content source (e.g. name of db schema)
   * 
   * @param name the unique name of the content source
   */
  public void setName(String name);

  /**
   * Gets the name of the type of the data source
   * 
   * @return the name of the type of the data source
   */
  public String getType();

  /**
   * Gets the type of the data source
   * 
   * @return the type of the data source
   * @throws SQLException if the database is not available
   */
  public Connection getConnection();

  /*
   * (non-Javadoc)
   * 
   * @see java.io.Closeable#close()
   */
  @Override
  public void close();

  /**
   * optimizes the database on the {@link IDataSource}
   */
  public void optimize();

  /**
   * gets all tables of the schema of this database connection
   * 
   * @return all tables
   */
  public abstract Collection<String> getTables();
}
