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

package de.hpi.fgis.ldp.server.persistency.loading.impl.inversepredicates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.IInversePredicateLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.InversePredicateModel;

/**
 * Loads basic information from DB2 to enable inverse predicate detection
 * 
 * @author toni.gruetze
 * 
 */
public class InversePredicateLoader extends LoaderBase implements IInversePredicateLoader {

  private final Log logger;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  // // TODO inject schema via Configuration
  // public InversePredicateLoader(String schema) {
  // // TODO inject
  // this.dataSource =
  // DataSourceProvider.getInstance().getConnectionPool(schema);
  // }

  private int fetchRowSize = 100;

  @Inject
  protected InversePredicateLoader(Log logger) {
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
   * @seede.hpi.fgis.ldp.server.persistency.loading.IInversePredicateLoader#
   * getInversePredicates(int, double, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public SortedSet<InversePredicateModel> getInversePredicates(final Cluster cluster,
      double minSupport, IProgress progress) throws SQLException {
    TreeSet<InversePredicateModel> result = new TreeSet<InversePredicateModel>();

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
      progress.startProgress("counting Links");
      con = super.newConnection(cluster.getDataSource());
      stmt = con.createStatement();

      String constraintViewLinks = " ";
      String initialconstraintConditionLinks = " ";
      String additionalConstraintConditionLinks = " ";
      String constraintViewLinkedSubjects = " ";
      String constraintConditionLinkedSubjects = " ";
      if (cluster.getId() >= 0) {
        constraintViewLinks = super.getClusterConstraintView(cluster);
        initialconstraintConditionLinks =
            " WHERE " + super.getClusterConstraintCondition("subject_id");
        additionalConstraintConditionLinks =
            " AND " + super.getClusterConstraintCondition("subject_id");

        constraintViewLinkedSubjects =
            super.getClusterConstraintView(cluster) + super.getClusterConstraintView(cluster, "2");
        constraintConditionLinkedSubjects =
            new StringBuilder(" AND ").append(super.getClusterConstraintCondition("subject_id"))
                .append(" AND ").append(super.getClusterConstraintCondition("subject_id_2", "2"))
                .toString();
      }
      // create the statement
      String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?countLinkPredicates.sql", constraintViewLinks,
              initialconstraintConditionLinks);

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      // count all internal links in the cluster
      rs = stmt.executeQuery(stmtString);

      double linkCount = 0;

      if (rs.next()) {
        linkCount = rs.getInt(1);
      }
      rs.close();
      // TODO check does it still work?
      // stmt.close();
      // stmt = con.createStatement();

      progress.stopProgress();

      // int index = 0;
      progress.startProgress("selecting inverse predicates");

      // create the statement
      stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?selectInversePredicates.sql", constraintViewLinks,
              constraintViewLinkedSubjects, initialconstraintConditionLinks,
              additionalConstraintConditionLinks, constraintConditionLinkedSubjects,
              Double.valueOf(linkCount * minSupport));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      // select the inverse predicates
      rs = stmt.executeQuery(stmtString);
      // injected
      rs.setFetchSize(this.fetchRowSize);

      // -- %1$s constraint view (linked subjects)
      // -- %2$s constraint condition (linked subjects)
      // -- %3$s number of examples

      // create the statement
      stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(cluster.getDataSource())
                  + "/?selectExampleSubjects.sql", constraintViewLinkedSubjects,
              constraintConditionLinkedSubjects, Integer.valueOf(15));

      // logger.debug("===============");
      // logger.debug(stmtString);
      // logger.debug("===============");
      // select the inverse predicates
      PreparedStatement ps = con.prepareStatement(stmtString);

      while (rs.next()) {
        // predicate id's
        final int idX = rs.getInt(2);
        final int idY = rs.getInt(4);
        // count of internal links with the predicates
        final double x = rs.getInt(6);
        final double y = rs.getInt(7);
        // count of the inverse internal links with the two predicates
        // (a-[x]->b and b-[y]->a)
        final double xNy = rs.getInt(5);
        // calculate the correlation between the two inverse predicates
        final double correlation =
            (linkCount * xNy - x * y) / Math.sqrt(x * (linkCount - x) * y * (linkCount - y));

        final InversePredicateModel resultItem = new InversePredicateModel();

        resultItem.setPredicateIDOne(idX);
        resultItem.setPredicateNameOne(rs.getString(1));
        resultItem.setPredicateIDTwo(idY);
        resultItem.setPredicateNameTwo(rs.getString(3));
        resultItem.setSupport(xNy / linkCount);
        resultItem.setCorrelation(correlation);
        resultItem.setCountIntersection((int) xNy);
        resultItem.setEntityOneCount((int) x);
        resultItem.setEntityTwoCount((int) y);

        ps.setInt(1, idX);
        ps.setInt(2, idY);

        ResultSet rsExamples = ps.executeQuery();
        // extract example subject pairs for the
        final ArrayList<String> examples1 = new ArrayList<String>();
        final ArrayList<String> examples2 = new ArrayList<String>();
        while (rsExamples.next()) {
          examples1.add(rsExamples.getString(1));
          examples2.add(rsExamples.getString(2));
        }
        resultItem.setExampleSubjects(examples1, examples2);

        rsExamples.close();

        result.add(resultItem);
        // index++;
        // TODO send progress feedback
        // progress #steps unknown
        // if(index%10==0)
        // progress.continueProgress(index);
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
        logger.warn(this.getClass().getName() + "#getInversePredicates()", e);
      }
    }

    return result;
  }

}
