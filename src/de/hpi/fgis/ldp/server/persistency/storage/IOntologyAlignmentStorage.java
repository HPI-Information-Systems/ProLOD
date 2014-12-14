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

package de.hpi.fgis.ldp.server.persistency.storage;

import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.SQLException;

import de.hpi.fgis.ldp.server.algorithms.ontologyAligment.TaxonomyManager;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.data.DataSource;

/**
 * enables the creation of data base schemata
 * 
 * @author toni.gruetze
 * 
 */
public interface IOntologyAlignmentStorage {

  /**
   * 
   * @param source
   * @param label
   * @param progress
   * @return
   * @throws SQLException
   */
  public void createAlignment(final DataSource source, TaxonomyManager tm, final IProgress progress)
      throws SQLException;

  public void writeOntologyChanges(DataSource source, TaxonomyManager tm, IProgress progress,
      TIntIntHashMap predicateToSourceMap);

}
