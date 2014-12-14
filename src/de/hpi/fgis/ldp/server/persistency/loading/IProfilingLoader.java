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

import java.sql.SQLException;
import java.util.ArrayList;

import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.Cluster;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.NormalizedPattern;
import de.hpi.fgis.ldp.shared.data.Pattern;
import de.hpi.fgis.ldp.shared.data.Predicate;
import de.hpi.fgis.ldp.shared.data.table.IDataTable;

/**
 * Class to Analyze a set of subjects and create object-based statistics.
 * 
 * @author dandy.fenz
 * @author toni.gruetze
 * 
 */
public interface IProfilingLoader {

  /**
   * Returns the ratio between internal-, external links and literals in the current cluster.
   * 
   * @param progress the progress feedback instance
   * @return the ratio between internal-, external links and literals in the current cluster
   */
  public abstract IDataTable getLinkLiteralRatio(final IProgress progress) throws SQLException;

  /**
   * Returns a sorted list of properties in the cluster
   * 
   * @param from the start offset of the elements to get
   * @param to the end index of the elements to get (inclusive)
   * @param progress the progress feedback instance
   * @return a sorted list of properties
   * 
   * 
   * 
   * @return a table for the properties
   */
  public abstract IDataTable getClusterProperties(int from, int to, final IProgress progress)
      throws SQLException;

  /**
   * Returns a map of all existing data types and their number of occurrence within the given
   * property starting with the most often occurred data type.
   * 
   * @param predicates
   * @param progress the progress feedback instance
   * @return a table with the data type information
   */
  public abstract IDataTable getDatatypeRatio(final ArrayList<Predicate> predicates,
      final IProgress progress) throws SQLException;

  /**
   * Returns a map of all normalized patterns and their number of occurrence within the given
   * property starting with the most often occurred normalized pattern.
   * 
   * @param predicates
   * 
   * @param from the start offset of the elements to get
   * @param to the end index of the elements to get (inclusive)
   * @param progress the progress feedback instance
   * @return a table with the pattern
   */
  public abstract IDataTable getNormalizedPattern(ArrayList<Predicate> predicates, int from,
      int to, final IProgress progress) throws SQLException;

  /**
   * Returns a table of patterns
   * 
   * @param propertyID
   * @param datatypeID
   * @param normalizedPattern
   * 
   * @param from the start offset of the elements to get
   * @param to the end index of the elements to get (inclusive)
   * @param progress the progress feedback instance
   * @return a table with the pattern
   */
  public abstract IDataTable getPattern(ArrayList<Predicate> predicates, Datatype datatype,
      NormalizedPattern normalizedPattern, int from, int to, final IProgress progress)
      throws SQLException;

  /**
   * gets a table of objects for the specified predicate, datatype, pattern combination
   * 
   * @param predicates the predicates to be considered
   * @param datatype the datatype to be considered
   * @param pattern the pattern to be considered
   * @param from the start offset of the elements to get
   * @param to the end index of the elements to get (inclusive)
   * @param progress the progress feedback instance
   * @return a table of objects
   */
  public abstract IDataTable getObjects(ArrayList<Predicate> predicates, Datatype datatype,
      Pattern pattern, int from, int to, final IProgress progress) throws SQLException;

  /**
   * sets the constraints of this {@link IProfilingLoader} to analyze all subjects in a given
   * cluster filter
   * 
   * @param cluster the cluster to be analyzed
   * @param filter the filter condition
   */
  // TODO add some different combination of {@link DataElement} instances
  public abstract void setConstraints(Cluster cluster) throws SQLException;
}
