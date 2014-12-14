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

package de.hpi.fgis.ldp.server.persistency.loading.impl.uniqueness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.IUniquenessLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.UniquenessModel;

/**
 * Loads basic information from DB2 to enable inverse predicate detection
 * 
 * @author toni.gruetze
 * 
 */
public class UniquenessLoader extends LoaderBase implements IUniquenessLoader {

  private final Log logger;

  private final ResourceReader resourceReader = new ResourceReader();

  private int fetchRowSize = 100;

  private DataSource dataSource;

  /**
   * SQL constraint view, to filter subjects by clusterID
   */
  private String constraintViews = " ";
  /**
   * SQL constraint condition, to filter subjects by clusterID
   */
  private String initialConstraintConditions = " ";
  private String additionalConstraintConditions = " ";
  /**
   * partition table to be used
   */
  private String partitionTable = "maintable";
  private Cluster cluster;

  @Inject
  protected UniquenessLoader(Log logger) {
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

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.loading.IUniquenessLoader# getUniqueness(int, double,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public SortedSet<UniquenessModel> getUniqueness(final Cluster cluster, double minSupport,
      IProgress progress) throws SQLException {
    TreeSet<UniquenessModel> result = new TreeSet<UniquenessModel>();

    setConstraints(cluster);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("selecting predicates");
      progress.startProgress("retrieving cluster property information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?countProperties.sql",
              this.getPartition(con), this.constraintViews, this.initialConstraintConditions,
              this.additionalConstraintConditions);
      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      /*
       * 
       * // create the statement String stmtString =
       * this.resourceReader.getUndocumentedStringFromResource (this.getClass() .getPackage(),
       * "sql/?" + "/?getPredicates.sql");
       * 
       * // logger.debug("==============="); // logger.debug(stmtString); //
       * logger.debug("===============");
       */
      /*
       * con = super.newConnection(cluster.getDataSource()); stmt = con.createStatement();
       * 
       * String constraintViewLinks = " "; String initialconstraintConditionLinks = " "; String
       * additionalConstraintConditionLinks = " "; String constraintViewLinkedSubjects = " "; String
       * constraintConditionLinkedSubjects = " "; if(cluster.getId()>=0) { constraintViewLinks =
       * super.getClusterConstraintView(cluster); initialconstraintConditionLinks = " WHERE " +
       * super.getClusterConstraintCondition("subject_id"); additionalConstraintConditionLinks =
       * " AND " + super.getClusterConstraintCondition("subject_id");
       * 
       * 
       * constraintViewLinkedSubjects = super.getClusterConstraintView(cluster) +
       * super.getClusterConstraintView(cluster, "2"); constraintConditionLinkedSubjects = new
       * StringBuilder(" AND ").append (super.getClusterConstraintCondition(
       * "subject_id")).append(" AND " ).append(super.getClusterConstraintCondition("subject_id_2",
       * "2")).toString(); } // create the statement String stmtString =
       * this.resourceReader.getUndocumentedStringFromResource(this .getClass().getPackage(),
       * "sql/?" + "/?countLinkPredicates.sql", constraintViewLinks,
       * initialconstraintConditionLinks);
       */
      rs = stmt.executeQuery(stmtString);

      while (rs.next()) {
        // predicate id's
        final Integer predicateId = rs.getInt(1);
        final String predicateName = rs.getString(2);
        final int predicateCount = rs.getInt(3);

        final UniquenessModel resultItem = new UniquenessModel();

        logger.debug(predicateName);
        resultItem.setPredicateID(predicateId);
        resultItem.setPredicateName(predicateName);
        // TODO
        Integer entityCount = cluster.getSize();
        Integer subjectCount = getSubjectCount(predicateId, progress);
        HashMap<String, Object> uniqueness =
            getUniqueness(entityCount, subjectCount, predicateId, progress);
        // Double density = getDensity(subjectCount, predicateId,
        // progress);

        resultItem.setUniqueness((Double) uniqueness.get("uniqueness"));
        resultItem.setDensity((Double) uniqueness.get("density"));
        resultItem.setValues((Integer) uniqueness.get("values"));
        resultItem.setUniqueValues((Integer) uniqueness.get("uniqueValues"));

        ArrayList<String> examples = new ArrayList<String>();
        examples.add("example");
        resultItem.setExampleSubjects(examples);

        result.add(resultItem);
      }
      progress.stopProgress();

      rs.close();
      stmt.close();

    } catch (SQLException e) {
      logger.error("Unable to get data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getUniqueness()", e);
      }
    }

    // logger.debug(result.size() + " predicates found in cluster");
    return result;
  }

  private Integer getSubjectCount(Integer predicateId, IProgress progress) throws SQLException {
    Integer result = null;
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      progress.startProgress("retrieving subject information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?getSubjects.sql",
              this.getPartition(con), this.constraintViews, predicateId);
      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      while (rs.next()) {
        result = rs.getInt(1);
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get subject data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getSubjectCount()", e);
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#getObjects
   * (java.util.ArrayList, de.hpi.fgis.ldp.shared.data.Datatype,
   * de.hpi.fgis.ldp.shared.data.Pattern, int, int, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  public HashMap<String, Object> getUniqueness(Integer entityCount, Integer subjectCount,
      Integer predicateId, IProgress progress) throws SQLException {
    HashMap<String, Object> resultMap = new HashMap<String, Object>();

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int propertyValues = 0;
    Integer uniqueValues = 0;
    Integer propertyValueCount = 0;

    // Integer values = 0;
    // HashMap<String, Integer> valueCounts = new HashMap<String,
    // Integer>();
    try {
      progress.startProgress("retrieving object information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?getValueDistribution.sql",
              this.getPartition(con), predicateId, this.constraintViews,
              this.initialConstraintConditions, this.additionalConstraintConditions);
      logger.debug("===============");
      logger.debug(stmtString);
      logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      while (rs.next()) {
        final Integer elementCount = rs.getInt(2);
        propertyValues += elementCount;

        propertyValueCount += 1;
        if (elementCount == 1) {
          uniqueValues += 1;
        }

        // final String value = rs.getString(2);
        // valueCounts.put(value, elementCount);
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get object data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getUniqueness()", e);
      }
    }

    // TODO test sizes (if > )
    // happens if more than one value per property?
    Double uniqueness = Double.valueOf(uniqueValues) / Double.valueOf(propertyValueCount);
    // percentage.setElement(rowIndex, Double.valueOf((100D *
    // count.getElement(rowIndex).intValue()) / cntSum));

    // Double density = Double.valueOf(subjectCount) /
    // (Double.valueOf(subjectCount) +
    // Double.valueOf(entityCount-subjectCount)) ;
    Double density = Double.valueOf(subjectCount) / Double.valueOf(entityCount);

    resultMap.put("uniqueness", uniqueness);
    resultMap.put("density", density);
    resultMap.put("values", propertyValues);
    resultMap.put("uniqueValues", uniqueValues);

    return resultMap;
  }

  public void setConstraints(Cluster cluster) throws SQLException {
    this.dataSource = cluster.getDataSource();

    this.cluster = cluster;
    if (cluster.getId() < 0) {
      this.constraintViews = " ";
      this.initialConstraintConditions = "";
      this.additionalConstraintConditions = " ";
    } else {
      this.constraintViews = super.getClusterConstraintView(cluster);
      final String constraintConditions = super.getClusterConstraintCondition("subject_id");
      this.initialConstraintConditions = " WHERE " + constraintConditions;
      this.additionalConstraintConditions = " AND " + constraintConditions;
    }

    this.partitionTable = null;
  }

  private String getPartition(Connection con) {
    if (this.partitionTable == null) {
      this.partitionTable = super.getPartition(this.cluster, con);
    }
    return this.partitionTable;
  }

}
