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

package de.hpi.fgis.ldp.server.persistency.access.pooling;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.persistency.access.ConnectionAccessException;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;

/**
 * This class holds a Pool of JDBC{@link Connection}s to perform real data access.
 * 
 * @author toni.gruetze
 * 
 */
public class ConnectionPool implements Closeable {

  /**
   * Represents a source for actual data connections.
   * 
   * @author toni.gruetze
   */
  public static interface IConnectionSource extends IDataSource {
    /**
     * Gets a JDBC-{@link Connection} from this instance.
     * 
     * @return a (new) connection or <code>null</code> if connection can't be established.
     * 
     * @throws SQLException if sth. goes wrong during creation process
     */
    public Connection createConnection() throws SQLException;

    /**
     * checks weather the given connection is valid or not
     * 
     * @param con the connection to be tested for validity
     * @return true if the connection is valid, otherwise false.
     */
    public boolean isValid(Connection con);
  }

  // // the package logger
  // private static Logger logger =
  // Logger.getLogger(ConnectionPool.class.getPackage().getName());

  private final Log logger;
  /**
   * idle time during waiting process (in ms)
   */
  private final long idleTime;
  private final long maxConnectionAge;
  private final int maxPoolSize;
  private final long maxWaitTime;
  private final long validityCheckDelay;
  private final Map<Connection, TimeInfo> allConnections;
  private final Queue<Connection> availableConnections;
  private IConnectionSource connectionSource;

  /**
   * Create a new {@link ConnectionPool} instance.
   */
  @Inject
  protected ConnectionPool(Log logger, @Named("db.pooling.maxPoolSize") int maxPoolSize,
      @Named("db.pooling.maxWaitTime") long maxWaitTime,
      @Named("db.pooling.maxConnectionAge") long maxConnectionAge,
      @Named("db.pooling.idleTime") long idleTime,
      @Named("db.pooling.validityCheckDelay") long validityCheckDelay) {
    this.logger = logger;

    this.idleTime = idleTime;
    this.maxConnectionAge = maxConnectionAge;
    this.maxPoolSize = maxPoolSize;
    this.maxWaitTime = maxWaitTime;

    this.validityCheckDelay = validityCheckDelay;

    this.allConnections = new HashMap<Connection, TimeInfo>(this.maxPoolSize);
    this.availableConnections = new LinkedList<Connection>();
  }

  /**
   * sets the connection source
   * 
   * @param connectionSource the connection source to be used
   */
  public void setConnectionSource(IConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }

  /**
   * gets the connection source
   * 
   * @return the connection source to be used
   */
  public IConnectionSource getConnectionSource() {
    return this.connectionSource;
  }

  /**
   * sets the {@link PooledJDBCConnection} to a "available" state
   * 
   * @param connection the connection to be available, right now.
   */
  protected void setAvailable(Connection connection) {
    synchronized (this) {
      if (this.allConnections.containsKey(connection)) {
        this.availableConnections.offer(connection);
      } else {
        releaseConnections(false, connection);
      }
    }
  }

  private boolean isValid(Connection connection) {
    synchronized (this) {
      final TimeInfo connectionTime = this.allConnections.get(connection);

      // unknown connection
      if (connectionTime == null) {
        return false;
      }
      final long now = System.currentTimeMillis();
      final long lastCheck = now - connectionTime.getLastCheck();
      final long age = now - connectionTime.getInit();
      // connection too old
      if (age > this.maxConnectionAge) {
        return false;
      }

      // accept shortly checked connections
      if (lastCheck <= this.validityCheckDelay) {
        return true;
      }
      // connection is opened
      try {
        if (connection.isClosed()) {
          return false;
        }
      } catch (SQLException e) {
        // sth. went wrong -> connection not valid anymore
        logger.fatal("Unable to check either the conneciton is opened or not!", e);
        return false;
      }

      final boolean isValid = this.connectionSource.isValid(connection);
      // refresh last checked time
      if (isValid) {
        connectionTime.setLastCheck(now);
      }
      return isValid;

    }
  }

  private void releaseConnections(boolean cleanupPool, Connection... connections) {
    synchronized (this) {
      for (Connection connection : connections) {
        this.allConnections.remove(connection);
        this.availableConnections.remove(connection);

        try {
          connection.close();
        } catch (SQLException e) {
          final String errMsg = "Error while trying to close connection!";
          logger.error(errMsg, e);
        }
      }

      if (cleanupPool) {
        // check all available other connections for validity (due to
        // better connection reset by peer problems)
        ArrayList<Connection> invalidConnections = new ArrayList<Connection>();
        for (Connection con : this.availableConnections) {
          if (!this.isValid(con)) {
            invalidConnections.add(con);
          }
        }

        this.releaseConnections(false,
            invalidConnections.toArray(new Connection[invalidConnections.size()]));
      }
    }
  }

  private Connection pollConnection() {
    Connection polledConnection = null;
    synchronized (this) {
      // simply try to get a available connection
      while (polledConnection == null && this.availableConnections.size() > 0) {
        Connection tmpConnection = this.availableConnections.poll();

        // if connection is faulty remove and check also all other
        // available connections
        if (!this.isValid(tmpConnection)) {
          this.releaseConnections(true, tmpConnection);
        } else {
          return tmpConnection;
        }
      }
    }
    return null;
  }

  /**
   * Gets a connection from this {@link ConnectionPoolException} instance. <b>Attention!</b> please
   * make sure to release the connection by calling {@link Connection#close()} after usage.
   * 
   * @return a (new) connection.
   */
  public PooledJDBCConnection getConnection() {
    final long startTime = System.currentTimeMillis();

    while (true) {
      // simply try to get a available connection (already existing)
      Connection actualConnection = this.pollConnection();

      // nothing found .. try to create new connection (again)
      if (actualConnection == null) {
        actualConnection = this.createConnection();
      }

      if (actualConnection != null) {
        return new PooledJDBCConnection(actualConnection, this);
      }

      // check if the max waiting time is reached
      final long timeDiff = System.currentTimeMillis() - startTime;
      if (this.maxWaitTime < timeDiff) {
        final String errMsg = "Unable to get a Connection since " + timeDiff + "ms.";
        logger.fatal(errMsg);
        throw new ConnectionAccessException(errMsg, this.connectionSource);
      }

      // no new connection available -> wait several ms and take an old
      // one (just wait a second)
      try {
        Thread.sleep(this.idleTime);
      } catch (InterruptedException e) {
        final String errMsg =
            "Interruption during waiting process! - Unable to get a Connection since "
                + (System.currentTimeMillis() - startTime) + "ms.";
        logger.fatal(errMsg, e);
        throw new ConnectionAccessException(errMsg, e, this.connectionSource);
      }
    }
  }

  private boolean lastConnectionCreationPossible = true;

  /**
   * create a new {@link Connection} instance an add it to the "all" pool if the pool size is not
   * yet reached
   * 
   * @return the new {@link Connection} instance or null if pool is already full
   */
  private Connection createConnection() {
    Connection actualConnection = null;
    // try to create a new connection
    synchronized (this) {
      if (this.allConnections.size() < this.maxPoolSize) {
        try {
          actualConnection = this.connectionSource.createConnection();

          // schedule release task 1s after point in time as the
          // actual max connection age is reached
          new ReleaseTask(actualConnection, maxConnectionAge + 1000);

          // it was possible to connection
          if (actualConnection != null) {
            this.allConnections.put(actualConnection, new TimeInfo());

            if (!lastConnectionCreationPossible) {
              this.lastConnectionCreationPossible = true;
            }
          } else {
            // it was not possible to create connection
            final String errMsg = "Unable to get a Connection for data source!";
            if (lastConnectionCreationPossible) {
              // if this is the first time -> log
              logger.fatal(errMsg);
              lastConnectionCreationPossible = false;
            }
            throw new ConnectionAccessException(errMsg, this.connectionSource);
          }
        } catch (SQLException e) {
          final String errMsg = "Failure during connection creation process!";
          logger.fatal(errMsg, e);

          throw new ConnectionAccessException(errMsg, e, this.connectionSource);
        }
      }
    }
    return actualConnection;
  }

  /**
   * Closes all connections in this pool (no check of their usage will be done).
   */
  public void closeAllConnections() {
    synchronized (this) {
      HashSet<Connection> connections = new HashSet<Connection>(this.allConnections.keySet());
      connections.addAll(this.availableConnections);

      this.releaseConnections(false, connections.toArray(new Connection[allConnections.size()]));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.Closeable#close()
   */
  @Override
  public void close() throws IOException {
    this.closeAllConnections();
  }

  private final static class TimeInfo {
    private final long init;
    private long lastCheck;

    public TimeInfo() {
      init = System.currentTimeMillis();
      this.lastCheck = init;
    }

    public long getLastCheck() {
      return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
      this.lastCheck = lastCheck;
    }

    public long getInit() {
      return init;
    }
  }

  private static final Timer timer = new Timer(true);

  private class ReleaseTask extends TimerTask {
    private final Connection con;

    public ReleaseTask(Connection con, long delay) {
      this.con = con;
      timer.schedule(this, delay);
    }

    @Override
    public void run() {
      synchronized (ConnectionPool.this) {
        if (availableConnections.contains(con)) {
          releaseConnections(false, this.con);
        }
        // otherwise the connection will be released later and then
        // checked for validity
      }
    }

  }
}
