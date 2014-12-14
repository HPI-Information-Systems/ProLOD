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

package de.hpi.fgis.ldp.server.persistency.storage.impl.cluster;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * Enables the storage of cluster sessions to the DB2 database.
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 * 
 */
public class ClusterStorage extends LoaderBase implements IClusterStorage {
  // private IDataSource dataSource;
  private final Log logger;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final String defaultUserView;

  // // TODO inject schema via Configuration
  // public ClusterStorage(final String schema) {
  // // TODO inject
  // this.dataSource =
  // DataSourceProvider.getInstance().getConnectionPool(schema);
  // }
  @Inject
  protected ClusterStorage(Log logger, @Named("db.defaultUserView") String defaultUserView) {
    this.logger = logger;
    this.defaultUserView = defaultUserView;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#storeSession
   * (de.hpi.fgis.ldp.shared.data.cluster.ICluster, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void storeSession(final Cluster parent, final Session session, final IProgress progress)
      throws SQLException {
    if (!parent.getDataSource().equals(session.getDataSource())) {
      throw new IllegalArgumentException(
          "Unable to store session to cluster with different data source.");
    }
    Connection con = null;
    Statement storeClusterSession = null;
    PreparedStatement storeCluster = null;
    PreparedStatement storeClusterSubject = null;
    PreparedStatement storeClusterSchema = null;
    Statement tmpStatement = null;
    ResultSet rsKey = null;

    try {
      progress.startProgress("storing cluster session");

      DataSource source = session.getDataSource();
      con = super.newConnection(source);

      // prepare the cluster creation statement
      final String clusterStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?createCluster.sql");
      storeCluster = con.prepareStatement(clusterStmtString, Statement.RETURN_GENERATED_KEYS);

      // prepare the cluster subject creation statement
      final String clusterSubjectStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?storeClusterSubject.sql");
      storeClusterSubject =
          con.prepareStatement(clusterSubjectStmtString, Statement.NO_GENERATED_KEYS);

      final List<String> finishClusterSubjectsStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?finishClusterSubjectStorage.sql",
              source.getLabel());

      // prepare the cluster mean schema creation statement
      final String clusterSchemaStoreStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?storeMeanSchema.sql");
      storeClusterSchema =
          con.prepareStatement(clusterSchemaStoreStmtString, Statement.NO_GENERATED_KEYS);

      final List<String> finishClusterSchemaStmtStrings2 =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?finishMeanSchemaStorage.sql",
              source.getLabel());

      // create the cluster session creation statement
      final String clusterSessionStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?createClusterSession.sql",
              session.getName());

      storeClusterSession = con.createStatement();
      logger.debug(clusterSessionStmtString);
      storeClusterSession.executeUpdate(clusterSessionStmtString, Statement.RETURN_GENERATED_KEYS);

      // retrieve session id from db
      rsKey = storeClusterSession.getGeneratedKeys();
      rsKey.next();
      session.setId(rsKey.getInt(1));

      progress.stopProgress();
      rsKey.close();
      storeClusterSession.close();

      String userView = parent.getDataSource().getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }

      progress.startProgress("storing clusters", session.getEntityClusters().size());
      ArrayList<Integer> clusterIDs = new ArrayList<Integer>();
      // store clusters
      for (EntityCluster entityCluster : session.getEntityClusters()) {
        progress.continueProgress();
        // ignore empty clusters
        if (entityCluster.entityIds().size() <= 0) {
          continue;
        }

        storeCluster.setInt(1, session.getId());
        storeCluster.setInt(2, entityCluster.getMetaData().getIndex());
        storeCluster.setInt(3, entityCluster.entityIds().size());
        if (entityCluster.getChildSession() != null
            && entityCluster.getChildSession().getId() != -1) {
          storeCluster.setInt(4, entityCluster.getChildSession().getId());
        } else {
          storeCluster.setNull(4, Types.INTEGER);
        }

        if (-1 != entityCluster.getMetaData().getError()) {
          storeCluster.setDouble(5, entityCluster.getMetaData().getError());
        } else {
          storeCluster.setNull(5, Types.DOUBLE);
        }

        storeCluster.setString(6, userView);

        storeCluster.execute();

        // retrieve session id from db
        rsKey = storeCluster.getGeneratedKeys();
        rsKey.next();
        entityCluster.getMetaData().setId(rsKey.getInt(1));
        clusterIDs.add(Integer.valueOf(entityCluster.getMetaData().getId()));

        rsKey.close();

        // store mean schema
        if (null != entityCluster.getMeanSchema()) {
          int rank = 0;
          for (int attrId : entityCluster.getMeanSchema()) {
            storeClusterSchema.setInt(1, entityCluster.getMetaData().getId());
            storeClusterSchema.setInt(2, attrId);
            storeClusterSchema.setInt(3, rank++);
            storeClusterSchema.addBatch();
          }
          storeClusterSchema.executeBatch();
          storeClusterSchema.clearBatch();
        }

        for (final String currentStmt : finishClusterSchemaStmtStrings2) {
          tmpStatement = con.createStatement();
          tmpStatement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
          tmpStatement.close();
        }

        // write entities to db
        int batchCnt = 0;
        for (int entityId : entityCluster.entityIds()) {
          batchCnt++;
          if (1000 < batchCnt) {
            storeClusterSubject.executeBatch();
            storeClusterSubject.clearBatch();
            batchCnt = 0;
          }
          storeClusterSubject.setInt(1, entityCluster.getMetaData().getId());
          storeClusterSubject.setInt(2, entityId);
          storeClusterSubject.addBatch();
        }
        storeClusterSubject.executeBatch();

        for (final String currentStmt : finishClusterSubjectsStmtStrings) {
          tmpStatement = con.createStatement();
          logger.info(" --> " + currentStmt);
          tmpStatement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
          tmpStatement.close();
        }
        if (entityCluster.getClusterName() != null) {
          renameCluster(entityCluster.getMetaData(), entityCluster.getClusterName());
        }
      }
      // using parent partitions
      if (parent.getId() >= 0) {
        // use partition name from parent
        String partitionName = this.getPartitionName(parent, con, source, tmpStatement);
        this.publishPartition(partitionName, parent.getDataSource().getLabel(), clusterIDs, con,
            source, tmpStatement);
      }

      this.finishClustering(source, con);
      this.finishClusterSession(source, con);
    } catch (SQLException e) {
      logger.error("Unable to store cluster data to DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }

        if (storeClusterSession != null) {
          storeClusterSession.close();
        }
        if (storeCluster != null) {
          storeCluster.close();
        }
        if (storeClusterSubject != null) {
          storeClusterSubject.close();
        }
        if (storeClusterSchema != null) {
          storeClusterSchema.close();
        }
        if (tmpStatement != null) {
          tmpStatement.close();
        }

        if (rsKey != null) {
          rsKey.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#storeSession()", e);
      }
    }

  }

  private void storeSessionRecursively(Cluster parent, final Session session, List<Integer> idList,
      final IProgress progress) throws SQLException {
    progress.startProgress("storing session data recursively",
        session.getEntityClusters().size() + 1);

    int currentChild = 0;
    for (EntityCluster entityCluster : session.getEntityClusters()) {
      Session childSession = entityCluster.getChildSession();
      if (null != childSession) {
        storeSessionRecursively(entityCluster.getMetaData(), childSession, idList,
            progress.continueWithSubProgressAt(currentChild, 1));
      }
      currentChild++;
    }
    this.storeSession(parent, session, progress.continueWithSubProgressAt(currentChild, 1));
    idList.add(Integer.valueOf(session.getId()));
    progress.stopProgress();
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#
   * storeSessionRecursively(de.hpi.fgis.ldp.shared.data.cluster.ICluster,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public List<Integer> storeSessionRecursively(final Session session, final IProgress progress)
      throws SQLException {
    List<Integer> ids = new ArrayList<Integer>();
    storeSessionRecursively(null, session, ids, progress);
    return ids;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#storeChildSession
   * (de.hpi.fgis.ldp.shared.data.cluster.ICluster, de.hpi.fgis.ldp.server.datastructures.Session,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void setChildSession(final Cluster cluster, Session session, final IProgress progress)
      throws SQLException {
    Connection con = null;
    Statement storeCluster = null;

    try {
      progress.startProgress("storing cluster child session");

      String userView = cluster.getDataSource().getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }

      final DataSource source = cluster.getDataSource();
      con = super.newConnection(source);

      // create the cluster session creation statement
      final String clusterStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?updateChildSession.sql",
              Integer.toString(cluster.getId()),
              (session.getId() == -1 ? "NULL" : Integer.toString(session.getId())), userView);

      storeCluster = con.createStatement();
      storeCluster.execute(clusterStmtString, Statement.NO_GENERATED_KEYS);

      this.finishClustering(source, con);
    } catch (SQLException e) {
      logger.error("Unable to store cluster data to DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (storeCluster != null) {
          storeCluster.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#storeChildSession()", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#renameCluster
   * (de.hpi.fgis.ldp.shared.data.Cluster, java.lang.String)
   */
  @Override
  public Cluster renameCluster(final Cluster cluster, String newName) throws SQLException {
    Connection con = null;
    PreparedStatement renameCluster = null;

    try {
      final DataSource source = cluster.getDataSource();
      con = super.newConnection(source);

      // create the cluster session creation statement
      final String clusterRenameStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?renameCluster.sql");

      String userView = cluster.getDataSource().getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }

      renameCluster = con.prepareStatement(clusterRenameStmtString, Statement.NO_GENERATED_KEYS);

      renameCluster.setString(1, newName);
      renameCluster.setInt(2, cluster.getId());
      renameCluster.setString(3, userView);

      renameCluster.execute();

      this.finishClustering(source, con);

      // TODO result may has to be loaded from db
      cluster.setLabel(newName);
      return cluster;
    } catch (SQLException e) {
      logger.error("Unable to rename cluster data in DB!", e);
      throw e;
    } finally {
      try {
        if (con != null) {
          if (!con.getAutoCommit()) {
            con.commit();
          }
          con.close();
        }
        if (renameCluster != null) {
          renameCluster.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#renameCluster()", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#
   * createClusterPartition(de.hpi.fgis.ldp.shared.data.cluster.ICluster,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void createClusterPartition(final Cluster cluster, final IProgress progress)
      throws SQLException {

    // no valid cluster
    if (cluster.getId() < 0) {
      return;
    }
    //
    Connection con = null;
    Statement statement = null;
    int oldTransactionLevel = -1;
    final String partitionName = "maintable_" + cluster.getId();

    try {
      final DataSource source = cluster.getDataSource();
      con = super.newConnection(source);

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the (table) creation statement
      final List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?createPartition.sql", partitionName,
              Integer.valueOf(cluster.getId()), cluster.getDataSource().getLabel());

      progress.startProgress("creating partition tables", createStmtStrings.size() + 3);

      for (final String currentStmt : createStmtStrings) {
        statement = con.createStatement();
        statement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        statement.close();
        progress.continueProgress();
      }

      // get children recursively
      ArrayList<Integer> children = new ArrayList<Integer>();
      children.add(Integer.valueOf(cluster.getId()));
      children.addAll(this.getChildren(children, source, con));
      progress.continueProgress();
      statement = con.createStatement();
      this.publishPartition(partitionName, cluster.getDataSource().getLabel(), children, con,
          source, statement);
      progress.continueProgress();
      this.finishClustering(source, con);
      progress.continueProgress();
    } catch (SQLException e) {
      logger.error("Unable to create cluster partitions in DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (statement != null) {
          statement.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#createClusterPartitions()", e);
      }
    }
  }

  private String getPartitionName(Cluster cluster, Connection con, DataSource source,
      Statement statement) throws SQLException {
    ResultSet rsPartitionName = null;
    try {
      String userView = source.getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }

      // select the cluster partition name
      final String selectChildrenStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?selectPartitionName.sql",
              Integer.valueOf(cluster.getId()), userView);

      statement = con.createStatement();
      rsPartitionName = statement.executeQuery(selectChildrenStmtString);

      if (rsPartitionName.next()) {
        return rsPartitionName.getString(1);
      } else {
        throw new IllegalStateException("Unable to retrieve cluster partition of cluster "
            + cluster.getId());
      }
    } finally {
      try {
        if (rsPartitionName != null) {
          rsPartitionName.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#getPartitionName()", e);
      }
    }
  }

  private void publishPartition(String partitionName, String schemaName,
      ArrayList<Integer> clusterIds, Connection con, DataSource source, Statement statement)
      throws SQLException {
    TIntSet clusterIdSet = new TIntHashSet(clusterIds);
    final String clusterIDString = this.getSQLList(clusterIdSet);
    // create the cluster partition publish statement
    final String publishStmtString =
        this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(), "sql/?"
            + super.getDataSourceType(source) + "/?publishPartition.sql", clusterIDString,
            partitionName, schemaName);

    statement = con.createStatement();
    statement.execute(publishStmtString, Statement.NO_GENERATED_KEYS);
    statement.close();
  }

  private Collection<Integer> getChildren(final Collection<Integer> clusterIDs,
      final DataSource source, final Connection con) throws SQLException {
    Statement selectChildren = null;
    ResultSet rs = null;
    ArrayList<Integer> allChildren = new ArrayList<Integer>();

    try {

      String userView = source.getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }
      TIntSet clusterIDSet = new TIntHashSet(clusterIDs);
      // create the cluster selection statement
      final String selectChildrenStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?selectChildren.sql",
              this.getSQLList(clusterIDSet), userView);

      selectChildren = con.createStatement();
      rs = selectChildren.executeQuery(selectChildrenStmtString);

      while (rs.next()) {
        allChildren.add(Integer.valueOf(rs.getInt(1)));
      }

    } catch (SQLException e) {
      logger.error("Unable to read cluster data from DB!", e);
      throw e;
    } finally {
      try {
        if (selectChildren != null) {
          selectChildren.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        logger.warn(this.getClass().getName() + "#getChildren()", e);
      }
    }
    if (allChildren.size() > 0) {
      allChildren.addAll(this.getChildren(allChildren, source, con));
    }

    return allChildren;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.IClusterStorage#copyClustering
   * (de.hpi.fgis.ldp.shared.data.DataSource, java.lang.String,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void copyClustering(DataSource sourceSchema, String userView, IProgress progress)
      throws SQLException {

    Connection con = null;
    Statement copyClustering = null;
    try {
      con = super.newConnection(sourceSchema);

      String sourceView = sourceSchema.getUserView();
      if (sourceView == null) {
        sourceView = defaultUserView;
      }
      String targetView = userView;
      if (targetView == null) {
        targetView = defaultUserView;
      }

      /*
       * -- %1$s the new user view -- %2$s the source user view
       */
      // create the table creation statement
      final String copyClusteringStmtString =
          this.resourceReader.getStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(sourceSchema) + "/?copyClustering.sql", sourceView,
              targetView);

      progress.startProgress("copying clustering", 2);

      copyClustering = con.createStatement();
      copyClustering.execute(copyClusteringStmtString, Statement.NO_GENERATED_KEYS);
      copyClustering.close();

      progress.continueProgress();

      this.finishClustering(sourceSchema, con);
    } catch (SQLException e) {
      logger.error("Unable to copy clustering from \"" + sourceSchema.getUserView() + "\" to \""
          + userView + "\"!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (copyClustering != null) {
          copyClustering.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#copyClustering()", e);
      }
    }
  }

  private void finishClustering(DataSource sourceSchema, Connection con) throws SQLException {

    final List<String> finishClusteringStmtStrings =
        this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
            "sql/?" + super.getDataSourceType(sourceSchema) + "/?finishClusterCreation.sql",
            sourceSchema.getLabel());

    for (final String currentStmt : finishClusteringStmtStrings) {
      Statement statement = con.createStatement();
      statement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
      statement.close();
    }
  }

  private void finishClusterSession(DataSource sourceSchema, Connection con) throws SQLException {

    final List<String> finishClusterSessionStmtStrings =
        this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
            "sql/?" + super.getDataSourceType(sourceSchema) + "/?finishClusterSessionCreation.sql",
            sourceSchema.getLabel());

    for (final String currentStmt : finishClusterSessionStmtStrings) {
      Statement statement = con.createStatement();
      statement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
      statement.close();
    }
  }

}
