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

package de.hpi.fgis.ldp.server.persistency.loading.impl.base;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * an super class for loader classes to provide helper methods
 * 
 * @author toni.gruetze
 * 
 */
public abstract class LoaderBase {
  private Log logger;
  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private ISQLDataSourceProvider sqlSourceProvider;
  private String defaultUserView;

  protected LoaderBase() {

  }

  protected LoaderBase(ISQLDataSourceProvider sqlSourceProvider, String defaultUserView) {
    this.setSQLSourceProvider(sqlSourceProvider);
    this.setDefaultUserView(defaultUserView);
  }

  @Inject
  protected void setSQLSourceProvider(ISQLDataSourceProvider provider) {
    this.sqlSourceProvider = provider;
  }

  @Inject
  protected void setLogger(Log logger) {
    this.logger = logger;
  }

  @Inject
  protected void setDefaultUserView(@Named("db.defaultUserView") String defaultUserView) {
    this.defaultUserView = defaultUserView;
  }

  /**
   * Get the cluster constraint view of a statement for a given cluster id
   * 
   * @param cluster the cluster to get condition for
   * @return the constraint view
   */
  protected String getClusterConstraintView(final Cluster cluster) {
    return this.getClusterConstraintView(cluster, "");
  }

  /**
   * Get the cluster constraint view of a statement for a given cluster id
   * 
   * @param cluster the cluster to get condition for
   * @param cluster the extension of the view name
   * @return the constraint view
   */
  protected String getClusterConstraintView(final Cluster cluster, final String nameExtension) {
    // this would be the slower, but 100% correct alternative but should not
    // lead to other results for all clusterings
    // return
    // ", (SELECT cs.subject_id as condition_subject_id FROM clusters c, cluster_subjects cs WHERE c.id = cs.cluster_id AND c.id = "
    // + cluster.getId() + " AND c.username = '" +
    // this.getUserViewOf(cluster.getDataSource()) + "') cluster_condition";
    // this is the faster alternative
    return new StringBuilder(
        ", (SELECT subject_id as condition_subject_id FROM cluster_subjects WHERE Cluster_ID = ")
        .append(cluster.getId()).append(") cluster_condition").append(nameExtension).append(" ")
        .toString();
  }

  /**
   * Get the cluster condition of a statement to a given column
   * 
   * @param subjectColumn the subject id column to be matched
   * @return the constraint condition
   */
  protected String getClusterConstraintCondition(final String subjectColumn) {
    return this.getClusterConstraintCondition(subjectColumn, "");
  }

  /**
   * Get the cluster condition of a statement to a given column
   * 
   * @param subjectColumn the subject id column to be matched
   * @param cluster the extension of the view name
   * @return the constraint condition
   */
  protected String getClusterConstraintCondition(final String subjectColumn,
      final String nameExtension) {
    return new StringBuilder(" ").append(subjectColumn).append(" = cluster_condition")
        .append(nameExtension).append(".condition_subject_id ").toString();
  }

  /**
   * Get the cluster condition of a statement for a given cluster id
   * 
   * @param clusterID the cluster id to get condition for
   * @param subjectColumn the subject id column to be matched
   * @return the cluster condition
   * @deprecated use {@link LoaderBase#getClusterConstraintView(int)} and
   *             {@link LoaderBase#getClusterConstraintCondition(String)} instead for performance
   *             improvements
   */
  @Deprecated
  protected String getClusterCondition(final int clusterID, final String subjectColumn) {
    if (clusterID < 0) {
      return "";
    }
    return " AND " + subjectColumn
        + " IN (SELECT subject_id FROM Cluster_Subjects WHERE Cluster_ID = " + clusterID + ")";
  }

  /**
   * creates a SQL List for the given set of elements.
   * 
   * @param tIntSet the elements to put in the SQL list.
   * @return the SQL list as a {@link String}.
   */
  protected String getSQLList(TIntSet tIntSet) {
    StringBuilder sb = new StringBuilder("(");

    boolean firstElement = true;
    TIntIterator elementIterator = tIntSet.iterator();
    while (elementIterator.hasNext()) {
      if (firstElement) {
        firstElement = false;
      } else {
        sb.append(", ");
      }
      sb.append(elementIterator.next());
    }

    return sb.append(")").toString();
  }

  /**
   * gets the name of the user view defined for the data source
   * 
   * @param source the data source to get user view for
   * @return the label of the user view
   */
  protected String getUserViewOf(DataSource source) {
    final String userView = source.getUserView();
    if (userView == null) {
      return this.defaultUserView;
    }
    return userView;
  }

  /**
   * gets the name of the cluster partition
   * 
   * @param cluster the cluster to get partition for
   * @param con the {@link Connection} to be used for data extraction
   * @return the name of the partition
   */
  protected String getPartition(final Cluster cluster, final Connection con) {
    if (cluster.getId() >= 0) {
      Statement stmt = null;
      ResultSet rs = null;
      try {
        stmt = con.createStatement();

        final String userView = this.getUserViewOf(cluster.getDataSource());

        final String stmtString =
            this.resourceReader.getUndocumentedStringFromResource(LoaderBase.class.getPackage(),
                "sql/selectPartition.sql", Integer.valueOf(cluster.getId()), userView);
        rs = stmt.executeQuery(stmtString);

        if (rs.next()) {
          return rs.getString(1);
        }
      } catch (SQLException e) {
        logger.error("Unable to get cluster partitions from DB!", e);
      } finally {
        try {
          if (stmt != null) {
            stmt.close();
          }
          if (rs != null) {
            rs.close();
          }
        } catch (SQLException e) {
          // ignore Errors while closing
          logger.warn(this.getClass().getName() + "#getPartition()", e);
        }
      }
    }
    // default
    return "maintable";
  }

  /**
   * gets a new connection to the database
   * 
   * @param source the data source to get a new connection for
   * @return the new connection
   * @throws SQLException if sth. went wrong
   */
  protected Connection newConnection(final DataSource source) throws SQLException {
    return this.sqlSourceProvider.getConnectionPool(source.getLabel()).getConnection();
  }

  protected String getDataSourceType(final DataSource source) {
    return this.sqlSourceProvider.getConnectionPool(source.getLabel()).getType();
  }

  protected Collection<String> getTables(final DataSource source) {
    return this.sqlSourceProvider.getConnectionPool(source.getLabel()).getTables();
  }

  protected void dropConnections(final DataSource source) {
    this.sqlSourceProvider.dropConnectionPool(source.getLabel());
  }
}
