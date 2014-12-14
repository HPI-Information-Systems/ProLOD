package de.hpi.fgis.ldp.server.persistency.loading.impl.meta;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.client.util.StringUtil;
import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * repository of meta information in a DB2 database
 * 
 * @author ziawasch.abedjan
 */
public class MyMetaLoader extends LoaderBase implements IMetaLoader {
  private final Log logger;

  private final ResourceReader resourceReader = new ResourceReader();

  private final int fetchRowSize;

  @Inject
  protected MyMetaLoader(@Named("db.fetchRowSize") int fetchRowSize, Log logger) {
    this.fetchRowSize = fetchRowSize;
    this.logger = logger;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.impl.meta.IMetaLoader#
   * fillSubjectNameMap(java.util.Map)
   */

  @Override
  public void fillTIDNameMap(TIntObjectHashMap<String> allSubjects, String ruleConfiguration,
      DataSource source) {
    if (allSubjects.size() <= 0) {
      return;
    }
    final String allKeys = this.getSQLList(allSubjects.keySet());

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = super.newConnection(source);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?" + ruleConfiguration
                  + "/transactionNames.sql", allKeys);

      rs = stmt.executeQuery(stmtString);
      //
      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        // allSubjects.put(Integer.valueOf(rs.getInt(1)),
        // StringUtil.getInstance().shortenURL(rs.getString(2)));
        allSubjects.put(Integer.valueOf(rs.getInt(1)), rs.getString(2));
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get subject meta data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#fillSubjectNameMap()", e);
      }
    }

  }

  @Override
  public void fillTransactionNameMap(TIntObjectHashMap<String> predicates,
      String ruleConfiguration, DataSource source) {
    if (predicates.size() <= 0) {
      return;
    }
    final String allKeys = this.getSQLList(predicates.keySet());

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = super.newConnection(source);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?" + ruleConfiguration
                  + "/itemNames.sql", allKeys);
      rs = stmt.executeQuery(stmtString);

      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        // predicates.put(Integer.valueOf(rs.getInt(1)),
        // StringUtil.getInstance().shortenURL(rs.getString(2)));
        predicates.put(Integer.valueOf(rs.getInt(1)), rs.getString(2));
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      logger.error("Unable to get predicate meta data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#fillPredicateNameMap()", e);
      }
    }

  }

  @Override
  public Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>> getSynonymDetails(
      int[] predicatePair, int objectID, DataSource source) {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    Cluster cluster = new Cluster(source);
    TIntObjectHashMap<String> firstSubjectList = new TIntObjectHashMap<String>();
    TIntObjectHashMap<String> secondSubjectList = new TIntObjectHashMap<String>();
    String constraintView = " ";
    String constraintCondition = " AND MT.internallink_id = " + objectID;
    if (cluster.getId() >= 0) {
      constraintView = super.getClusterConstraintView(cluster);
      constraintCondition =
          " AND " + super.getClusterConstraintCondition("MT.subject_id")
              + " AND MT.internallink_id = " + objectID;
    }

    try {
      con = super.newConnection(source);
      stmt = con.createStatement();

      final String stmtString1 =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?SubjectPairsNames.sql",
              this.getPartition(cluster, con), constraintView, constraintCondition,
              predicatePair[0]);
      final String stmtString2 =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?SubjectPairsNames.sql",
              this.getPartition(cluster, con), constraintView, constraintCondition,
              predicatePair[1]);
      System.out.println(stmtString1);
      rs = stmt.executeQuery(stmtString1);

      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        firstSubjectList.put(Integer.valueOf(rs.getInt(1)),
            StringUtil.getInstance().shortenURL(rs.getString(2)));
        // predicates.put(Integer.valueOf(rs.getInt(1)),
        // rs.getString(2));
      }

      rs = stmt.executeQuery(stmtString2);

      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        secondSubjectList.put(Integer.valueOf(rs.getInt(1)),
            StringUtil.getInstance().shortenURL(rs.getString(2)));
        // predicates.put(Integer.valueOf(rs.getInt(1)),
        // rs.getString(2));
      }
      rs.close();
      stmt.close();

    } catch (SQLException e) {
      logger.error("Unable to get predicate meta data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#fillPredicateNameMap()", e);
      }
    }
    Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>> resultPair =
        new Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>>(firstSubjectList,
            secondSubjectList);
    return resultPair;
  }
}
