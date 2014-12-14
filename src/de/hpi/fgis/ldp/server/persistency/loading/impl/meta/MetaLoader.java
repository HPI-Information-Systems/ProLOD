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

package de.hpi.fgis.ldp.server.persistency.loading.impl.meta;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.IMetaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * repository of meta information in a DB2 database
 * 
 * @author toni.gruetze
 */
public class MetaLoader extends LoaderBase implements IMetaLoader {
  private final Log logger;
  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final int fetchRowSize;

  @Inject
  protected MetaLoader(@Named("db.fetchRowSize") int fetchRowSize, Log logger) {
    this.fetchRowSize = fetchRowSize;
    this.logger = logger;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.impl.meta.IMetaLoader#
   * fillPredicateNameMap(java.util.Map)
   */
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
              "sql/?" + super.getDataSourceType(source) + "/?predicateNames.sql", allKeys);
      rs = stmt.executeQuery(stmtString);
      // TODO inject
      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
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

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.impl.meta.IMetaLoader#
   * fillSubjectNameMap(java.util.Map)
   */
  @Override
  public void fillTIDNameMap(TIntObjectHashMap<String> subjects, String ruleConfiguration,
      DataSource source) {
    if (subjects.size() <= 0) {
      return;
    }
    final String allKeys = this.getSQLList(subjects.keySet());

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = super.newConnection(source);
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?subjectNames.sql", allKeys);

      rs = stmt.executeQuery(stmtString);
      // TODO inject
      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        subjects.put(Integer.valueOf(rs.getInt(1)), rs.getString(2));
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
  public Pair<TIntObjectHashMap<String>, TIntObjectHashMap<String>> getSynonymDetails(
      int[] predicatePair, int objectID, DataSource source) {
    // TODO Auto-generated method stub
    return null;
  }
}
