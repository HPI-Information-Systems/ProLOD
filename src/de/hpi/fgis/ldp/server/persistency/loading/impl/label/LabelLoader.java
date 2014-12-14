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

package de.hpi.fgis.ldp.server.persistency.loading.impl.label;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader;
import de.hpi.fgis.ldp.server.persistency.loading.impl.base.LoaderBase;
import de.hpi.fgis.ldp.server.util.ResourceReader;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * The {@link ILabelLoader} is used to extract all possible descriptions for entities in a cluster
 * from database.
 * 
 * @author toni.gruetze
 * @author david.sonnabend
 * 
 */
public class LabelLoader extends LoaderBase implements ILabelLoader {
  // TODO inject via setters
  @Inject
  protected Log logger;

  private final int fetchRowSize;
  private final int maxDescriptions;
  private final String defaultUserView;

  // TODO inject
  protected ResourceReader resourceReader = new ResourceReader();

  private final DataSource mainSchemaSource;

  @Inject
  protected LabelLoader(@Named("db.mainSchema") String mainSchema,
      @Named("db.fetchRowSize") int fetchRowSize,
      @Named("labeling.maxDescriptionTexts") int maxDescriptions,
      @Named("db.defaultUserView") String defaultUserView) {
    this.mainSchemaSource = new DataSource(mainSchema);
    this.fetchRowSize = fetchRowSize;
    this.maxDescriptions = maxDescriptions;
    this.defaultUserView = defaultUserView;
  }

  /**
   * iterates the description texts of different clusters of a clustering session
   * 
   * @author toni.gruetze
   * 
   */
  private class DescriptionIterator implements Iterator<Pair<Integer, String>>, Closeable {
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private final IProgress progress;
    protected int currentText = 0;
    protected int textCount = 0;
    private boolean hasNextElement;
    private int nextClusterID = -1;
    private String nextText = null;

    /**
     * creates a new {@link DescriptionIterator}
     * 
     * @param con the connection to be used
     * @param stmt
     * @param rs
     * @param progress
     */
    protected DescriptionIterator(final Session parentSession, final IProgress progress)
        throws SQLException {
      super();
      this.progress = progress;
      this.initProgress(parentSession);
    }

    @SuppressWarnings("synthetic-access")
    private void initProgress(final Session parentSession) throws SQLException {
      this.progress.startProgress("analyzing descriptions for cluster session");

      DataSource source = parentSession.getDataSource();
      this.con = LabelLoader.super.newConnection(source);

      this.stmt =
          this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      String userView = parentSession.getDataSource().getUserView();
      if (userView == null) {
        userView = defaultUserView;
      }

      final String stmtString =
          LabelLoader.this.resourceReader.getUndocumentedStringFromResource(this.getClass()
              .getPackage(), "sql/?" + LabelLoader.super.getDataSourceType(source)
              + "/?selectDescriptions.sql", Integer.valueOf(parentSession.getId()), Integer
              .valueOf(maxDescriptions), userView);
      this.rs = this.stmt.executeQuery(stmtString);

      this.rs.last();
      this.textCount = this.rs.getRow();
      this.rs.beforeFirst();

      this.rs.setFetchSize(LabelLoader.this.fetchRowSize);

      this.progress.startProgress("analyzing descriptions for cluster session", this.textCount);

      this.hasNextElement = this.textCount > 0;
      this.peekNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return this.hasNextElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Pair<Integer, String> next() throws NoSuchElementException {

      if (this.currentText % 20000 == 0) {
        this.progress.continueProgressAt(this.currentText);
      }
      this.currentText++;

      Pair<Integer, String> result = null;
      try {
        if (this.hasNextElement) {
          result = new Pair<Integer, String>(Integer.valueOf(this.nextClusterID), this.nextText);
        } else {
          throw new NoSuchElementException();
        }

        this.peekNext();
      } catch (SQLException e) {
        LabelLoader.this.logger.error("Unable to get cluster description data from DB!", e);
      }
      return result;
    }

    private void peekNext() throws SQLException {
      // if there is sth.else
      if (this.hasNextElement) {
        // get next element
        this.hasNextElement = this.rs.next();
        if (this.hasNextElement) {
          this.nextClusterID = this.rs.getInt("CLUSTER_ID");
          this.nextText = this.rs.getString("TEXT");
        } else {
          this.nextClusterID = -1;
          this.nextText = null;
          this.close();
        }
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Unable to remove description from  read only query!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
      this.progress.stopProgress();
      try {
        if (this.con != null) {
          this.con.close();
        }
        if (this.stmt != null) {
          this.stmt.close();
        }
        if (this.rs != null) {
          this.rs.close();
        }
      } catch (SQLException e) {
        // ignore Errors while closing
        LabelLoader.this.logger.warn(DescriptionIterator.class.getName()
            + "#iterateClusterDescriptions()", e);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader#
   * iterateClusterDescriptions(de.hpi.fgis.ldp.server.datastructures.Session,
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public IClusterDescriptions iterateClusterDescriptions(final Session parentSession,
      final IProgress progress) throws SQLException {

    final DescriptionIterator iterator = new DescriptionIterator(parentSession, progress);

    return new IClusterDescriptions() {
      boolean alreadyUsed = false;

      @Override
      public Iterator<Pair<Integer, String>> iterator() {
        if (!this.alreadyUsed) {
          this.alreadyUsed = true;
          return iterator;
        }
        throw new UnsupportedOperationException(
            "Unable to reinstantiate the description iterator! Please create a new Iterable instance!");
      }

      @Override
      public int size() {
        return iterator.textCount;
      }

      @Override
      public void close() {
        iterator.close();
      }

    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.hpi.fgis.ldp.server.persistency.loading.ILabelLoader#getStopWords(
   * de.hpi.fgis.ldp.server.util.progress.IProgress)
   */
  @Override
  public Set<String> getStopWords(IProgress progress) throws SQLException {
    final HashSet<String> stopWords = new HashSet<String>();

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      progress.startProgress("loading stop words");
      con = super.newConnection(this.mainSchemaSource);
      stmt = con.createStatement();

      // FIXME move STOPWORDS Table to default schema
      final String stmtString =
          this.resourceReader.getUndocumentedStringFromResource(this.getClass().getPackage(),
              "sql/?" + super.getDataSourceType(this.mainSchemaSource) + "/?selectStopWords.sql",
              "prolod_main");
      rs = stmt.executeQuery(stmtString);
      // TODO inject
      rs.setFetchSize(this.fetchRowSize);

      while (rs.next()) {
        stopWords.add(rs.getString("TOKEN"));
      }
    } catch (SQLException e) {
      this.logger.error("Unable to get stop words from DB!", e);
      throw e;
    } finally {
      progress.stopProgress();
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
        this.logger.warn(this.getClass().getName() + "#getStopWords()", e);
      }
    }

    return stopWords;
  }

}
