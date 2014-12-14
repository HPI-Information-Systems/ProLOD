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

package de.hpi.fgis.ldp.server.persistency.access.pooling.test;

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
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.impl.SimpleLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.persistency.access.ConnectionAccessException;
import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool;
import de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool.IConnectionSource;

/**
 * the {@link ConnectionPool} test class
 * 
 * @author toni.gruetze
 * 
 */
public class ConnectionPoolTest {

  private ConnectionPool pool;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() {
    this.pool = new ConnectionPool(new SimpleLog("test"), 3, 100, 600000, 1000, 0) {};

    this.pool.setConnectionSource(new IConnectionSource() {
      private int index = 1;

      @Override
      public synchronized Connection createConnection() throws SQLException {
        Connection result = new ConnectionDummy(this.index);
        this.index++;
        return result;
      }

      @Override
      public boolean isValid(Connection con) {
        return true;
      }

      @Override
      public void close() {
        throw new IllegalStateException();
      }

      @Override
      public Connection getConnection() {
        throw new IllegalStateException();
      }

      @Override
      public String getName() {
        throw new IllegalStateException();
      }

      @Override
      public Collection<String> getTables() {
        throw new IllegalStateException();
      }

      @Override
      public String getType() {
        throw new IllegalStateException();
      }

      @Override
      public void optimize() {
        throw new IllegalStateException();
      }

      @Override
      public void setName(String name) {
        throw new IllegalStateException();
      }
    });
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() {
    this.pool = null;
  }

  /**
   * Test method for
   * {@link de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool#getConnection()} .
   * 
   * @throws SQLException
   */
  @Test
  public void testGetConnection() throws SQLException {
    Connection con1 = this.pool.getConnection();
    Assert.assertEquals("1", con1.getCatalog());
    Connection con2 = this.pool.getConnection();
    Assert.assertEquals("2", con2.getCatalog());
    Connection con3 = this.pool.getConnection();
    Assert.assertEquals("3", con3.getCatalog());
    Connection con4 = null;
    try {
      con4 = this.pool.getConnection();
      Assert.fail("got more connections than max. available (3)");
    } catch (ConnectionAccessException e) {
      // correct exception nothing to do
    }
    try {
      con2.close();
    } catch (SQLException e) {
      Assert.fail("unable to close/release connection" + e.getMessage());
      e.printStackTrace();
    }
    con4 = this.pool.getConnection();
    Assert.assertEquals("2", con4.getCatalog());
  }

  /**
   * Test method for
   * {@link de.hpi.fgis.ldp.server.persistency.access.pooling.ConnectionPool#closeAllConnections()}
   * .
   * 
   * @throws SQLException
   */
  @Test
  public void testCloseAllConnections() throws SQLException {
    Connection con1 = this.pool.getConnection();
    Connection con2 = this.pool.getConnection();
    Connection con3 = this.pool.getConnection();

    Assert.assertEquals("1", con1.getCatalog());
    Assert.assertEquals("2", con2.getCatalog());
    Assert.assertEquals("3", con3.getCatalog());

    Connection con4 = null;
    try {
      con4 = this.pool.getConnection();
      Assert.fail("got more connections than max. available (3)");
    } catch (ConnectionAccessException e) {
      // correct exception nothing to do
    }

    this.pool.closeAllConnections();

    Assert.assertEquals("-1", con1.getCatalog());
    Assert.assertEquals("-2", con2.getCatalog());
    Assert.assertEquals("-3", con3.getCatalog());

    con4 = this.pool.getConnection();
    Assert.assertEquals("4", con4.getCatalog());

    Connection con5 = this.pool.getConnection();
    Assert.assertEquals("5", con5.getCatalog());
    Connection con6 = this.pool.getConnection();
    Assert.assertEquals("6", con6.getCatalog());

    Connection con7 = null;
    try {
      con7 = this.pool.getConnection();
      Assert.fail("got more connections than max. available (3)");
    } catch (ConnectionAccessException e) {
      // correct exception nothing to do
    }

    con5.close();

    con7 = this.pool.getConnection();
    Assert.assertEquals("5", con7.getCatalog());
  }

  /**
   * No functionality at all (just for testing purposes)
   * 
   * @author toni.gruetze
   *
   */
  private class ConnectionDummy implements Connection {
    private int index;

    public ConnectionDummy(int index) {
      this.index = index;
    }

    @Override
    public String getCatalog() throws SQLException {
      return Integer.toString(this.index);
    }

    @Override
    public void clearWarnings() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void close() throws SQLException {
      this.index *= -1;
    }

    @Override
    public boolean isClosed() throws SQLException {
      return this.index < 0;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
      return !this.isClosed();
    }

    @Override
    public Statement createStatement() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void commit() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Blob createBlob() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Clob createClob() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public NClob createNClob() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
        throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Properties getClientInfo() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public int getHoldability() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
        int resultSetConcurrency) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
        int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void rollback() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new UnsupportedOperationException("Not Supported!");
    }

  }
}
