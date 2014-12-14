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

package de.hpi.fgis.ldp.server.persistency.loading.impl.cluster;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.EntityCluster;
import de.hpi.fgis.ldp.server.datastructures.IClusterFactory;
import de.hpi.fgis.ldp.server.datastructures.ISchema;
import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.ObjectValue;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.Subject;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;

/**
 * Loads cluster information from DB2
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 */
public class ClusterLoader extends LoaderBase implements IClusterLoader {
  protected final Log logger;

  private final String defaultUserView;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final IMetaLoader metaLoader;

  private int fetchRowSize = 100;

  @Inject
  protected ClusterLoader(IMetaLoader metaLoader, Log logger,
      @Named("db.defaultUserView") String defaultUserView) {
    this.metaLoader = metaLoader;
    this.logger = logger;
    this.defaultUserView = defaultUserView;
  }

  /**
   * sets the fetchRowSize
   * 
   * @param fetchRowSize the fetchRowSize to set
   */
  @Inject
  public void setFetchRowSize(@Named("db.fetchRowSize") int fetchRowSize) {
    this.fetchRowSize = fetchRowSize;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader#loadSession (int)
   */
  @Override
  public IClusterFactory loadSession(final Cluster parent) {
    return new ClusterFactory(parent);
  }

  /**
   * 
   * @param sessionID
   * @param progress
   * @return
   */
  protected List<EntityCluster> getEntityClusters(final Cluster parent, final IProgress progress)
      throws SQLException {
    final List<Cluster> clusters = this.getClusters(parent, progress);
    return this.getEntityClusters(clusters, progress);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader#getEntityClusters
   * (java.util.List, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public List<EntityCluster> getEntityClusters(List<Cluster> clusters, final IProgress progress)
      throws SQLException {
    Connection con = null;
    PreparedStatement loadMean = null;
    PreparedStatement loadEntities = null;
    final List<EntityCluster> clusterDetails = new ArrayList<EntityCluster>();

    try {
      progress.startProgress("loading schema meta information", clusters.size());

      // FIXME check weather all clusters have the same data source
      final DataSource source = clusters.get(0).getDataSource();
      con = super.newConnection(source);

      // create the mean schema statement
      String meanStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?selectMeanSchema.sql");

      loadMean = con.prepareStatement(meanStmtString);

      // create the entity selection statement
      String entityStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?selectEntities.sql");

      loadEntities = con.prepareStatement(entityStmtString);
      // injected
      loadEntities.setFetchSize(this.fetchRowSize);

      for (final Cluster currentCluster : clusters) {

        // load mean schema for this cluster
        loadMean.setInt(1, currentCluster.getId());
        final ResultSet rsMean = loadMean.executeQuery();
        List<Integer> meanAttributes = new ArrayList<Integer>();
        while (rsMean.next()) {
          meanAttributes.add(Integer.valueOf(rsMean.getInt(1)));
        }
        rsMean.close();

        Schema mean = meanAttributes.isEmpty() ? null : new Schema(this.intArray(meanAttributes));

        // load entities
        loadEntities.setInt(1, currentCluster.getId());
        final ResultSet rsEntities = loadEntities.executeQuery();
        List<Integer> entityList = new ArrayList<Integer>();
        while (rsEntities.next()) {
          entityList.add(Integer.valueOf(rsEntities.getInt(1)));
        }
        rsEntities.close();

        int[] entities = this.intArray(entityList);
        Arrays.sort(entities);

        clusterDetails.add(new EntityCluster(currentCluster, entities, mean));

        progress.continueProgress();
      }

      if (clusterDetails.isEmpty()) {
        // session does not exist
        return null;
      }

    } catch (SQLException e) {
      this.logger.error("Unable to get entity cluster data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }

        if (loadEntities != null) {
          loadEntities.close();
        }
        if (loadMean != null) {
          loadMean.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClusterDetails()", e);
      }
    }
    return clusterDetails;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IClusterLoader#getClusters (int,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public List<Cluster> getClusters(final Cluster parent, final IProgress progress)
      throws SQLException {

    Connection con = null;
    PreparedStatement loadClusters = null;
    ResultSet rsClusters = null;
    List<Cluster> clusterDetails = new ArrayList<Cluster>();

    try {
      progress.startProgress("selecting clusters");

      final DataSource source = parent.getDataSource();
      con = super.newConnection(source);

      // create the cluster selection statement
      final String clusterStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?selectClusters.sql");

      loadClusters = con.prepareStatement(clusterStmtString);

      loadClusters.setInt(1, parent.getChildSessionID());

      String userView = parent.getDataSource().getUserView();
      if (userView == null) {
        userView = this.defaultUserView;
      }
      loadClusters.setString(2, userView);

      rsClusters = loadClusters.executeQuery();

      while (rsClusters.next()) {
        final Cluster newElement = new Cluster(parent.getDataSource());

        newElement.setId(rsClusters.getInt(1));
        newElement.setIndex(rsClusters.getInt(2));
        final String label = rsClusters.getString(3);
        if (label == null) {
          newElement.setLabel("null");
        } else {
          newElement.setLabel(label);
        }

        final int childSession = rsClusters.getInt(4);
        final boolean hasChildSession = !rsClusters.wasNull();
        if (hasChildSession) {
          newElement.setChildSessionID(childSession);
        }

        newElement.setError(rsClusters.getDouble(5));

        newElement.setSize(rsClusters.getInt(6));

        clusterDetails.add(newElement);
      }

      if (clusterDetails.isEmpty()) {
        // session does not exist
        return new ArrayList<Cluster>();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to get entity cluster data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }

        if (rsClusters != null) {
          rsClusters.close();
        }
        if (loadClusters != null) {
          loadClusters.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClusters()", e);
      }
    }
    return clusterDetails;
  }

  /**
   * creates cluster lists for a given cluster session from DB2 this is a wrapper for the
   * IClusterFactory interface
   * 
   * @author daniel.hefenbrock
   * @author toni.gruetze
   * 
   */
  private class ClusterFactory implements IClusterFactory {

    private final Cluster parent;

    /**
     * Create a new {@link ClusterFactory}
     * 
     * @param parent the parent cluster
     */
    public ClusterFactory(final Cluster parent) {
      this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.datastructures.IClusterFactory#createClusters ()
     */
    @Override
    public List<EntityCluster> getEntityClusters(final IProgress progress) {
      try {
        return ClusterLoader.this.getEntityClusters(this.parent, progress);
      } catch (SQLException e) {
        ClusterLoader.this.logger.error("Unable to get clustersfrom DB!", e);
      }
      // default return empty list
      return new ArrayList<EntityCluster>();
    }
  }

  private class Schema extends AbstractList<Integer> implements ISchema {
    private final int[] properties;

    protected Schema(int[] properties) {
      this.properties = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public Integer get(int index) {
      return Integer.valueOf(this.properties[index]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
      return this.properties.length;
    }

  }

  private int[] intArray(List<Integer> list) {
    int[] res = new int[list.size()];
    int index = 0;
    for (int val : list) {
      res[index++] = val;
    }
    return res;
  }

  @Override
  @Deprecated
  public IDataTable getSubjectSamples(Cluster cluster, int sampleSize, final IProgress progress)
      throws SQLException {

    // create resultTable
    // IntegerColumn key = new IntegerColumn("Subject ID",false);
    // StringColumn subj = new StringColumn("Subject",true);
    // IntegerColumn props = new IntegerColumn("#Properties",true);
    DataColumn<Integer> key = new DataColumn<Integer>("Subject ID", false);
    DataColumn<String> subj = new DataColumn<String>("Subject", true);
    DataColumn<Integer> props = new DataColumn<Integer>("#Properties", true);

    DataTable resultTable = new DataTable(key);
    resultTable.addColumn(subj);
    resultTable.addColumn(props);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("selecting samples", sampleSize);

      final DataSource source = cluster.getDataSource();
      con = super.newConnection(source);
      stmt = con.createStatement();

      String constraintView = " ";
      String constraintCondition = " ";
      if (cluster.getId() >= 0) {
        constraintView = super.getClusterConstraintView(cluster);
        constraintCondition = " WHERE " + super.getClusterConstraintCondition("ID");
      }

      // create the cluster selection statement
      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?clusterSubjects.sql", constraintView,
              constraintCondition, Integer.valueOf(sampleSize));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      int row = 0;

      while (rs.next()) {
        key.setElement(row, Integer.valueOf(rs.getInt(1)));
        // TODO remove replace all
        subj.setElement(row, rs.getString(2).replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
        props.setElement(row, Integer.valueOf(rs.getInt(3)));

        if (row % (sampleSize / 5) == 0) {
          progress.continueProgressAt(row);
        }
        row++;
      }
      rs.close();

    } catch (SQLException e) {
      this.logger.error("Unable to get subject sample from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getSubjectSamples()", e);
      }
    }
    return resultTable;
  }

  @Override
  public IDataTable getMetaInformation(final Cluster cluster, final IProgress progress)
      throws SQLException {
    // Doesn't work without a cluster
    if (cluster.getId() <= -1) {
      throw new IllegalStateException("Not (yet) implemented!");
    }

    // create resultTable
    DataColumn<String> key = new DataColumn<String>("Key", true);
    DataColumn<String> value = new DataColumn<String>("Value", true);

    DataTable resultTable = new DataTable(key);
    resultTable.addColumn(value);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("retrieving cluster information");

      final DataSource source = cluster.getDataSource();
      con = super.newConnection(source);
      stmt = con.createStatement();

      String userView = cluster.getDataSource().getUserView();
      if (userView == null) {
        userView = this.defaultUserView;
      }

      // create the cluster selection statement
      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?clusterInformation.sql",
              this.getPartition(cluster, con), Integer.valueOf(cluster.getId()), userView);

      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      if (rs.next()) {
        key.setElement(0, "Label");
        value.setElement(0, rs.getString(1));

        String targetUserView = rs.getString(5);
        // default view
        if (targetUserView.equals(this.defaultUserView)) {
          targetUserView = "[" + this.defaultUserView + "]";
        }

        key.setElement(1, "User view");
        value.setElement(1, targetUserView);

        key.setElement(2, "Entities");
        value.setElement(2, Integer.toString(rs.getInt(3)));

        key.setElement(3, "RDF triples");
        value.setElement(3, Integer.toString(rs.getInt(4)));

        key.setElement(4, "Average error");
        value.setElement(4, Double.toString(rs.getDouble(2)));

        key.setElement(5, "Average properties per entity");
        value
            .setElement(5,
                Double.toString((double) Math.round((((double) rs.getInt(4)) / ((double) rs
                    .getInt(3))) * 100) / 100));

        key.setElement(7, "internal Schema");
        value.setElement(7, source.getLabel());

        key.setElement(8, "internal Cluster ID");
        value.setElement(8, Integer.toString(cluster.getId()));

      }
      rs.close();

    } catch (SQLException e) {
      this.logger.error("Unable to get cluster meta information from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getMetaInformation()", e);
      }
    }

    ArrayList<Cluster> simpleClusters = new ArrayList<Cluster>(1);
    simpleClusters.add(cluster);

    List<EntityCluster> clusterDetails = this.getEntityClusters(simpleClusters, progress);

    if (clusterDetails.size() == 1) {
      EntityCluster details = clusterDetails.get(0);

      // get property name for the each property id
      TIntObjectHashMap<String> idNameMap = new TIntObjectHashMap<String>();
      for (Integer propID : details.getMeanSchema()) {
        idNameMap.put(propID, null);
      }

      // load predicate names
      this.metaLoader.fillTransactionNameMap(idNameMap, "sp", cluster.getDataSource());

      StringBuilder meanSchema = new StringBuilder();
      for (String entry : idNameMap.valueCollection()) {
        // TODO remove replace all & <br> by \n
        meanSchema.append(entry.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("<br/>");
      }
      key.setElement(6, "Mean schema");
      value.setElement(6, meanSchema.toString().trim());
    }

    return resultTable;
  }

  @Override
  @Deprecated
  public int getParentClusterID(final Session session, IProgress progress) throws SQLException {
    Connection con = null;
    PreparedStatement loadClusterID = null;
    ResultSet rs = null;

    int parentClusterId = -1;

    try {
      progress.startProgress("loading parent cluster");

      con = super.newConnection(session.getDataSource());

      // create the mean schema statement
      String clusterIDStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(session.getDataSource())
                  + "/?selectParentCluster.sql");

      loadClusterID = con.prepareStatement(clusterIDStmtString);

      loadClusterID.setInt(1, session.getId());
      String userView = session.getDataSource().getUserView();
      if (userView == null) {
        userView = this.defaultUserView;
      }
      loadClusterID.setString(2, userView);

      rs = loadClusterID.executeQuery();
      if (rs.next()) {
        parentClusterId = rs.getInt(1);
      }
    } catch (SQLException e) {
      this.logger.error("Unable to get parent cluster from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (loadClusterID != null) {
          loadClusterID.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getParentClusterID()", e);
      }
    }

    return parentClusterId;
  }

  @Override
  public IDataTable getSortetSubjects(Cluster cluster, int from, int to, IProgress progress)
      throws SQLException {

    // create resultTable
    DataColumn<Subject> subject = new DataColumn<Subject>("Subject", true);
    DataColumn<Integer> tupleCount = new DataColumn<Integer>("#Triples", true);

    DataTable resultTable = new DataTable(subject);
    resultTable.addColumn(tupleCount);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("selecting subjects", to - from);

      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      String constraintView = " ";
      String constraintCondition = " ";
      if (cluster.getId() >= 0) {
        constraintView = super.getClusterConstraintView(cluster);
        constraintCondition = " WHERE " + super.getClusterConstraintCondition("ID");
      }

      // create the cluster selection statement
      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?sortedClusterSubjects.sql", constraintView, constraintCondition,
              Integer.valueOf(to));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      int row = 0;
      while (rs.next()) {
        if (from <= row && row <= to) {
          // Subject
          subject.setElement(row - from, new Subject(rs.getInt(1), rs.getString(2)));

          // count
          tupleCount.setElement(row - from, Integer.valueOf(rs.getInt(3)));
        }
        row++;
      }
      rs.close();

    } catch (SQLException e) {
      this.logger.error("Unable to get subject sample from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getSortetSubjects()", e);
      }
    }
    return resultTable;
  }

  @Override
  public IDataTable getSubjectTriples(final Cluster cluster, Subject subject, IProgress progress)
      throws SQLException {

    // create resultTable
    DataColumn<Predicate> predicate = new DataColumn<Predicate>("Predicate", true);
    DataColumn<ObjectValue> object = new DataColumn<ObjectValue>("Object", true);

    DataTable resultTable = new DataTable(predicate);
    resultTable.addColumn(object);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("selecting subjects");

      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      // create the cluster selection statement
      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?selectSubjectTuples.sql", this.getPartition(cluster, con),
              Integer.toString(subject.getId()));

      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      int row = 0;
      while (rs.next()) {
        // Predicate
        predicate.setElement(row, new Predicate(rs.getInt(1), rs.getString(2)));

        // Object
        object.setElement(row, new ObjectValue(rs.getInt(3), rs.getString(4)));
        row++;
      }
      rs.close();

    } catch (SQLException e) {
      this.logger.error("Unable to get tuples for subject \"" + subject + "\" from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getSubjectTriples()", e);
      }
    }
    return resultTable;
  }
}
