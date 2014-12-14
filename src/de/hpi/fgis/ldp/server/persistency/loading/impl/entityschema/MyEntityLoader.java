package de.hpi.fgis.ldp.server.persistency.loading.impl.entityschema;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.array.IntArray2D;
import de.hpi.fgis.ldp.server.datastructures.impl.EntitySchemaList;
import de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * Loads basic information from DB2 to enable association rule detection
 * 
 * @author toni.gruetze
 * 
 */
public class MyEntityLoader extends LoaderBase implements IEntitySchemaLoader {

  private final Log logger;
  //
  // private IDataSource dataSource;

  //
  private final ResourceReader resourceReader = new ResourceReader();

  private int fetchRowSize = 100;
  private static int basketCount;

  @Inject
  protected MyEntityLoader(Log logger) {
    this.logger = logger;
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

  /**
   * Get all entities of the database.<br>
   * A entity is defined as a sorted (ascending) set of predicate id's.<br>
   * A entity list is a sorted (ascending in subject id's) set of entities
   * 
   * 
   * @param condition the condition to be used
   * @param cluster the cluster to get entities for
   * @param progress the progress feedback instance
   * @return all entity of the database
   */
  private IEntitySchemaList getEntities(String ruleConfiguration, final String constraintView,
      final String constraintCondition, final Cluster cluster, final IProgress progress2)
      throws SQLException {

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    IEntitySchemaList entities = null;

    IProgress currentSubProgress = null;
    progress2.startProgress("Loading Entities ...", 100);

    try {
      currentSubProgress = progress2.continueWithSubProgress(4);
      currentSubProgress.startProgress("counting subjects");

      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource()) + "/?" + ruleConfiguration
                  + "/countBaskets.sql", this.getPartition(cluster, con), constraintView,
              constraintCondition);

      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      basketCount = 0;

      if (rs.next()) {
        basketCount = rs.getInt(1);
      }
      rs.close();

      //
      // stmt.close();
      // stmt = con.createStatement();
      currentSubProgress.stopProgress();
      progress2.continueProgressAt(4);

      // count the triplets
      currentSubProgress = progress2.continueWithSubProgress(48);
      currentSubProgress.startProgress("counting triplets");

      stmt.close();
      stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      // create the statement
      stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource()) + "/?" + ruleConfiguration
                  + "/selectTriplets.sql", this.getPartition(cluster, con), constraintView,
              constraintCondition);

      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      // select the subjects/entities
      rs = stmt.executeQuery(stmtString);

      rs.last();
      final int rowCount = rs.getRow();
      rs.beforeFirst();

      currentSubProgress.stopProgress();
      progress2.continueProgressAt(52);

      if (basketCount <= 0 || rowCount <= 0) {
        // no matching entities found
        logger.debug(basketCount + "Basketcount");
        logger.debug(rowCount + "rowCount");
        return null;
      }
      logger.info("Number of Baskets:" + basketCount);
      // select the triplets
      currentSubProgress = progress2.continueWithSubProgress(47);
      currentSubProgress.startProgress("loading triplets", basketCount);
      // create a array for the predicates of !all! triplets
      // (grouped
      // by subject id and sorted within one group)
      final int entityColumn = 0;
      final int indexColumn = 1;
      int[] predicateItems = new int[rowCount];
      // create a mapper which stores the start index of the
      // predicates of a entity in the predicate item list
      IntArray2D itemMapper = new IntArray2D(basketCount, 2, Integer.MIN_VALUE);

      int lastSubjectID = Integer.MIN_VALUE;
      int rowIndex = 0;
      int subjectIndex = 0;

      // injected
      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final int currentSubjectID = rs.getInt(1);
        final int currentPredicateID = rs.getInt(2);

        // new subject/entity (order by subjectID)
        if (currentSubjectID != lastSubjectID) {
          lastSubjectID = currentSubjectID;
          // add the subject id and start index of the
          // predicates
          // of the current entity instance (in the predicate
          // item
          // list) to the item mapper
          itemMapper.setValue(subjectIndex, entityColumn, currentSubjectID);
          itemMapper.setValue(subjectIndex, indexColumn, rowIndex);

          if (subjectIndex % 75000 == 0) {
            currentSubProgress.continueProgressAt(subjectIndex);
          }

          subjectIndex++;
        }

        // add predicate item (of the subject/entity with the
        // subjectID = lastSubjectID)
        predicateItems[rowIndex] = currentPredicateID;

        rowIndex++;

      }
      // remove unnecessary space at the end of the predicates
      predicateItems = Arrays.copyOf(predicateItems, rowIndex);
      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
      this.logger.debug("#rows: " + rowIndex);

      progress2.continueProgressAt(99);
      currentSubProgress = progress2.continueWithSubProgress(1);
      currentSubProgress.startProgress("creating entity entries");

      // adds the predicate items and their subject id's to a
      // entity
      // list and sorts the predicate items per subject/entity
      // + store the entity list in the loader
      // cache
      entities =
          EntitySchemaList.Factory.getInstance().newInstance(itemMapper, predicateItems,
              entityColumn, indexColumn);

      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get entity data from DB!", e);
      throw e;
    } finally {
      progress2.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getAllEntities()", e);
      }
    }
    return entities;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader#getEntityList
   * (de.hpi.fgis.ldp.shared.data.DataSource, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IEntitySchemaList getEntityList(final DataSource source, String ruleConfiguration,
      IProgress progress) throws SQLException {
    return this.getEntityList(new Cluster(source), ruleConfiguration, progress);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader#getEntityList
   * (de.hpi.fgis.ldp.shared.data.DataSource, int, double,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IEntitySchemaList getEntityList(final DataSource source, String ruleConfiguration,
      int minPropOccurance, double maxPropFrequency, IProgress progress) throws SQLException {
    boolean first = true;
    final StringBuilder constraintView =
        new StringBuilder(", (SELECT ID as constraint_predicate_id FROM predicatetable ");
    if (minPropOccurance > 0) {
      if (first) {
        constraintView.append(" WHERE ");
      } else {
        constraintView.append(" AND ");
      }
      constraintView.append(" CNT >= ").append(minPropOccurance);
      first = false;
    }

    if (minPropOccurance > maxPropFrequency) {
      if (first) {
        constraintView.append(" WHERE ");
      } else {
        constraintView.append(" AND ");
      }
      constraintView.append(" CNT <= (SELECT COUNT(*)*").append(maxPropFrequency)
          .append(" FROM MAINTABLE)");
      first = false;
    }
    constraintView.append(" ) predicate_constraint ");

    final String constraintCondition =
        " AND predicate_id = predicate_constraint.constraint_predicate_id ";

    final IEntitySchemaList entities =
        this.getEntities(ruleConfiguration, constraintView.toString(), constraintCondition,
            new Cluster(source), progress);

    return entities;
    // return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IEntitySchemaLoader#getEntityList
   * (de.hpi.fgis.ldp.shared.data.cluster.ICluster, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IEntitySchemaList getEntityList(Cluster cluster, String ruleConfiguration,
      IProgress progress) throws SQLException {
    // create the subject/triplet condition
    String constraintView = " ";
    String constraintCondition = " ";
    if (cluster.getId() >= 0) {
      constraintView = super.getClusterConstraintView(cluster);
      constraintCondition = " AND " + super.getClusterConstraintCondition("MT.subject_id");
    }

    final IEntitySchemaList entities =
        this.getEntities(ruleConfiguration, constraintView, constraintCondition, cluster, progress);

    return entities;
  }

  @Override
  public Map<Integer, String> getClasses(DataSource source, IProgress progress) throws SQLException {
    Map<Integer, String> classes = new HashMap<Integer, String>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    progress.startProgress("Loading classes ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting classes and labels");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getClasses.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final String classUrl = rs.getString(1);
        final Integer classId = rs.getInt(2);
        classes.put(classId, classUrl);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return classes;
  }

  @Override
  public Map<Integer, String> getClasses(Cluster cluster, IProgress progress) throws SQLException {
    return getClasses(cluster.getDataSource(), progress);
  }

  public static int getBasketCount() {
    return basketCount;
  }

  @Override
  public Map<Integer, Integer> getClassHierarchy(DataSource source, IProgress progress)
      throws SQLException {
    Map<Integer, Integer> hierarchy = new HashMap<Integer, Integer>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    progress.startProgress("Loading classes ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting classes and labels");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getClassHierarchy.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer subClassId = rs.getInt(1);
        final Integer classId = rs.getInt(2);
        hierarchy.put(subClassId, classId);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return hierarchy;
  }

  @Override
  public Integer getRootClass(DataSource source, IProgress progress) throws SQLException {
    Integer rootClass = null;
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    progress.startProgress("Loading root class (owl:Thing) ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting root class (owl:Thing)");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getRootClass.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        rootClass = rs.getInt(1);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return rootClass;
  }

  @Override
  public Map<Integer, ArrayList<Integer>> getEntityClassList(DataSource source, IProgress progress,
      Map<Integer, String> classes) throws SQLException {
    Map<Integer, ArrayList<Integer>> entityClasses = new HashMap<Integer, ArrayList<Integer>>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    progress.startProgress("Loading root class (owl:Thing) ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting root class (owl:Thing)");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getEntityClasses.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        Integer entityId = rs.getInt(1);
        Integer classId = rs.getInt(2);
        ArrayList<Integer> classList = new ArrayList<Integer>();
        if (!classes.get(classId).equals("http://www.w3.org/2002/07/owl#Thing")) {
          if (entityClasses.containsKey(entityId)) {
            classList = entityClasses.get(entityId);
          }
          classList.add(classId);
          entityClasses.put(entityId, classList);
        }
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return entityClasses;
  }

  @Override
  public TIntIntHashMap getPredicateDomain(DataSource source, IProgress progress)
      throws SQLException {
    TIntIntHashMap predicateToDomainMap = new TIntIntHashMap();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Loading classes ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting predicates and domains");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getPredicateDomains.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer predicate = rs.getInt(1);
        final Integer classId = rs.getInt(2);
        predicateToDomainMap.put(predicate, classId);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return predicateToDomainMap;
  }

  @Override
  public TIntIntHashMap getInstanceCounts(DataSource source, IProgress progress)
      throws SQLException {
    TIntIntHashMap predicateToDomainMap = new TIntIntHashMap();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Count Instances per Class ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting classes and instance counts");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getInstanceCounts.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer classId = rs.getInt(1);
        final Integer count = rs.getInt(2);
        predicateToDomainMap.put(classId, count);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return predicateToDomainMap;
  }

  @Override
  public TIntIntHashMap getPredicateCounts(DataSource source, IProgress progress,
      TIntIntHashMap predicateToSourceClass) throws SQLException {
    TIntIntHashMap predicateCounts = new TIntIntHashMap();
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Count Instances per Class ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting classes and instance counts");

      con = super.newConnection(source);

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getPredicateCounts.sql",
              this.getPartition(new Cluster(source), con));

      TIntIntHashMap subjectToPredicateMapping =
          getPredicateIDMappings(source, currentSubProgress, "sp");
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");

      stmt = con.prepareStatement(stmtString);
      for (int predicate : predicateToSourceClass.keys()) {
        stmt.setInt(1, subjectToPredicateMapping.get(predicate));
        stmt.setInt(2, predicateToSourceClass.get(predicate));

        rs = stmt.executeQuery();
        if (rs.next()) {
          final Integer count = rs.getInt(1);
          predicateCounts.put(predicate, count);
        }
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return predicateCounts;
  }

  @Override
  public TIntIntHashMap getClusterIdMappings(DataSource source, IProgress progress, String mapping)
      throws SQLException {
    TIntIntHashMap subjectToClusterMap = new TIntIntHashMap();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Count Instances per Class ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting classes and instance counts");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getClusterIDMappings.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer subject_id = rs.getInt(1);
        final Integer cluster_id = rs.getInt(2);
        if (mapping.equals("sc")) {

          subjectToClusterMap.put(subject_id, cluster_id);
        } else {
          subjectToClusterMap.put(cluster_id, subject_id);
        }
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return subjectToClusterMap;
  }

  @Override
  public TIntIntHashMap getPredicateIDMappings(DataSource source, IProgress progress, String mapping)
      throws SQLException {
    TIntIntHashMap subjectToClusterMap = new TIntIntHashMap();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Count Instances per Class ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting predicate mappings");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getPredicateIDMappings.sql",
              this.getPartition(new Cluster(source), con));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer subject_id = rs.getInt(1);
        final Integer predicate_id = rs.getInt(2);
        if (mapping.equals("sp")) {

          subjectToClusterMap.put(subject_id, predicate_id);
        } else {
          subjectToClusterMap.put(predicate_id, subject_id);
        }
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
      throw e;
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getClasses()", e);
      }
    }
    return subjectToClusterMap;
  }

  @Override
  public TIntObjectHashMap<String> getRemovedProperties(DataSource source, IProgress progress,
      int s_id) {
    TIntObjectHashMap<String> emovedProperties = new TIntObjectHashMap<String>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Loading removed Properties ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("getting removed properties for curent class");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getRemovedProperties.sql", s_id);

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final Integer predicate_as_s_id = rs.getInt(1);
        final String predicate = rs.getString(2);
        emovedProperties.put(predicate_as_s_id, predicate);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getRemovedProperties()", e);
      }
    }
    return emovedProperties;
  }

  @Override
  public Map<String, String> getAddedProperties(DataSource source, IProgress progress, int s_id) {
    Map<String, String> addedProperties = new HashMap<String, String>();
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    IProgress currentSubProgress = null;
    // progress.startProgress("Loading classes ...", 100);

    try {
      currentSubProgress = progress.continueWithSubProgress(4);
      currentSubProgress.startProgress("get added properties for the current class");

      con = super.newConnection(source);
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?getAddedProperties.sql", s_id);

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);
      while (rs.next()) {
        final String predicate = rs.getString(1);
        final String sourceClass = rs.getString(2);
        addedProperties.put(predicate, sourceClass);
      }

      rs.close();
      stmt.close();
      currentSubProgress.stopProgress();
    } catch (SQLException e) {
      this.logger.error("Unable to get class data from DB!", e);
    } finally {
      // progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#getAddedPropeties()", e);
      }
    }
    return addedProperties;
  }

}
