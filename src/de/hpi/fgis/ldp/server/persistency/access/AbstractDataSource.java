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

package de.hpi.fgis.ldp.server.persistency.access;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool;
import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool.IConnectionSource;

/**
 * abstract implementation of the {@link IDataSource} interface. This class holds different
 * Informations to provide the {@link IDataSource} features with the help of a
 * {@link IConnectionSource}
 * 
 * @author toni.gruetze
 * 
 */
public abstract class AbstractDataSource implements IConnectionSource {
  private String name;
  private final String type;
  protected final Log logger;
  private ConnectionPool connectionPool;

  private static Collection<Class<? extends Driver>> registered =
      new HashSet<Class<? extends Driver>>();

  private void registerDriver() {
    synchronized (registered) {
      Driver driver = null;
      try {
        driver = this.getDriver();
        if (!registered.contains(driver.getClass())) {
          // build path
          DriverManager.registerDriver(driver);

          registered.add(driver.getClass());
        }
      } catch (SQLException e) {
        if (driver == null) {
          logger.error("Unable to initialize driver of \"" + this.getClass().getName()
              + "#getDriver()\"", e);
        } else {
          logger.error("Unable to initialize driver \"" + driver.getClass().getName() + "\"", e);
        }
      }
    }
  }

  /**
   * gets the jdbc driver of this concrete instance
   * 
   * @return the jdbc driver
   */
  protected abstract Driver getDriver() throws SQLException;

  /**
   * Creates a new instance of {@link AbstractDataSource}
   * 
   * @param name the name of the new {@link IDataSource}
   * @param type the type of the new {@link IDataSource}
   * @param connectionSource the connection which should be used to create new JDBC
   *        {@link Connection}s for the integrated {@link ConnectionPool}.
   */
  protected AbstractDataSource(final String type, Log logger) {
    this.type = type;
    this.logger = logger;
    this.registerDriver();
  }

  @Inject
  protected void setConnectionPoolInstance(ConnectionPool pool) {
    this.connectionPool = pool;
    this.connectionPool.setConnectionSource(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#getType()
   */
  @Override
  public final Connection getConnection() {
    // Get a connection from pool
    return this.connectionPool.getConnection();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#getType()
   */
  @Override
  public String getName() {
    return this.name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#setName(java.lang .String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#getType()
   */
  @Override
  public String getType() {
    return this.type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.IDataSource#close()
   */
  @Override
  public void close() {
    this.connectionPool.closeAllConnections();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool.
   * IConnectionSource#isValid(java.sql.Connection)
   */
  @Override
  public boolean isValid(Connection con) {
    try {
      return con.isValid(500);
    } catch (SQLException e) {
      this.getLogger().warn("Connection closed!", e);
      try {
        con.close();
      } catch (SQLException e1) {
        // ignore errors during cleanup
      }
    }
    return false;
  }

  /**
   * gets the logger
   * 
   * @return the logger
   */
  protected final Log getLogger() {
    return this.logger;
  }

  private static HashSet<String> optimizedConnectionTypes = new HashSet<String>();
  private final static Timer timer = new Timer(true);

  @Inject
  protected void startAutomaticOptimization(@Named("db.autoOptimizeDelay") int delay) {
    synchronized (optimizedConnectionTypes) {
      // optimize if needed but just once for a connection type
      if (delay > 0 && !optimizedConnectionTypes.contains(this.type)) {
        optimizedConnectionTypes.add(this.type);

        logger.info("activating db optimization task");
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            AbstractDataSource.this.optimize();
          }
          // schedule in repetition [delay]ms beginning @midnight
        }, delay - (System.currentTimeMillis() % delay), delay);
      }
    }
  }
}
