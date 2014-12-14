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

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

// FIXME comments
public class DataSourceProvider implements ISQLDataSourceProvider {

  @Inject
  private DataSourceProvider(Provider<IDataSource> provider) {
    this.provider = provider;
  }

  private final Provider<IDataSource> provider;
  private final Map<String, IDataSource> dataSources = new HashMap<String, IDataSource>();

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider#
   * getConnectionPool(java.lang.String)
   */
  @Override
  public IDataSource getConnectionPool(String schema) {
    IDataSource result = null;
    synchronized (this.dataSources) {
      // search for schema
      if (this.dataSources.containsKey(schema)) {
        result = this.dataSources.get(schema);
      } else {
        // unknown / new schema
        result = this.provider.get();
        result.setName(schema);
        this.dataSources.put(schema, result);
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider#
   * dropConnectionPool(java.lang.String)
   */
  @Override
  public void dropConnectionPool(String schema) {
    synchronized (this.dataSources) {
      // search for schema
      if (this.dataSources.containsKey(schema)) {
        this.dataSources.remove(schema).close();
      }
    }
  }
}
