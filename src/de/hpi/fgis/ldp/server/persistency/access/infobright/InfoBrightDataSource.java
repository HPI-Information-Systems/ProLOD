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

package de.hpi.fgis.ldp.server.persistency.access.infobright;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysql.jdbc.Driver;

import de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.util.ResourceReader;

/**
 * a {@link IDataSource} instance for InfoBright databases
 * 
 * @author toni.gruetze
 * 
 */
public class InfoBrightDataSource extends AbstractDataSource {
  private final static String TYPE = "infobright";

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  private final String username;
  private final String password;
  private final String host;
  private final int port;

  /**
   * creates a new {@link InfoBrightDataSource}
   */
  @Inject
  protected InfoBrightDataSource(@Named("db.infobright.username") String username,
      @Named("db.infobright.password") String password, @Named("db.infobright.host") String host,
      @Named("db.infobright.port") int port, Log logger) {
    super(InfoBrightDataSource.TYPE, logger);
    this.username = username;
    this.password = password;
    this.host = host;
    this.port = port;
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
    properties.put("user", this.username);// NonRegisteringDriver.USER_PROPERTY_KEY
    // Set password for connection
    properties.put("password", this.password);// NonRegisteringDriver.PASSWORD_PROPERTY_KEY

    // Set schema for connection
    properties.put("DBNAME", this.getName());// NonRegisteringDriver.DBNAME_PROPERTY_KEY
    // Set description for connection
    properties.put("description", InfoBrightDataSource.TYPE + "@" + this.getName());

    // Set URL for data source
    String url = this.getJDBCString();

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
    // TODO check whether sth. can be done or not
    // String... entityIDs
  }

  private final String getJDBCString() {
    return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.AbstractDataSource#getDriver()
   */
  @Override
  protected Driver getDriver() throws SQLException {
    return new Driver();
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
      result = stmt.execute("select 0");
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
}
