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

import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * ClusterLoader loads cluster sessions from storage (e.g. from the database).
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 */
public interface IClusterLoader {

  /**
   * loads a cluster session form storage
   * 
   * @param parent the parent cluster to be loaded
   * @return the cluster session
   */
  public IClusterFactory loadSession(final Cluster cluster);

  /**
   * gets a list of (sub-)clusters of the given cluster id
   * 
   * @param cluster the parent cluster of the cluster to load
   * @param progress the progress feedback instance
   * @return the list of clusters
   */
  public List<Cluster> getClusters(final Cluster cluster, final IProgress progress)
      throws SQLException;

  /**
   * gets a list of {@link IClusterDetails}
   * 
   * @param clusters the clusters to get details for
   * @param progress the progress feedback instance
   * @return the list of cluster details
   */
  public List<EntityCluster> getEntityClusters(List<Cluster> clusters, final IProgress progress)
      throws SQLException;

  /**
   * gets the parent cluster id of a given session id
   * 
   * @param sessionID the child session id of the cluster to load
   * @param progress the progress feedback instance
   * @return the id of the cluster
   */
  @Deprecated
  public int getParentClusterID(final Session session, final IProgress progress)
      throws SQLException;

  /**
   * 
   * Returns some random cluster subjects
   * 
   * @param cluster the cluster to get information for
   * @param sampleSize the size of the sample
   * @param progress the progress feedback instance
   * @return a table with the subject samples
   * @deprecated use {@link IClusterLoader#getSortetSubjects} instead
   */
  @Deprecated
  public abstract IDataTable getSubjectSamples(Cluster cluster, int sampleSize,
      final IProgress progress) throws SQLException;

  /**
   * 
   * Returns a sorted table of cluster subjects and their tuplecount
   * 
   * @param clusterID the id of the cluster to get information for
   * @param fromRow the row number of the first subject to be loaded
   * @param toRow the row number of the last subject to be loaded
   * @param progress the progress feedback instance
   * @return a table with the subjects
   */
  public abstract IDataTable getSortetSubjects(Cluster cluster, int from, int to,
      final IProgress progress) throws SQLException;

  /**
   * 
   * Returns the set of triples of the requested cluster
   * 
   * @param source the cluster of the subject
   * @param subject the subject to be loaded
   * @param progress the progress feedback instance
   * @return a table with the triples of the specified subject
   */
  public abstract IDataTable getSubjectTriples(Cluster cluster, Subject subject,
      final IProgress progress) throws SQLException;

  /**
   * Returns general Information about the cluster
   * 
   * @param cluster the cluster to get information for
   * @param progress the progress feedback instance
   * @return a table with general information
   */
  public abstract IDataTable getMetaInformation(Cluster cluster, final IProgress progress)
      throws SQLException;

}
