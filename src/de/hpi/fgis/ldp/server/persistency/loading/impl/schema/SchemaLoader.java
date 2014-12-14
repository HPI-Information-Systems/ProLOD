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

package de.hpi.fgis.ldp.server.persistency.loading.impl.schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * Loads cluster information from DB2
 * 
 * @author daniel.hefenbrock
 * @author toni.gruetze
 */
public class SchemaLoader extends LoaderBase implements ISchemaLoader {
  protected final Log logger;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final String defaultUserView;

  private final DataSource mainSchemaSource;

  @Inject
  public SchemaLoader(@Named("db.mainSchema") String mainSchema,
      @Named("db.defaultUserView") String defaultUserView, Log logger) {
    this.mainSchemaSource = new DataSource(mainSchema);
    this.logger = logger;
    this.defaultUserView = defaultUserView;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader#getRoots(de.
   * hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public List<Cluster> getRootClusters(final IProgress progress) throws SQLException {
    Connection con = null;
    Statement loadClusters = null;
    ResultSet rsClusters = null;
    List<Cluster> rootClusters = new ArrayList<Cluster>();

    try {
      progress.startProgress("selecting root clusters");

      con = super.newConnection(this.mainSchemaSource);

      // create the cluster selection statement
      final String clusterStmtString =
          this.resourceReader
              .getUndocumentedStringFromResource(this.getClass().getPackage(),
                  "sql/?" + super.getDataSourceType(this.mainSchemaSource)
                      + "/?selectRootClusters.sql", this.defaultUserView);

      loadClusters = con.createStatement();
      rsClusters = loadClusters.executeQuery(clusterStmtString);

      int tmp_id = 0;
      while (rsClusters.next()) {
        // schema.id, schema.schema_name, schema.root_session,
        // schema.tuples, schema.entities, username
        final Cluster newRootCluster = new Cluster(new DataSource(rsClusters.getString(1)));

        newRootCluster.setId(--tmp_id);
        newRootCluster.setLabel(rsClusters.getString(2));
        newRootCluster.setChildSessionID(rsClusters.getInt(3));
        newRootCluster.setTripleCount(rsClusters.getInt(4));
        newRootCluster.setSize(rsClusters.getInt(5));

        rootClusters.add(newRootCluster);
      }

    } catch (SQLException e) {
      logger.error("Unable to get root cluster data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getRoots()", e);
      }
    }
    return rootClusters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.ISchemaLoader#getRootCluster
   * (de.hpi.fgis.ldp.shared.data.DataSource, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public Cluster getRootCluster(DataSource source, IProgress progress) throws SQLException {
    // load root session of data source
    Connection con = null;
    Statement loadClusters = null;
    ResultSet rsClusters = null;

    try {
      progress.startProgress("selecting root clusters");

      con = super.newConnection(this.mainSchemaSource);

      String userView = source.getUserView();
      if (userView == null) {
        userView = this.defaultUserView;
      }

      // create the cluster selection statement
      final String clusterStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.mainSchemaSource) + "/?selectRootCluster.sql",
              source.getLabel(), userView);

      loadClusters = con.createStatement();
      rsClusters = loadClusters.executeQuery(clusterStmtString);

      if (rsClusters.next()) {
        // schema.id, schema.schema_name, schema.root_session,
        // schema.tuples, schema.entities, username
        String targetUserView = rsClusters.getString(6);
        // default view
        if (targetUserView.equals(this.defaultUserView)) {
          targetUserView = null;
        }
        final Cluster newRootCluster =
            new Cluster(new DataSource(rsClusters.getString(1)).asUserView(targetUserView));

        newRootCluster.setId(-1);
        newRootCluster.setLabel(rsClusters.getString(2));
        newRootCluster.setChildSessionID(rsClusters.getInt(3));
        newRootCluster.setTripleCount(rsClusters.getInt(4));
        newRootCluster.setSize(rsClusters.getInt(5));

        return newRootCluster;
      } else {
        return null;
      }

    } catch (SQLException e) {
      logger.error("Unable to get root cluster data from DB!", e);
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
        logger.warn(this.getClass().getName() + "#getRoots()", e);
      }
    }
  }
}
