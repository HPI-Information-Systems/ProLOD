package de.hpi.fgis.ldp.server.persistency.loading.impl.itemsets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.ItemSetList;
import de.hpi.fgis.ldp.server.persistency.loading.IItemSetLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;

/**
 * Loads initial item set information from DB2 to enable association rule detection
 * 
 * @author toni.gruetze
 * 
 */
public class MyItemSetLoader extends LoaderBase implements IItemSetLoader {

  private final Log logger;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  // // TODO inject schema via Configuration
  // public ItemSetLoader(String schema) {
  // // TODO inject
  // this.dataSource =
  // DataSourceProvider.getInstance().getConnectionPool(schema);
  // }
  private int fetchRowSize = 100;

  @Inject
  protected MyItemSetLoader(Log logger) {
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
   * @see de.hpi.fgis.ldp.server.persistency.loading.IItemSetLoader#getOneItemSets
   * (de.hpi.fgis.ldp.shared.data.cluster.ICluster, int,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IItemSetList getOneItemSets(final Cluster cluster, String ruleConfiguration,
      final int minCount, final IProgress progress) throws SQLException {
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      // count the predicates with minSupport>=x
      progress.startProgress("counting initial itemsets");

      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource()) + "/?" + ruleConfiguration
                  + "/countItemSets.sql", Integer.valueOf(minCount),
              super.getClusterConstraintView(cluster),
              " AND " + super.getClusterConstraintCondition("MT.subject_id"),
              super.getPartition(cluster, con));

      int predicateCount = 0;
      // execute statement
      // System.err.println(stmtString);

      rs = stmt.executeQuery(stmtString);

      if (rs.next()) {
        predicateCount = rs.getInt(1);
      }
      rs.close();
      if (predicateCount == 0) {
        return null;
      }
      //
      // stmt.close();
      // stmt = con.createStatement();
      progress.stopProgress();

      // load the predicates with minSupport>=x
      progress.startProgress("loading initial itemsets", predicateCount);

      ItemSetList itemSets = new ItemSetList(1, predicateCount);

      // create the statement
      stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource()) + "/?" + ruleConfiguration
                  + "/selectItemSets.sql", Integer.valueOf(minCount),
              super.getClusterConstraintView(cluster),
              " AND " + super.getClusterConstraintCondition("MT.subject_id"),
              super.getPartition(cluster, con));
      // execute statement
      rs = stmt.executeQuery(stmtString);
      // injected
      rs.setFetchSize(this.fetchRowSize);

      int index = 0;
      while (rs.next()) {
        itemSets.setItemSet(index, rs.getInt(1));
        if (index++ % 5000 == 0) {
          progress.continueProgressAt(index);
        }
      }
      rs.close();

      return itemSets;
    } catch (SQLException e) {
      this.logger.error("Unable to get item sets from DB!", e);
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
        this.logger.warn(this.getClass().getName() + "#getOneItemSets()", e);
      }
    }
  }

}
