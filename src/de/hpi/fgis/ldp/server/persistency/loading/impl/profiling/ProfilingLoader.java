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

package de.hpi.fgis.ldp.server.persistency.loading.impl.profiling;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.NormalizedPattern;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;
import de.hpi.fgis.ldp.shared.data.table.impl.DataColumn;
import de.hpi.fgis.ldp.shared.data.table.impl.DataTable;

/**
 * Class to Analyze a set of subjects and create object-based statistics.
 * 
 */
public class ProfilingLoader extends LoaderBase implements IProfilingLoader {
  private final Log logger;
  private DataSource dataSource;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private int fetchRowSize = 100;

  @Inject
  protected ProfilingLoader(Log logger) {
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

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#
   * getLinkLiteralRatio(de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getLinkLiteralRatio(final IProgress progress) throws SQLException {
    int countInternalLinks = 0;
    int countExternalLinks = 0;
    int countLiterals = 0;
    int countAll = 0;

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      progress.startProgress("retrieving link literal ratio information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?linkLiteralRatio.sql",
              this.getPartition(con), this.constraintViews, this.initialConstraintConditions,
              this.additionalConstraintConditions);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);
      rs.setFetchSize(1);

      if (rs.next()) {
        countExternalLinks = rs.getInt(3);
        countInternalLinks = rs.getInt(2);
        countAll = rs.getInt(1);
        countLiterals = countAll - countInternalLinks - countExternalLinks;
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get link literal ration data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getLinkLiteralRatio()", e);
      }
    }

    // create resultTable
    DataColumn<String> key = new DataColumn<String>("Key", true);
    DataColumn<Integer> value = new DataColumn<Integer>("Value", true);

    DataTable resultTable = new DataTable(key);
    resultTable.addColumn(value);

    key.setElement(0, "Internal Links");
    value.setElement(0, Integer.valueOf(countInternalLinks));

    key.setElement(1, "External Links");
    value.setElement(1, Integer.valueOf(countExternalLinks));

    key.setElement(2, "Literals");
    value.setElement(2, Integer.valueOf(countLiterals));

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader# getClusterProperties(int, int,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getClusterProperties(int from, int to, final IProgress progress)
      throws SQLException {

    // create resultTable
    DataColumn<Predicate> predicate = new DataColumn<Predicate>("Predicate", true);
    DataColumn<Integer> count = new DataColumn<Integer>("Count", true);
    DataColumn<Double> percentage = new DataColumn<Double>("%", true);

    DataTable resultTable = new DataTable(predicate);
    resultTable.addColumn(count);
    resultTable.addColumn(percentage);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cntSum = 0;
    try {
      progress.startProgress("retrieving cluster property information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?countProperties.sql",
              this.getPartition(con), this.constraintViews, this.initialConstraintConditions,
              this.additionalConstraintConditions);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      // injected
      rs.setFetchSize(this.fetchRowSize);

      int row = 0;
      while (rs.next()) {

        // count
        final int elementCount = rs.getInt(3);
        cntSum += elementCount;

        if (from <= row && row <= to) {

          // property id&name
          // TODO remove replace all
          predicate.setElement(
              row - from,
              new Predicate(rs.getInt(1), rs.getString(2).replaceAll("<", "&lt;")
                  .replaceAll(">", "&gt;")));

          // count
          count.setElement(row - from, Integer.valueOf(elementCount));
        }
        row++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get cluster properties from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getClusterProperties()", e);
      }
    }

    final int rowCount = resultTable.getRowCount();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      // set the overall percentage
      percentage.setElement(rowIndex,
          Double.valueOf((100D * count.getElement(rowIndex).intValue()) / cntSum));
    }

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#getDatatypeRatio
   * (java.util.List, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getDatatypeRatio(final ArrayList<Predicate> predicates, final IProgress progress)
      throws SQLException {

    // create resultTable
    DataColumn<Datatype> datatype = new DataColumn<Datatype>("Datatype", true);
    DataColumn<Integer> count = new DataColumn<Integer>("Count", true);
    DataColumn<Double> percentage = new DataColumn<Double>("%", true);
    DataColumn<Double> min = new DataColumn<Double>("Minimum", true);
    DataColumn<Double> max = new DataColumn<Double>("Maximum", true);
    DataColumn<Double> avg = new DataColumn<Double>("Average", true);
    DataColumn<Double> stddev = new DataColumn<Double>("Standard Deviation", true);

    DataTable resultTable = new DataTable(datatype);
    resultTable.addColumn(count);
    resultTable.addColumn(percentage);
    resultTable.addColumn(min);
    resultTable.addColumn(max);
    resultTable.addColumn(avg);
    resultTable.addColumn(stddev);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cntSum = 0;
    try {
      progress.startProgress("retrieving datatype information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final TIntSet predicateIDs = new TIntHashSet();
      for (final Predicate predicate : predicates) {
        predicateIDs.add(predicate.getId());
      }

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?datatypeDistribution.sql",
              this.getPartition(con), super.getSQLList(predicateIDs), this.constraintViews,
              this.initialConstraintConditions, this.additionalConstraintConditions);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      // injected
      rs.setFetchSize(this.fetchRowSize);

      int row = 0;
      while (rs.next()) {

        // count
        final int elementCount = rs.getInt(2);
        cntSum += elementCount;

        // datatype
        datatype.setElement(row, Datatype.getDatatype(rs.getInt(1)));

        // count
        count.setElement(row, Integer.valueOf(elementCount));

        // Minimum
        min.setElement(row, Double.valueOf(rs.getDouble(3)));

        // Maximun
        max.setElement(row, Double.valueOf(rs.getDouble(4)));

        // Average
        avg.setElement(row, Double.valueOf(rs.getDouble(5)));

        // Standard Devation
        stddev.setElement(row, Double.valueOf(rs.getDouble(6)));
        row++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get data type ratio data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getDatatypeRatio()", e);
      }
    }

    final int rowCount = resultTable.getRowCount();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      // set the overall percentage
      percentage.setElement(rowIndex,
          Double.valueOf((100D * count.getElement(rowIndex).intValue()) / cntSum));
    }

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#
   * getNormalizedPattern(java.util.ArrayList, int, int,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getNormalizedPattern(ArrayList<Predicate> predicates, int from, int to,
      IProgress progress) throws SQLException {
    // create resultTable
    DataColumn<NormalizedPattern> pattern =
        new DataColumn<NormalizedPattern>("Normalized Pattern", true);
    DataColumn<Integer> count = new DataColumn<Integer>("Count", true);
    DataColumn<Double> percentage = new DataColumn<Double>("%", true);

    DataTable resultTable = new DataTable(pattern);
    resultTable.addColumn(count);
    resultTable.addColumn(percentage);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cntSum = 0;
    try {
      progress.startProgress("retrieving normalized pattern information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final TIntSet predicateIDs = new TIntHashSet();
      for (final Predicate predicate : predicates) {
        predicateIDs.add(predicate.getId());
      }

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource)
                  + "/?normalizedPatternDistribution.sql", this.getPartition(con),
              super.getSQLList(predicateIDs), this.constraintViews,
              this.initialConstraintConditions, this.additionalConstraintConditions);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);
      // injected
      rs.setFetchSize(this.fetchRowSize);

      int row = 0;
      while (rs.next()) {

        // count
        final int elementCount = rs.getInt(3);
        cntSum += elementCount;

        if (from <= row && row <= to) {
          // Normalized Pattern
          pattern.setElement(row - from, new NormalizedPattern(rs.getInt(1), rs.getString(2)));

          // count
          count.setElement(row - from, Integer.valueOf(elementCount));
        }
        row++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get normalized pattern data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getNormalizedPattern()", e);
      }
    }

    final int rowCount = resultTable.getRowCount();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      // set the overall percentage
      percentage.setElement(rowIndex,
          Double.valueOf((100D * count.getElement(rowIndex).intValue()) / cntSum));
    }

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#getPattern
   * (java.util.ArrayList, de.hpi.fgis.ldp.shared.data.Datatype,
   * de.hpi.fgis.ldp.shared.data.NormalizedPattern, int, int,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getPattern(ArrayList<Predicate> predicates, Datatype datatype,
      NormalizedPattern normalizedPattern, int from, int to, IProgress progress)
      throws SQLException {
    // create resultTable
    DataColumn<Pattern> pattern = new DataColumn<Pattern>("Pattern", true);
    DataColumn<Integer> count = new DataColumn<Integer>("Count", true);
    DataColumn<Double> percentage = new DataColumn<Double>("%", true);

    DataTable resultTable = new DataTable(pattern);
    resultTable.addColumn(count);
    resultTable.addColumn(percentage);

    // checks whether a normalizedPattern is set
    String initialCondition = this.initialConstraintConditions;
    String additionalCondition = this.additionalConstraintConditions;
    if (normalizedPattern != null) {
      initialCondition =
          " WHERE normalizedpattern_id = " + normalizedPattern.getId()
              + this.additionalConstraintConditions;
      additionalCondition += " AND normalizedpattern_id = " + normalizedPattern.getId();
    }

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cntSum = 0;
    try {
      progress.startProgress("retrieving pattern information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final TIntSet predicateIDs = new TIntHashSet();
      for (final Predicate predicate : predicates) {
        predicateIDs.add(predicate.getId());
      }

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?patternDistribution.sql",
              this.getPartition(con), Integer.valueOf(datatype.getId()),
              super.getSQLList(predicateIDs), this.constraintViews, initialCondition,
              additionalCondition);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      // injected
      rs.setFetchSize(this.fetchRowSize);

      int row = 0;
      while (rs.next()) {

        // count
        final int elementCount = rs.getInt(3);
        cntSum += elementCount;

        if (from <= row && row <= to) {
          String patternString = rs.getString(2);
          // TODO replace constants???
          if (Datatype.INTEGER.equals(datatype) || Datatype.DECIMAL.equals(datatype)) {
            // FIXME do this in preprocessing
            // Pattern
            final int patternLength = patternString.length();
            StringBuilder sb = new StringBuilder("1");
            for (int i = 0; i < patternLength - 1; i++) {
              sb.append('0');
            }
            patternString = sb.toString() + " ... " + sb.append('0').toString();
          }
          pattern.setElement(row - from, new Pattern(rs.getInt(1), patternString));

          // count
          count.setElement(row - from, Integer.valueOf(elementCount));
        }
        row++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get pattern data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getPattern()", e);
      }
    }

    final int rowCount = resultTable.getRowCount();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      // set the overall percentage
      percentage.setElement(rowIndex,
          Double.valueOf((100D * count.getElement(rowIndex).intValue()) / cntSum));
    }

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#getObjects
   * (java.util.ArrayList, de.hpi.fgis.ldp.shared.data.Datatype,
   * de.hpi.fgis.ldp.shared.data.Pattern, int, int, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IDataTable getObjects(ArrayList<Predicate> predicates, Datatype datatype, Pattern pattern,
      int from, int to, IProgress progress) throws SQLException {
    // create resultTable
    DataColumn<String> value = new DataColumn<String>("Value", true);
    DataColumn<Integer> count = new DataColumn<Integer>("Count", true);
    DataColumn<Double> percentage = new DataColumn<Double>("%", true);
    DataTable resultTable = new DataTable(value);
    resultTable.addColumn(count);
    resultTable.addColumn(percentage);

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cntSum = 0;
    try {
      progress.startProgress("retrieving object information");
      con = super.newConnection(this.dataSource);
      stmt = con.createStatement();

      final TIntSet predicateIDs = new TIntHashSet();;
      for (final Predicate predicate : predicates) {
        predicateIDs.add(predicate.getId());
      }

      final String patternCondition = (pattern == null) ? " IS NULL " : " = " + pattern.getId();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.dataSource) + "/?valueDistribution.sql",
              this.getPartition(con), super.getSQLList(predicateIDs),
              Integer.valueOf(datatype.getId()), patternCondition, this.constraintViews,
              this.initialConstraintConditions, this.additionalConstraintConditions);
      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      rs = stmt.executeQuery(stmtString);

      // injected
      rs.setFetchSize(this.fetchRowSize);

      int row = 0;
      while (rs.next()) {

        // count
        final int elementCount = rs.getInt(2);
        cntSum += elementCount;

        if (from <= row && row <= to) {
          // value
          value.setElement(row - from, rs.getString(1));

          // count
          count.setElement(row - from, Integer.valueOf(elementCount));
        }
        row++;
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
        logger.warn(this.getClass().getName() + "#getObjects()", e);
      }
    }

    final int rowCount = resultTable.getRowCount();
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      // set the overall percentage
      percentage.setElement(rowIndex,
          Double.valueOf((100D * count.getElement(rowIndex).intValue()) / cntSum));
    }

    return resultTable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.IProfilingLoader#setConstraints
   * (de.hpi.fgis.ldp.shared.data.Cluster)
   */
  @Override
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
