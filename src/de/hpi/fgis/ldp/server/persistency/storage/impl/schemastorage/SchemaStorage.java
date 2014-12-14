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

package de.hpi.fgis.ldp.server.persistency.storage.impl.schemastorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * enables the creation of MySQL data base schemata
 * 
 * @author toni.gruetze
 * 
 */
public class SchemaStorage extends LoaderBase implements ISchemaStorage {
  private final Log logger;

  private final String mainSchemaName;
  private final String defaultUserView;
  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  @Inject
  public SchemaStorage(@Named("db.mainSchema") String mainSchema, Log logger,
      @Named("db.defaultUserView") String defaultUserView) {
    this.mainSchemaName = mainSchema;
    this.logger = logger;
    this.defaultUserView = defaultUserView;
  }

  String toValidSchemaName(String schema) {
    final String result = schema.toUpperCase().replaceAll("[^A-Z0-9]+", "_");
    if (result.length() <= 64) {
      return result;
    }
    return result.substring(0, 64);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage#createSchema (java.lang.String,
   * java.lang.String, de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public String createSchema(final String schemaName, final String label, final IProgress progress)
      throws SQLException {
    // check for valid schema name for db
    final String internalDBName = toValidSchemaName(schemaName);
    Connection con = null;
    Statement createSchema = null;
    PreparedStatement publishSchema = null;
    try {
      progress.startProgress("creating schema");
      DataSource source = new DataSource(this.mainSchemaName);
      con = super.newConnection(source);

      // create the table creation statement
      final List<String> createSchemaCreationStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(new DataSource(internalDBName))
                  + "/?createSchema.sql", internalDBName);

      progress.startProgress("creating schema", createSchemaCreationStmtStrings.size() + 2);

      // create the prepared statement to fill the import table
      final String publishSchemaStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?publishSchema.sql",
              this.mainSchemaName);

      publishSchema = con.prepareStatement(publishSchemaStmtString, Statement.NO_GENERATED_KEYS);

      // valid schema name for db
      publishSchema.setString(1, internalDBName);
      publishSchema.setString(2, label);

      publishSchema.executeUpdate();

      progress.continueProgress();

      for (final String currentStmt : createSchemaCreationStmtStrings) {
        createSchema = con.createStatement();
        createSchema.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createSchema.close();
        progress.continueProgress();
      }

      this.publishRootSession(con, new DataSource(internalDBName), this.defaultUserView, -1,
          progress.continueWithSubProgress(1));
    } catch (SQLException e) {
      this.logger.error("Unable to create new schema \"" + internalDBName + "\"!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (createSchema != null) {
          createSchema.close();
        }
        if (publishSchema != null) {
          publishSchema.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#createSchema()", e);
      }
    }
    return internalDBName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage#dropSchema( java.lang.String,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void dropSchema(String schemaName, IProgress progress) throws SQLException {
    Connection con = null;
    Statement dropSchema = null;
    boolean lastStatementFailed = false;
    try {
      DataSource source = new DataSource(schemaName);
      con = super.newConnection(source);

      Collection<String> tableNames = super.getTables(source);

      // create the table creation statement
      final List<String> schemaDropStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?dropSchema.sql", this.mainSchemaName,
              schemaName);

      progress.startProgress("dropping schema", tableNames.size() + schemaDropStmtStrings.size());

      // just log the first exception!
      SQLException firstException = null;

      // drop tables
      for (final String tableName : tableNames) {
        try {
          this.dropTable(tableName, source, con);
        } catch (SQLException e) {
          if (firstException == null) {
            firstException = e;
          }
        }
        progress.continueProgress();
      }

      for (int statementIndex = 0; statementIndex < schemaDropStmtStrings.size(); statementIndex++) {
        final String currentStmt = schemaDropStmtStrings.get(statementIndex);
        try {
          dropSchema = con.createStatement();
          dropSchema.execute(currentStmt, Statement.NO_GENERATED_KEYS);
          dropSchema.close();
          progress.continueProgress();
        } catch (SQLException e) {
          if (firstException == null) {
            firstException = e;
          }
          if (statementIndex >= schemaDropStmtStrings.size() - 1) {
            lastStatementFailed = true;
          }
        }
      }
      if (firstException != null) {
        this.logger.error(
            "Some errors occured while trying to drop schema \"" + schemaName + "\"!",
            firstException);

        // throw the first exception if the last statement couldn't be
        // executed
        if (lastStatementFailed) {
          throw firstException;
        }
      }
      super.dropConnections(source);
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (dropSchema != null) {
          dropSchema.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#dropSchema()", e);
      }
    }
  }

  private void dropTable(String tableName, DataSource source, Connection con) throws SQLException {
    Statement dropTable = null;
    try {

      // create the table creation statement
      final List<String> tableDropStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?dropTable.sql", tableName);

      // just log the first exception!
      SQLException firstException = null;
      for (int statementIndex = 0; statementIndex < tableDropStmtStrings.size(); statementIndex++) {
        final String currentStmt = tableDropStmtStrings.get(statementIndex);
        try {
          dropTable = con.createStatement();
          dropTable.executeUpdate(currentStmt, Statement.NO_GENERATED_KEYS);
          dropTable.close();
        } catch (SQLException e) {
          if (firstException == null) {
            firstException = e;
          }
        }
      }
      if (firstException != null) {
        throw firstException;
      }
    } finally {
      try {
        if (dropTable != null) {
          dropTable.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#dropTable()", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.storage.ISchemaStorage#publishRootSession
   * (java.lang.String, de.hpi.fgis.ldp.server.datastructures.Session,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public void publishRootSession(Session rootSession, IProgress progress) throws SQLException {

    Connection con = null;
    Statement updateSchema = null;
    DataSource source = rootSession.getDataSource();
    final String schemaName = source.getLabel();

    try {
      progress.startProgress("setting schema meta information", 2);
      con = super.newConnection(source);

      String userView = rootSession.getDataSource().getUserView();
      if (userView == null) {
        userView = this.defaultUserView;
      }

      // create the statement to update meta information
      final String updateMetaInformationStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?updateMetaInformation.sql",
              this.mainSchemaName, schemaName);

      updateSchema = con.createStatement();

      updateSchema.execute(updateMetaInformationStmtString, Statement.NO_GENERATED_KEYS);
      progress.continueProgress();

      // update progress.continueProgress();

      this.publishRootSession(con, source, userView, rootSession.getId(),
          progress.continueWithSubProgress(1));
    } catch (SQLException e) {
      this.logger.error("Unable to create update schema meta data of \"" + schemaName + "\"!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.close();
        }
        if (updateSchema != null) {
          updateSchema.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#publishRootSession()", e);
      }
    }
  }

  private void publishRootSession(Connection con, DataSource source, String userView,
      int sessionID, IProgress progress) throws SQLException {
    PreparedStatement updateRootSession = null;
    final String schemaName = source.getLabel();

    try {
      progress.startProgress("setting root session information");
      con = super.newConnection(source);

      // create the prepared statement to fill the root_sessions table
      final String publishSchemaStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(source) + "/?publishSchemaClustering.sql",
              this.mainSchemaName);

      updateRootSession = con.prepareStatement(publishSchemaStmtString);

      updateRootSession.setString(1, schemaName);
      updateRootSession.setString(2, userView);
      updateRootSession.setInt(3, sessionID);

      updateRootSession.execute();

    } finally {
      progress.stopProgress();
      try {
        if (updateRootSession != null) {
          updateRootSession.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#publishRootSession()", e);
      }
    }
  }
}
