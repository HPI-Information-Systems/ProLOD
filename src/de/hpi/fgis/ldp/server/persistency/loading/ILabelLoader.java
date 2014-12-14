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

package de.hpi.fgis.ldp.server.persistency.loading;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Set;

import de.hpi.fgis.ldp.server.datastructures.Session;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Pair;

/**
 * The {@link ILabelLoader} is used to extract all possible descriptions for entities in a cluster
 * from database.
 * 
 * @author toni.gruetze
 * @author david.sonnabend
 * 
 */
public interface ILabelLoader {

  /**
   * a {@link Iterable} of cluster descriptions
   * 
   * @author toni.gruetze
   * 
   */
  public interface IClusterDescriptions extends Iterable<Pair<Integer, String>>, Closeable {
    /**
     * gets the number of cluster texts
     * 
     * @return the number of cluster texts
     */
    public int size();

    /*
     * (non-Javadoc)
     * 
     * @see java.io.Closeable#close()
     */
    @Override
    public void close();
  }

  /**
   * gets a {@link Iterable} with all detail texts of all clusters from the session
   * 
   * @param parentSession the parent session id to get descriptions for
   * @param progress the progress feedback instance
   * @return a {@link Iterable} with a {@link Pair} of cluster id and containing Entity description
   *         text.
   */
  public IClusterDescriptions iterateClusterDescriptions(final Session parentSession,
      final IProgress progress) throws SQLException;

  /**
   * Gets a set of stop words from database
   * 
   * @param progress the progress feedback instance
   * @return a set of stop words
   */
  public Set<String> getStopWords(final IProgress progress) throws SQLException;
}
