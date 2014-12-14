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

package de.hpi.fgis.ldp.server.algorithms.dataimport.file;

import java.io.InputStream;
import java.util.Iterator;

import de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes.Tuple;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * this interface provides the methods to access tuple files
 * 
 * @author toni.gruetze
 */
public interface TupleReaderFactory {

  /**
   * creates a new instance based on the given stream
   * 
   * @param dataStream the data to be used as data base
   * @param streamSize the size of the stream to be processed
   * 
   * @param progress the progress feedback instance
   */
  public Iterator<Tuple> build(final InputStream dataStream, final long streamSize,
      final IProgress progress);

  /**
   * creates a new instance based on the given file
   * 
   * @param filename the file to be used as data base
   * 
   * @param progress the progress feedback instance
   */
  public Iterable<Tuple> build(final String filename, final IProgress progress);
}
