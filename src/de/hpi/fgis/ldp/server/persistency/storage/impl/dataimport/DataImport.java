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

package de.hpi.fgis.ldp.server.persistency.storage.impl.dataimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.ImportTuple;
import de.hpi.fgis.ldp.server.persistency.access.IDataSource;
import de.hpi.fgis.ldp.server.persistency.access.ISQLDataSourceProvider;
import de.hpi.fgis.ldp.server.persistency.storage.IDataImport;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Datatype;

/**
 * enables the import of large data set to a empty mysql schema
 * 
 * @author toni.gruetze
 */
public class DataImport implements IDataImport {
  protected final Log logger;
  private int tupleCount = 0;
  private final ISQLDataSourceProvider dataSourceProvider;

  // TODO inject
  private final ResourceReader resourceReader = new ResourceReader();

  protected final int maxStringLength;
  protected final int maxTextLength;
  protected final int maxPatternLength;
  protected final int maxThreadCount;
  protected final int maxBatchSize;
  private final String defaultUserView;
  private String schema;

  @Inject
  protected DataImport(ISQLDataSourceProvider provider,
      @Named("db.maxLengthOfStrings") int maxStringLength,
      @Named("db.maxLengthOfPatterns") int maxPatternLength,
      @Named("db.maxLengthOfTexts") int maxTextLength,
      @Named("server.maxThreadCount") int maxThreadCount,
      @Named("db.maxBatchSize") int maxBatchSize,
      @Named("db.defaultUserView") String defaultUserView, Log logger) {
    this.dataSourceProvider = provider;
    this.maxTextLength = maxTextLength;
    this.maxStringLength = maxStringLength;
    this.maxPatternLength = maxPatternLength;
    this.maxThreadCount = maxThreadCount;
    this.maxBatchSize = maxBatchSize;
    this.defaultUserView = defaultUserView;
    this.logger = logger;
  }

  @Override
  public void setSchemaName(final String schema) {
    this.schema = schema;
  }

  private IDataSource getDataSource() {
    if (this.schema == null) {
      throw new IllegalStateException(
          "Unable to get connection to data base, please specify the schema name!");
    }

    return dataSourceProvider.getConnectionPool(schema);
  }

  @Override
  public void cleanup(IProgress progress) throws SQLException {
    IDataSource dataSource = this.getDataSource();
    Connection con = null;
    Statement dropTables = null;
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> dropStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?dropTempTables.sql");

      progress.startProgress("cleaning up", dropStmtStrings.size());

      for (final String currentStmt : dropStmtStrings) {
        dropTables = con.createStatement();
        dropTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        dropTables.close();
        progress.continueProgress();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to create cluster tables!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (dropTables != null) {
          dropTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#cleanup()", e);
      }
    }
  }

  @Override
  public void createClusterTables(IProgress progress) throws SQLException {
    IDataSource dataSource = this.getDataSource();
    Connection con = null;
    Statement createTables = null;
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?createClusterTables.sql", this.defaultUserView);

      progress.startProgress("creating cluster tables", createStmtStrings.size());

      for (final String currentStmt : createStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
        progress.continueProgress();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to create cluster tables!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (createTables != null) {
          createTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#createClusterTables()", e);
      }
    }
  }

  @Override
  public void createMainTable(IProgress mainProgress) throws SQLException {
    IDataSource dataSource = this.getDataSource();

    Connection con = null;
    Statement createTables = null;
    // PreparedStatement fillMaintable = null;
    // ResultSet rs = null;
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

      // TODO inject
      int tuplePartitionRatio = 1000;
      // create the table creation statement
      List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?createMainTable.sql", Integer.valueOf(this.tupleCount),
              Integer.valueOf(this.tupleCount / tuplePartitionRatio), this.schema);

      mainProgress.startProgress("creating maintable structure", createStmtStrings.size());

      for (final String currentStmt : createStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
        mainProgress.continueProgress();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to create main table!", e);
      throw e;
    } finally {
      mainProgress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (createTables != null) {
          createTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#createMainTable()", e);
      }
    }
  }

  @Override
  public void createMaterializedViews(IProgress progress) throws SQLException {
    IDataSource dataSource = this.getDataSource();

    Connection con = null;
    Statement createTables = null;
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?createMaterializedViews.sql",
              Integer.valueOf(Datatype.TEXT.getId()), this.schema);

      progress.startProgress("creating materialized views", createStmtStrings.size());

      for (final String currentStmt : createStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
        progress.continueProgress();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to create materialized views!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (createTables != null) {
          createTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#createMaterializedViews()", e);
      }
    }
  }

  @Override
  public void createMetaTables(IProgress progress) throws SQLException {
    IDataSource dataSource = this.getDataSource();

    Connection con = null;
    Statement createTables = null;
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> createStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?createMetaTables.sql",
              Integer.valueOf(this.maxStringLength), Integer.valueOf(this.maxStringLength),
              Integer.valueOf(this.maxTextLength), Integer.valueOf(this.maxPatternLength),
              Integer.valueOf(this.maxPatternLength), this.schema);

      progress.startProgress("creating meta tables", createStmtStrings.size());

      for (final String currentStmt : createStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
        progress.continueProgress();
      }

    } catch (SQLException e) {
      this.logger.error("Unable to create meta tables!", e);
      throw e;
    } finally {
      progress.stopProgress();
      try {
        if (con != null) {
          con.commit();
          con.setAutoCommit(true);
          if (oldTransactionLevel >= 0) {
            con.setTransactionIsolation(oldTransactionLevel);
          }
          con.close();
        }
        if (createTables != null) {
          createTables.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#createMetaTables()", e);
      }
    }
  }

  @Override
  public int store(Iterable<ImportTuple> entries) throws SQLException {
    IDataSource dataSource = this.getDataSource();

    Connection con = null;
    Statement createTables = null;
    PreparedStatement fillTable = null;
    Statement finishTables = null;
    final TupleCounter tupleCounter = new TupleCounter();
    int oldTransactionLevel = -1;
    try {
      con = dataSource.getConnection();

      con.setAutoCommit(false);
      oldTransactionLevel = con.getTransactionIsolation();
      con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // create the table creation statement
      final List<String> createTableStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?createTempTable.sql",
              Integer.valueOf(this.maxStringLength), Integer.valueOf(this.maxStringLength),
              Integer.valueOf(this.maxTextLength), Integer.valueOf(this.maxPatternLength),
              Integer.valueOf(this.maxPatternLength));

      final List<String> commitStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?commitTempTable.sql", this.schema);

      // finish tmp table
      // create the finish table statement
      final List<String> finishTableStmtStrings =
          this.resourceReader.getCommandsFromResource(this.getClass().getPackage(), "sql/?"
              + dataSource.getType() + "/?finishTempTable.sql",
              Integer.valueOf(this.maxStringLength), Integer.valueOf(this.maxStringLength),
              Integer.valueOf(this.maxTextLength), Integer.valueOf(this.maxPatternLength),
              Integer.valueOf(this.maxPatternLength), this.schema);

      for (final String currentStmt : createTableStmtStrings) {
        createTables = con.createStatement();
        createTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
        createTables.close();
      }

      // Fill table
      // create the prepared statement to fill the import table
      final String fillTableStmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + dataSource.getType() + "/?fillTempTable.sql");

      fillTable = con.prepareStatement(fillTableStmtString, Statement.NO_GENERATED_KEYS);

      final PreparedStatement finalFillStatement = fillTable;

      final int batchSize = this.maxBatchSize;

      final Iterator<ImportTuple> iterator = entries.iterator();

      final Connection connection = con;
      // create several jobs which parallel read tuples
      ArrayList<Runnable> jobs = new ArrayList<Runnable>(this.maxThreadCount);
      for (int i = 0; i < this.maxThreadCount; i++) {
        jobs.add(new Runnable() {
          /*
           * (non-Javadoc)
           * 
           * @see java.lang.Runnable#run()
           */
          @Override
          public void run() {

            while (true) {
              ImportTuple tuple = null;
              try {
                // get the next tuple, if nothing available stop
                // this job
                tuple = iterator.next();
              } catch (NoSuchElementException e) {
                // nothing special to do .. iterator empty
                break;
              }

              // persist this tuple to the database
              if (tuple != null) {
                synchronized (finalFillStatement) {
                  try {
                    // set tuple values
                    finalFillStatement.setString(
                        1,
                        tuple.getSubject() == null ? null : tuple.getSubject().substring(0,
                            Math.min(tuple.getSubject().length(), DataImport.this.maxStringLength)));
                    finalFillStatement.setString(
                        2,
                        tuple.getPredicate() == null ? null : tuple.getPredicate()
                            .substring(
                                0,
                                Math.min(tuple.getPredicate().length(),
                                    DataImport.this.maxStringLength)));
                    finalFillStatement.setString(
                        3,
                        tuple.getObject() == null ? null : tuple.getObject().substring(0,
                            Math.min(tuple.getObject().length(), DataImport.this.maxTextLength)));
                    finalFillStatement.setString(
                        4,
                        tuple.getNormPattern() == null ? null : tuple.getNormPattern().substring(
                            0,
                            Math.min(tuple.getNormPattern().length(),
                                DataImport.this.maxPatternLength)));
                    finalFillStatement.setString(
                        5,
                        tuple.getPattern() == null ? null : tuple.getPattern()
                            .substring(
                                0,
                                Math.min(tuple.getPattern().length(),
                                    DataImport.this.maxPatternLength)));
                    finalFillStatement.setInt(6, tuple.getDatatype().getId());
                    finalFillStatement.setDouble(7, tuple.getParsedValue());

                    finalFillStatement.addBatch();

                    if (tupleCounter.next() % batchSize == 0) {
                      finalFillStatement.executeBatch();
                      finalFillStatement.clearBatch();
                      for (final String currentStmt : commitStmtStrings) {
                        final Statement commitStatement = connection.createStatement();
                        commitStatement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
                        commitStatement.close();
                      }
                    }
                  } catch (Exception e) {
                    // log error but ignore this to execute
                    // the other
                    // inserts
                    if (!tupleCounter.failure) {
                      DataImport.this.logger.fatal("Unable to execute batch!", e);
                      if (e instanceof SQLException) {
                        SQLException e2 = (SQLException) e;
                        while ((e2 = e2.getNextException()) != null) {
                          DataImport.this.logger.fatal("Following Exceptions: ", e2);
                        }
                      }
                      tupleCounter.failure = true;
                    }
                  }

                }
              }
            }
            try {
              synchronized (finalFillStatement) {
                // 600 000 ~ 30min
                finalFillStatement.executeBatch();
                finalFillStatement.clearBatch();
                for (final String currentStmt : commitStmtStrings) {
                  final Statement commitStatement = connection.createStatement();
                  commitStatement.execute(currentStmt, Statement.NO_GENERATED_KEYS);
                  commitStatement.close();
                }
              }
            } catch (Exception e) {
              // log error but ignore this to execute the other
              // inserts
              DataImport.this.logger.fatal("Unable to execute batch!", e);
            }
          }
        });
      }
      // start all jobs until the last one
      for (int i = 0; i < this.maxThreadCount - 1; i++) {
        Thread executer = new Thread(jobs.get(i));
        executer.setDaemon(true);
        executer.start();
      }

      // run the last job
      jobs.get(this.maxThreadCount - 1).run();

      // wait 5 seconds to be sure that all jobs have finished - every job
      // has to finish exactly one (the last) tuple (means
      // parsing&persistence)
      if (this.maxThreadCount > 1) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          // ignore interruption while waiting
          this.logger.warn(this.getClass().getName() + "#store()", e);
        }
      }

      for (final String currentStmt : finishTableStmtStrings) {
        if (currentStmt != null && !"".equals(currentStmt.trim())) {
          finishTables = con.createStatement();
          finishTables.execute(currentStmt, Statement.NO_GENERATED_KEYS);
          finishTables.close();
        }
      }
    } catch (SQLException e) {
      this.logger.error("Unable to import data from source!", e);
      throw e;
    } finally {
      this.logger.info(tupleCounter.current() + " tuples read!");
      try {
        synchronized (fillTable) {
          if (fillTable != null) {
            try {
              fillTable.executeBatch();
              fillTable.clearBatch();
            } catch (SQLException e) {
              // ignore Errors while commiting the last tuples
            }
          }
          if (con != null) {
            con.commit();
            con.setAutoCommit(true);
            if (oldTransactionLevel >= 0) {
              con.setTransactionIsolation(oldTransactionLevel);
            }
            con.close();
          }
          if (createTables != null) {
            createTables.close();
          }
          if (fillTable != null) {
            fillTable.close();
          }
          if (finishTables != null) {
            finishTables.close();
          }
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        this.logger.warn(this.getClass().getName() + "#store()", e);
      }
    }
    this.tupleCount = tupleCounter.current();
    return this.tupleCount;
  }

  protected static class TupleCounter {
    private int tupleCount;
    public boolean failure = false;

    public TupleCounter() {
      this.tupleCount = 0;
    }

    public int next() {
      return ++this.tupleCount;
    }

    public int current() {
      return this.tupleCount;
    }
  }
}
