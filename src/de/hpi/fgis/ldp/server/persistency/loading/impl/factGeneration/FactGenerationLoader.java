package de.hpi.fgis.ldp.server.persistency.loading.impl.factGeneration;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.IEntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.array.IntArray2D;
import de.hpi.fgis.ldp.server.datastructures.impl.EntitySchemaList;
import de.hpi.fgis.ldp.server.persistency.loading.IFactGenerationLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

public class FactGenerationLoader extends LoaderBase implements IFactGenerationLoader {

  private final Log logger;
  //
  // private IDataSource dataSource;

  //
  private final ResourceReader resourceReader = new ResourceReader();

  private int fetchRowSize = 100;
  private static int basketCount;

  @Inject
  protected FactGenerationLoader(Log logger) {
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
   * @param missingObject
   * @param existingObject
   * 
   * 
   * @param condition the condition to be used
   * @param cluster the cluster to get entities for
   * @param progress the progress feedback instance
   * @return all entity of the database
   */
  private IEntitySchemaList getEntities(final String constraintView,
      final String constraintCondition, int existingObject, int missingObject,
      final Cluster cluster, final IProgress progress2) throws SQLException {

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
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?countViolatingSubjects.sql", this.getPartition(cluster, con),
              constraintView, constraintCondition, existingObject, missingObject);

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
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?selectViolatingEntities.sql", this.getPartition(cluster, con),
              constraintView, constraintCondition, existingObject, missingObject);

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

  @Override
  public IEntitySchemaList getEntityList(Cluster cluster, int existingObject, int missingObject,
      IProgress progress) throws SQLException {
    String constraintView = " ";
    String constraintCondition = " ";
    if (cluster.getId() >= 0) {
      constraintView = super.getClusterConstraintView(cluster);
      constraintCondition = " AND " + super.getClusterConstraintCondition("MT.subject_id");

    }

    final IEntitySchemaList entities =
        this.getEntities(constraintView, constraintCondition, existingObject, missingObject,
            cluster, progress);

    return entities;

    // constraintCondition =
    // "MT.subject_id NOT IN (Select MT.subject_id FROM "+getPartition(cluster,
    // con)+" MT, ";

    // statementBuffer.append(triples+".SUBJECT NOT IN ( SELECT "+triples+".SUBJECT FROM ")
    // .append(triples);
    // if (concept != null) {
    // statementBuffer.append(", ").append(ontologyMappingTable);
    // statementBuffer.append(" where "+triples+".SUBJECT =");
    // statementBuffer.append(ontologyMappingTable);
    // statementBuffer.append(".SUBJECT AND ");
    // statementBuffer.append(ontologyMappingTable);
    // statementBuffer.append(".TYPE = '");
    // statementBuffer.append(concept).append("' AND ");
    //
    // } else {
    // statementBuffer.append(" where ");
    // }
    // statementBuffer.append(triples+"." + valuePart + " = ?)");
    // System.out.println(statementBuffer.toString());
    // subjectRetrieveStatement = dbc.getPreparedStatement(statementBuffer
    // .toString());

  }

  @Override
  public IEntitySchemaList getEntityList(final DataSource source, int existingObject,
      int missingObject, IProgress progress) throws SQLException {
    return this.getEntityList(new Cluster(source), existingObject, missingObject, progress);
  }

  @Override
  public int[] getDesignators(Cluster cluster, int object, IProgress progress) throws SQLException {
    String constraintView = " ";
    String constraintCondition = " ";
    if (cluster.getId() >= 0) {
      constraintView = super.getClusterConstraintView(cluster);
      constraintCondition =
          " AND " + super.getClusterConstraintCondition("MT.subject_id")
              + " AND MT.internallink_id = " + object;
    }

    final int[] desginators =
        this.getDesignators(constraintView, constraintCondition, object, cluster, progress);

    return desginators;
  }

  @Override
  public int[] getDesignators(DataSource source, int object, IProgress progress)
      throws SQLException {
    return this.getDesignators(new Cluster(source), object, progress);
  }

  private int[] getDesignators(String constraintView, String constraintCondition, int object,
      Cluster cluster, IProgress progress) throws SQLException {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    TIntSet predicates = new TIntHashSet();
    progress.startProgress("Loading desginators ...", 100);

    try {

      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader
              .getUndocumentedStringFromResource(this.getClass().getPackage(),
                  "sql/?" + super.getDataSourceType(cluster.getDataSource())
                      + "/?selectPredicates.sql", this.getPartition(cluster, con), constraintView,
                  constraintCondition, object);

      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      // count the subjects/entities
      rs = stmt.executeQuery(stmtString);

      basketCount = 0;

      while (rs.next()) {
        predicates.add(rs.getInt(1));
      }
    } catch (SQLException e) {
      this.logger.error("Unable to get predicates for an object from DB!", e);
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
        this.logger.warn(this.getClass().getName() + "#getPredicatesForAnObject()", e);
      }
    }
    return predicates.toArray();

  }
}
