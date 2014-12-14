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
import java.util.List;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * Enables the storage of cluster sessions (e.g. to the database).
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 * 
 */
public interface IClusterStorage {
  /**
   * Stores the given session
   * 
   * @param parent the parent cluster to be sub clustered
   * @param session the parent cluster to be stored
   * @param progress the progress feedback instance
   */
  public void storeSession(Cluster parent, final Session session, IProgress progress)
      throws SQLException;

  /**
   * sets the given session as a child of the cluster
   * 
   * @param cluster The cluster to be subclustered
   * @param session the session to be stored
   * @param progress the progress feedback instance
   */
  public void setChildSession(Cluster cluster, Session session, IProgress progress)
      throws SQLException;

  /**
   * Stores a session an all its child sessions recursively. Returns the list of session ids of the
   * session that were created while storing.
   * 
   * @param session the parent session to be stored
   * @param progress the progress feedback instance
   * @return a list of all stored session ids
   */
  public List<Integer> storeSessionRecursively(final Session session, IProgress progress)
      throws SQLException;

  /**
   * renames the given cluster the parent cluster id of a given session id
   * 
   * @param cluster The cluster to be renamed
   * @param newName the new name of the cluster
   * @return the new cluster
   */
  public Cluster renameCluster(final Cluster cluster, final String newName) throws SQLException;

  /**
   * creates partitions for a given set of clusters
   * 
   * @param cluster The cluster to create a partition for
   * @param progress the progress feedback instance
   */
  public void createClusterPartition(final Cluster cluster, final IProgress progress)
      throws SQLException;

  /**
   * copies the clustering of the given source
   * 
   * @param sourceSchema the source schema to copy the clustering from
   * @param userView the name of the user view to be created
   * @param progress the progress feedback instance
   * @return the root session
   */
  public void copyClustering(final DataSource sourceSchema, final String userView,
      final IProgress progress) throws SQLException;
}
