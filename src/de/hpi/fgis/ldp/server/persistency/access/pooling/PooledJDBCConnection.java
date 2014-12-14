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
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * A simple Wrapper which enables the pooled usage of {@link Connection} instances. A instance of
 * this class sets itself available for further usage in the containing connection pool, if it will
 * be "closed" virtually.
 * 
 * @author toni.gruetze
 */
public class PooledJDBCConnection implements Connection, Closeable {
  private final Connection actualConnection;
  private final ConnectionPool releaseObserver;
  private boolean isClosed = false;
  private final ArrayList<Statement> statementChildren = new ArrayList<Statement>(2);

  /**
   * Creates a new {@link PooledJDBCConnection} wrapper instance containing an actual
   * {@link Connection}
   * 
   * @param actualConnection the actual {@link Connection} instance to be used for data access
   * @param observer the observer to be informed if the connection will be closed "virtually" (means
   *        released by the component)
   */
  public PooledJDBCConnection(final Connection actualConnection, final ConnectionPool observer) {
    this.actualConnection = actualConnection;
    this.releaseObserver = observer;
  }

  /**
   * Gets the actual {@link Connection} instance.
   * 
   * @return the actual {@link Connection} instance.
   */
  public Connection getActualConnetion() {
    return this.actualConnection;
  }

  /**
   * release this Connection and make it available in the pool.
   */
  @Override
  public synchronized void close() {
    if (!this.isClosed) {
      this.isClosed = true;

      // close all opened connections
      for (final Statement current : this.statementChildren) {
        if (current != null) {
          try {
            current.close();
          } catch (SQLException e) {
            // ignore errors during closing process
          }
        }
      }

      this.releaseObserver.setAvailable(this.actualConnection);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#close()
   */
  public void closeActualConnection() throws SQLException {
    this.actualConnection.close();
  }

  /**
   * @throws SQLException
   * @see java.sql.Connection#clearWarnings()
   */
  @Override
  public void clearWarnings() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.clearWarnings();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#commit()
   */
  @Override
  public void commit() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.commit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
   */
  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createArrayOf(typeName, elements);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createBlob()
   */
  @Override
  public Blob createBlob() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createBlob();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createClob()
   */
  @Override
  public Clob createClob() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createClob();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createNClob()
   */
  @Override
  public NClob createNClob() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createNClob();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createSQLXML()
   */
  @Override
  public SQLXML createSQLXML() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createSQLXML();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement()
   */
  @Override
  public Statement createStatement() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }

    final Statement stmt = this.actualConnection.createStatement();

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement(int, int, int)
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final Statement stmt =
        this.actualConnection.createStatement(resultSetType, resultSetConcurrency,
            resultSetHoldability);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement(int, int)
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final Statement stmt =
        this.actualConnection.createStatement(resultSetType, resultSetConcurrency);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
   */
  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.createStruct(typeName, attributes);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getAutoCommit()
   */
  @Override
  public boolean getAutoCommit() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getAutoCommit();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getCatalog()
   */
  @Override
  public String getCatalog() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getCatalog();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getClientInfo()
   */
  @Override
  public Properties getClientInfo() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getClientInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getClientInfo(java.lang.String)
   */
  @Override
  public String getClientInfo(String name) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getClientInfo(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getHoldability()
   */
  @Override
  public int getHoldability() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getHoldability();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getMetaData()
   */
  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getMetaData();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getTransactionIsolation()
   */
  @Override
  public int getTransactionIsolation() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getTransactionIsolation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getTypeMap()
   */
  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getTypeMap();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getWarnings()
   */
  @Override
  public SQLWarning getWarnings() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.getWarnings();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isClosed()
   */
  @Override
  public boolean isClosed() throws SQLException {
    return this.isClosed || this.actualConnection.isClosed();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isReadOnly()
   */
  @Override
  public boolean isReadOnly() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.isReadOnly();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isValid(int)
   */
  @Override
  public boolean isValid(int timeout) throws SQLException {
    return !this.isClosed && this.actualConnection.isValid(timeout);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.isWrapperFor(iface);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#nativeSQL(java.lang.String)
   */
  @Override
  public String nativeSQL(String sql) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.nativeSQL(sql);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
   */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final CallableStatement stmt =
        this.actualConnection.prepareCall(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
   */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final CallableStatement stmt =
        this.actualConnection.prepareCall(sql, resultSetType, resultSetConcurrency);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String)
   */
  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final CallableStatement stmt = this.actualConnection.prepareCall(sql);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType,
      int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt =
        this.actualConnection.prepareStatement(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt =
        this.actualConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int)
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt = this.actualConnection.prepareStatement(sql, autoGeneratedKeys);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt = this.actualConnection.prepareStatement(sql, columnIndexes);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
   */
  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt = this.actualConnection.prepareStatement(sql, columnNames);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String)
   */
  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    final PreparedStatement stmt = this.actualConnection.prepareStatement(sql);

    this.statementChildren.add(stmt);

    return stmt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
   */
  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.releaseSavepoint(savepoint);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#rollback()
   */
  @Override
  public void rollback() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.rollback();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#rollback(java.sql.Savepoint)
   */
  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.rollback(savepoint);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setAutoCommit(boolean)
   */
  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setAutoCommit(autoCommit);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setCatalog(java.lang.String)
   */
  @Override
  public void setCatalog(String catalog) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setCatalog(catalog);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setClientInfo(java.util.Properties)
   */
  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    if (this.isClosed) {
      throw new SQLClientInfoException("Unable to use already closed connections!", null);
    }
    this.actualConnection.setClientInfo(properties);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
   */
  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    if (this.isClosed) {
      throw new SQLClientInfoException("Unable to use already closed connections!", null);
    }
    this.actualConnection.setClientInfo(name, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setHoldability(int)
   */
  @Override
  public void setHoldability(int holdability) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setHoldability(holdability);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setReadOnly(boolean)
   */
  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setReadOnly(readOnly);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint()
   */
  @Override
  public Savepoint setSavepoint() throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.setSavepoint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint(java.lang.String)
   */
  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.setSavepoint(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setTransactionIsolation(int)
   */
  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setTransactionIsolation(level);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setTypeMap(java.util.Map)
   */
  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    this.actualConnection.setTypeMap(map);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (this.isClosed) {
      throw new SQLException("Unable to use already closed connections!");
    }
    return this.actualConnection.unwrap(iface);
  }
}
