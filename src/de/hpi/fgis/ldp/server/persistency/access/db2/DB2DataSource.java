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

package de.hpi.fgis.ldp.server.persistency.access.db2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibm.db2.jcc.DB2Driver;

import de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.SSHClient;

/**
 * a {@link IDataSource} instance for DB2 databases
 * 
 * @author toni.gruetze
 */
public class DB2DataSource extends AbstractDataSource {
  private final static String TYPE = "db2";
  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final String username;
  private final String password;
  private final String host;
  private final int port;

  private final String sshHost;
  private final int sshPort;
  private final String fingerprint;
  private final String sshUsername;
  private final String sshPassword;
  private final String remoteDirectory;

  private final String database;
  private final SSHClient sshClient;

  /**
   * creates a new {@link DB2DataSource}
   */
  @Inject
  protected DB2DataSource(@Named("db.db2.username") String username,
      @Named("db.db2.password") String password, @Named("db.db2.host") String host,
      @Named("db.db2.port") int port, @Named("db.db2.database") String database,
      @Named("db.db2.ssh.username") String sshUsername,
      @Named("db.db2.ssh.password") String sshPassword, @Named("db.db2.ssh.host") String sshHost,
      @Named("db.db2.ssh.port") int sshPort, @Named("db.db2.ssh.fingerprint") String fingerprint,
      @Named("db.db2.ssh.remoteDirectory") String remoteDirectory, SSHClient sshClient, Log logger) {
    super(DB2DataSource.TYPE, logger);
    this.username = username;
    this.password = password;
    this.host = host;
    this.port = port;
    this.sshHost = sshHost;
    this.sshPort = sshPort;
    this.fingerprint = fingerprint;
    this.sshUsername = sshUsername;
    this.sshPassword = sshPassword;
    this.remoteDirectory = remoteDirectory;
    this.database = database;
    this.sshClient = sshClient;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool.
   * IConnectionSource#createConnection()
   */
  @Override
  public Connection createConnection() throws SQLException {
    // Create Properties object
    Properties properties = new Properties();
    // Set user ID for connection
    properties.put("user", this.username);
    // Set password for connection
    properties.put("password", this.password);

    // Set schema for connection
    properties.put("currentSchema", this.getName());
    // Set description for connection
    properties.put("description", DB2DataSource.TYPE + "@" + this.getName());

    // Set URL for data source
    String url = this.getJDBCString();

    // creates database connection
    final Connection connection = DriverManager.getConnection(url, properties);

    if (!this.isValid(connection)) {
      return null;
    }

    return connection;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#optimize()
   */
  @Override
  public void optimize() {
    synchronized (this.resourceReader) {
      logger.info("starting db optimization run");
      // connect to ssh server
      // TODO inject
      Collection<String> tables = this.getTables();
      try {
        this.sshClient.init(sshHost, sshPort, fingerprint, sshUsername, sshPassword,
            remoteDirectory);
        if (this.sshClient.connect() && tables.size() > 0) {
          StringBuilder commandBuilder = new StringBuilder();
          // connect db2 cmd line
          // TODO inject
          List<String> sshStmtStrings =
              this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
                  "ssh/connect.ssh", this.database, this.username, this.password);

          for (final String sshCommand : sshStmtStrings) {
            commandBuilder.append(sshCommand).append(';');
          }

          for (final String currentEntity : tables) {
            // load db2 commands from resource
            sshStmtStrings =
                this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
                    "ssh/optimize.ssh", this.getName().toUpperCase(), currentEntity.toUpperCase());

            // execute ssh commands
            for (final String sshCommand : sshStmtStrings) {
              commandBuilder.append(sshCommand).append(';');
            }
          }
          final String command = commandBuilder.toString();
          this.getLogger().info(command + ": " + this.sshClient.sendCommand(command));
        } else {
          this.getLogger().warn("Unable to connect to SSH server for db2 database!");
        }
      } catch (IOException e) {
        this.getLogger().error("Unable to execute commands at SSH server for db2 database!", e);
      } finally {
        try {
          if (this.sshClient != null) {
            this.sshClient.disconnect();
          }
        } catch (IOException e) {
          this.getLogger().warn(this.getClass().getName() + "#optimize()", e);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource#getTables()
   */
  @Override
  public Collection<String> getTables() {
    final ArrayList<String> allTables = new ArrayList<String>();

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      con = this.getConnection();
      stmt = con.createStatement();

      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/selectTables.sql", this.getName().toUpperCase());
      rs = stmt.executeQuery(stmtString);

      while (rs.next()) {
        allTables.add(rs.getString(1));
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      this.getLogger().error("Unable to get table names from DB!", e);
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
        this.getLogger().warn(this.getClass().getName() + "#getTables()", e);
      }
    }
    return allTables;
  }

  public void optimizeToStdErr() {

    synchronized (this.resourceReader) {
      // connect to ssh server
      // TODO inject
      Collection<String> tables = this.getTables();
      try {
        StringBuilder commandBuilder = new StringBuilder();
        // connect db2 cmd line
        // TODO inject
        List<String> sshStmtStrings =
            this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
                "ssh/connect.ssh", this.database, this.username, this.password);

        for (final String sshCommand : sshStmtStrings) {
          commandBuilder.append(sshCommand).append('\n');
        }

        for (final String currentEntity : tables) {
          // load db2 commands from resource
          sshStmtStrings =
              this.resourceReader.getCommandsFromResource(this.getClass().getPackage(),
                  "ssh/optimize.ssh", this.getName().toUpperCase(), currentEntity.toUpperCase());

          // execute ssh commands
          for (final String sshCommand : sshStmtStrings) {
            commandBuilder.append(sshCommand).append('\n');
          }
        }
        final String command = commandBuilder.toString();
        System.err.println(command);
      } finally {
        try {
          if (this.sshClient != null) {
            this.sshClient.disconnect();
          }
        } catch (IOException e) {
          this.getLogger().warn(this.getClass().getName() + "#optimize()", e);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource#isValid( java.sql.Connection)
   */
  @Override
  public boolean isValid(Connection con) {
    // checks this db2 connection to be valid
    Statement stmt = null;
    boolean result = false;
    try {
      stmt = con.createStatement();
      result = stmt.execute("select 0 from SYSIBM.DUAL");
    } catch (SQLException e) {
      this.getLogger().error("Connection closed!", e);
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        this.getLogger().warn(this.getClass().getName() + "#isValid(Connection)", e);
      }
    }
    return result;
  }

  private final String getJDBCString() {
    return "jdbc:db2://" + this.host + ":" + this.port + "/" + this.database;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource#getDriver()
   */
  @Override
  protected DB2Driver getDriver() {
    return new com.ibm.db2.jcc.DB2Driver();
  }
}
