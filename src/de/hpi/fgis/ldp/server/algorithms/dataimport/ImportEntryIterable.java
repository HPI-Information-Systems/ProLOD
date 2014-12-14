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

package de.hpi.fgis.ldp.server.algorithms.dataimport;

import java.util.Iterator;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes.Tuple;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.Bz2FileReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.GzipFileReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.NTFileParser;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.TupleReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.patternstatistics.PatternAnalyzer;
import de.hpi.fgis.ldp.server.datastructures.ImportTuple;
import de.hpi.fgis.ldp.server.util.progress.IProgress;
import de.hpi.fgis.ldp.shared.config.dataimport.FileType;
import de.hpi.fgis.ldp.shared.data.Datatype;
import de.hpi.fgis.ldp.shared.data.Pair;

public class ImportEntryIterable implements Iterable<ImportTuple> {
  private FileType fileType = null;
  private String path;
  private IProgress progress;
  private final Provider<ImportEntryIterator> iteratorProvider;

  @Inject
  protected ImportEntryIterable(Provider<ImportEntryIterator> iteratorProvider) {
    this.iteratorProvider = iteratorProvider;
  }

  /**
   * gets the path
   * 
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * sets the path
   * 
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * gets the file type
   * 
   * @return the file type
   */
  public FileType getFileType() {
    return fileType;
  }

  /**
   * sets the file type
   * 
   * @param fileType the file type
   */
  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  /**
   * gets the progress
   * 
   * @return the progress
   */
  public IProgress getProgress() {
    return progress;
  }

  /**
   * sets the progress
   * 
   * @param progress the progress to set
   */
  public void setProgress(IProgress progress) {
    this.progress = progress;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<ImportTuple> iterator() {
    final ImportEntryIterator iterator = this.iteratorProvider.get();
    if (path == null) {
      throw new IllegalStateException("Unable to initialize tuple iterator without file name!");
    }

    // if no file type is selected, check the file ending
    if (this.fileType == null) {
      this.setFileType(FileType.getType(path));
    }
    iterator.initParser(this.getPath(), this.getFileType(), this.getProgress());
    return iterator;
  }

  /**
   * enables the iteration of {@link ImportEntry} instances from data file
   * 
   * @author toni.gruetze
   */
  protected static class ImportEntryIterator implements Iterator<ImportTuple> {
    // TODO inject via setter
    @Inject
    private Log logger;

    private final Provider<NTFileParser.Factory> ntReaderProvider;
    private final Provider<Bz2FileReaderFactory> bz2ReaderProvider;
    private final Provider<GzipFileReaderFactory> gzReaderProvider;

    private Iterator<Tuple> parser;
    private final PatternAnalyzer ananlyzer;

    @Inject
    protected ImportEntryIterator(PatternAnalyzer analyzer,
        Provider<NTFileParser.Factory> ntReaderProvider,
        Provider<Bz2FileReaderFactory> bz2ReaderProvider,
        Provider<GzipFileReaderFactory> gzReaderProvider) {
      this.ananlyzer = analyzer;
      this.ntReaderProvider = ntReaderProvider;
      this.bz2ReaderProvider = bz2ReaderProvider;
      this.gzReaderProvider = gzReaderProvider;
    }

    protected void initParser(final String path, FileType fileType, final IProgress progress) {
      TupleReaderFactory factory = null;
      // TODO inject factory
      switch (fileType) {
        case NT:
          factory = ntReaderProvider.get();
          break;
        case NT_BZ2:
          factory = bz2ReaderProvider.get().setUnzippedReader(ntReaderProvider.get());
          break;
        case NT_GZ:
          factory = gzReaderProvider.get().setUnzippedReader(ntReaderProvider.get());
          break;
        default:
          throw new IllegalStateException("Unknown file type\"" + fileType + "\"");
      }

      this.parser = factory.build(path, progress).iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return this.parser.hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public ImportTuple next() {
      final Tuple tuple = this.parser.next();
      if (tuple == null) {
        return null;
      }
      Pair<String, String> pattern = this.ananlyzer.getPattern(tuple.getObject());
      final ImportTuple newEntry = new ImportTuple();
      newEntry.setSubject(tuple.getSubject());
      newEntry.setPredicate(tuple.getPredicate());
      newEntry.setObject(tuple.getObject());
      newEntry.setNormPattern(pattern.getSecondElem());
      newEntry.setPattern(pattern.getFirstElem());
      newEntry.setDatatype(this.ananlyzer.getDatatype(newEntry.getPattern()));

      if (newEntry.getDatatype().equals(Datatype.INTEGER)
          || newEntry.getDatatype().equals(Datatype.DECIMAL)) {
        try {
          // special case: remove spaces e.g. between '-' and digits
          newEntry.setParsedValue(Double.parseDouble(newEntry.getObject().replaceAll(" |,", "")));
        } catch (NumberFormatException e) {
          logger.warn("Value couldn't be casted to a numeric value: " + newEntry.getObject()
              + " (suggested data type: " + newEntry.getDatatype().getLabel() + ").", e);
          logger.warn("The datatype was changed from " + newEntry.getDatatype().getLabel() + " to "
              + Datatype.STRING.getLabel() + ".");

          newEntry.setDatatype(Datatype.STRING);
          // insert length of C in this
          newEntry.setParsedValue(newEntry.getObject().length());
        }
      } else {
        // insert length of C in this
        newEntry.setParsedValue(newEntry.getObject().length());
      }

      return newEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      this.parser.remove();
    }

  }
}
