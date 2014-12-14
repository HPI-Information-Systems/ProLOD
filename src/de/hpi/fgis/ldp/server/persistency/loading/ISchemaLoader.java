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
import java.util.List;

import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * SchemaLoader loads schema informations data source (e.g. from the database).
 * 
 * @author toni.gruetze
 */
public interface ISchemaLoader {
  /**
   * loads the root cluster of all data sources
   * 
   * @param progress the progress feedback instance
   * @return the root clusters
   */
  public List<Cluster> getRootClusters(final IProgress progress) throws SQLException;

  /**
   * loads the root cluster of a data source
   * 
   * @param source the data schema to be accessed
   * @param progress the progress feedback instance
   * @return the root cluster
   */
  public Cluster getRootCluster(final DataSource source, final IProgress progress)
      throws SQLException;
}
