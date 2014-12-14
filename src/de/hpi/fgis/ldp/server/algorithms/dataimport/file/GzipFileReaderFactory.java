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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes.Tuple;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * represents a tuple reader factory for GZipped data
 * 
 * @author toni.gruetze
 */
public class GzipFileReaderFactory implements TupleReaderFactory {

  @Inject
  private Log logger;

  private TupleReaderFactory innerReaderFactory;

  protected GzipFileReaderFactory() {
    // hide default constructor for injection
  }

  /**
   * this method has to be called to set the inner file reader
   * 
   * @param innerReaderFactory the factory for the inner file reader
   * @return the instance of this {@link GzipFileReaderFactory}
   */
  public GzipFileReaderFactory setUnzippedReader(TupleReaderFactory innerReaderFactory) {
    this.innerReaderFactory = innerReaderFactory;
    return this;
  }

  private GZIPInputStream openGZIPInputStream(final InputStream initializedStream)
      throws IOException {
    // open unzip stream
    return new GZIPInputStream(initializedStream);
  }

  @Override
  public Iterator<Tuple> build(final InputStream dataStream, final long unzippedStreamSize,
      final IProgress progress) {
    try {
      GZIPInputStream unzippedStream = openGZIPInputStream(dataStream);

      return GzipFileReaderFactory.this.innerReaderFactory.build(unzippedStream,
          unzippedStreamSize, progress);
    } catch (IOException e) {
      // log error
      if (logger != null) {
        logger.error("Unable to read BZipped file!", e);
      }
      throw new IllegalStateException("Unable to access dataStream!", e);
    }
  }

  @Override
  public Iterable<Tuple> build(final String filename, final IProgress progress) {
    final long filesize = new File(filename).length();

    return new Iterable<Tuple>() {
      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Iterable#iterator()
       */
      @Override
      public Iterator<Tuple> iterator() {
        try {
          // estimate the unzipped file size: 25 is the average
          // compression rate
          // this has to be done because reading the file and
          // determining the size is a very slow alternative
          long unzippedStreamSize = filesize * 25;

          return GzipFileReaderFactory.this.build(new FileInputStream(filename),
              unzippedStreamSize, progress);
        } catch (FileNotFoundException e) {
          throw new IllegalStateException("Unable to open file \"" + filename + "\"", e);
        }
      }
    };
  }

}
