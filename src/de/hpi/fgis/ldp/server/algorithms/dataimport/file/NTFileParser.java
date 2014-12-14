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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;

import de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes.Tuple;
import de.hpi.fgis.ldp.server.util.progress.IProgress;

/**
 * Parser for NT files based on the grammar of http://www.w3.org/TR/rdf-testcases/#ntriples : <br/>
 * <table border="0" summary="ntriple ebnf">
 * <tbody>
 * <tr align="left">
 * <td><a name="ntripleDoc" id="ntripleDoc">ntripleDoc</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#line">line</a>*</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="line" id="line">line</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#ws">ws</a>* ( <a href="#comment">comment</a> | <a href="#triple">triple</a> )? <a
 * href="#eoln">eoln</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="comment" id="comment">comment</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>'#' ( <a href="#character">character</a> - ( <a href="#cr">cr</a> | <a href="#lf">lf</a> ) )*
 * </td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="triple" id="triple">triple</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#subject">subject</a> <a href="#ws">ws</a>+ <a href="#predicate">predicate</a> <a
 * href="#ws">ws</a>+ <a href="#object">object</a> <a href="#ws">ws</a>* '.' <a href="#ws">ws</a>*</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="subject" id="subject">subject</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#uriref">uriref</a> | <a href="#nodeID">nodeID</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="predicate" id="predicate">predicate</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#uriref">uriref</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="object" id="object">object</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#uriref">uriref</a> | <a href="#nodeID">nodeID</a> | <a href="#literal">literal</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="uriref" id="uriref">uriref</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>'&lt;' <a href="#absoluteURI">absoluteURI</a> '&gt;'</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="bNode" id="bNode"></a><a name="nodeID" id="nodeID">nodeID</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>'_:' <a href="#name">name</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="literal" id="literal">literal</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#langString">langString</a> | <a href="#datatypeString">datatypeString</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="langString" id="langString">langString</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>'"' <a href="#string">string</a> '"' ( '@' <a href="#language">language</a> )?</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="datatypeString" id="datatypeString">datatypeString</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>'"' <a href="#string">string</a> '"' '^^' <a href="#uriref">uriref</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left" valign="top">
 * <td><a name="language" id="language">language</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>[a-z]+ ('-' [a-z0-9]+ )*<br />
 * encoding a <a href=
 * "http://www.w3.org/TR/2004/REC-rdf-concepts-20040210/#dfn-language-identifier" >language tag</a>.
 * </td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="ws" id="ws">ws</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#space">space</a> | <a href="#tab">tab</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="eoln" id="eoln">eoln</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#cr">cr</a> | <a href="#lf">lf</a> | <a href="#cr">cr</a> <a href="#lf">lf</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="space" id="space">space</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>#x20 / * US-ASCII space - decimal 32 * /</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="cr" id="cr">cr</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>#xD / * US-ASCII carriage return - decimal 13 * /</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="lf" id="lf">lf</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>#xA / * US-ASCII line feed - decimal 10 * /</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="tab" id="tab">tab</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>#x9 / * US-ASCII horizontal tab - decimal 9 * /</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="string" id="string">string</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#character">character</a>* with escapes as defined in section <a
 * href="#ntrip_strings">Strings</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="name" id="name">name</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>[A-Za-z][A-Za-z0-9]*</td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="absoluteURI" id="absoluteURI">absoluteURI</a></td>
 * 
 * <td>::=</td>
 * 
 * <td><a href="#character">character</a>+ with escapes as defined in section <a
 * href="#sec-uri-encoding">URI References</a></td>
 * 
 * <td>
 * </td>
 * </tr>
 * 
 * <tr align="left">
 * <td><a name="character" id="character">character</a></td>
 * 
 * <td>::=</td>
 * 
 * <td>[#x20-#x7E] / * US-ASCII <a href="#space">space</a> to decimal 126 * /</td>
 * 
 * <td>
 * </td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author toni.gruetze
 */
public class NTFileParser implements Iterator<Tuple> {
  private final Object syncToken = new Object();

  private Log logger;

  // (simplified) grammar
  private final static String WHITESPACE = "[ \\t]";
  private final static String POSSIBLE_SPACES = WHITESPACE + "*";
  private final static String NEEDED_SPACES = WHITESPACE + "+";

  private final static String LANGUAGE = "@[a-z][\\-a-z0-9]*";
  private final static String DATATYPE = "\\^\\^<[^<>]+>";

  private final static String URIREF_DATA = "<([^<> \\t]+)>";
  private final static String NODEID_DATA = "_:([A-Za-z][A-Za-z0-9]*)";
  private final static String LITERAL_DATA = "\"(.*)\"(" + LANGUAGE + "|" + DATATYPE + ")?";

  private final static String SUBJECT = "(" + URIREF_DATA + "|" + NODEID_DATA + ")";
  private final static String PREDICATE = "(" + URIREF_DATA + ")";
  private final static String OBJECT = "(" + URIREF_DATA + "|" + NODEID_DATA + "|" + LITERAL_DATA
      + ")";
  private final static String OPTIONAL_CONTEXT = "(" + NEEDED_SPACES + "(" + URIREF_DATA + "|"
      + NODEID_DATA + "|" + LITERAL_DATA + "))?";

  private final static String TUPLE = POSSIBLE_SPACES + SUBJECT + NEEDED_SPACES + PREDICATE
      + NEEDED_SPACES + OBJECT + OPTIONAL_CONTEXT + POSSIBLE_SPACES + "\\." + POSSIBLE_SPACES;
  // @Deprecated
  // private final static String TRIPLE2 = POSSIBLE_SPACES + SUBJECT +
  // NEEDED_SPACES + PREDICATE
  // + NEEDED_SPACES + OBJECT + POSSIBLE_SPACES + "\\." + POSSIBLE_SPACES;
  private final static String BLANKLINE = POSSIBLE_SPACES;
  private final static String COMMENT = POSSIBLE_SPACES + "#[^\\n\\r]*";

  @SuppressWarnings("unused")
  // might be useful in the future
  private final static String LINE = "^" + TUPLE + "|" + BLANKLINE + "|" + COMMENT + "$";
  // private final static String LINE = "^" + TRIPLE + "|" + BLANKLINE + "|" +
  // COMMENT + "$";

  // private final Pattern pattern = Pattern.compile(TRIPLE);
  private final Pattern pattern = Pattern.compile(TUPLE);
  private BufferedReader reader;
  private final IProgress progress;
  private long filePosition;
  private long lineNumber = 0;
  private String nextLine;
  private final boolean logProgress;

  /**
   * creates a new instance based on the given stream
   * 
   * @param dataStream the data stream on which this instance will be based on
   * @param streamSize the size of the stream
   * @param progress the progress feedback instance
   */
  protected NTFileParser(final InputStream dataStream, final long streamSize,
      final IProgress progress) {
    this.reader = new BufferedReader(new InputStreamReader(dataStream));
    this.progress = progress;
    this.logProgress = streamSize > 0;
    progress.startProgress("Reading N-Tupel source file.", streamSize);
    this.readNextLine();
  }

  protected NTFileParser setLogger(Log logger) {
    this.logger = logger;
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    synchronized (this.syncToken) {
      return this.nextLine != null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  @Override
  public Tuple next() throws NoSuchElementException {
    String currentLine = null;
    synchronized (this.syncToken) {
      if (this.nextLine == null) {
        throw new NoSuchElementException();
      }
      currentLine = this.nextLine;

      if (this.logProgress) {
        // progress feedback
        if (this.lineNumber % 50000 == 0) {
          this.progress.continueProgressAt(this.filePosition);
        }

        this.filePosition += currentLine.length() + 2; // zzgl. \n
        this.lineNumber++;
      }
      this.readNextLine();
    }

    Matcher m = this.pattern.matcher(currentLine);
    Tuple result = null;

    if (m.matches()) {
      result = new Tuple();
      // get subject value
      // 2 -> subject URI
      result.setSubject(m.group(2));
      // 3 -> object NodeID
      if (result.getSubject() == null) {
        result.setSubject(m.group(3));
      }

      // get subject value
      // 5 -> predicate URI
      result.setPredicate(m.group(5));

      // 7 -> object URI
      result.setObject(m.group(7));
      // 8 -> object NodeID
      if (result.getObject() == null) {
        result.setObject(m.group(8));
      }
      // 9 -> object Literal
      if (result.getObject() == null) {
        result.setObject(m.group(9).replaceAll("\\\\\"", "\""));
      }

      // ignore context (optional)
      // 13 -> context URI
      // 14 -> object NodeID
      // 15 -> object Literal
    } else {
      if (logger != null) {
        logger.info("Ignoring the following line with insufficient triple information: "
            + currentLine);
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    throw new IllegalStateException();
  }

  /**
   * reads the next line
   */
  protected void readNextLine() {
    synchronized (this.syncToken) {
      if (this.reader != null) {
        try {
          this.nextLine = this.reader.readLine();
        } catch (IOException e) {
          if (logger != null) {
            logger.warn("Unable to read file completely!", e);
          }

          this.nextLine = null;
          try {
            this.reader.close();
          } catch (IOException e1) {
            // ignore errors while closing
          }
          this.reader = null;
        }
      }
      if (this.nextLine == null) {
        this.progress.stopProgress();
      }
    }
  }

  /**
   * creates a new instance based on the given file
   * 
   * @author toni.gruetze
   */
  public static class Factory implements TupleReaderFactory {
    @Inject
    protected Log logger;

    protected Factory() {
      // hide default constructor for injection
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.algorithms.dataimport.file.TupleReaderFactory
     * #build(java.io.InputStream, long, de.hpi.fgis.ldp.server.util.progress.IProgress)
     */
    @Override
    public Iterator<Tuple> build(final InputStream dataStream, final long streamSize,
        final IProgress progress) {
      // attention! we are unable to use the stream a second time
      return new NTFileParser(dataStream, streamSize, progress).setLogger(logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hpi.fgis.ldp.server.algorithms.dataimport.file.TupleReaderFactory
     * #build(java.lang.String, de.hpi.fgis.ldp.server.util.progress.IProgress)
     */
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
            return Factory.this.build(new FileInputStream(filename), filesize, progress);
          } catch (FileNotFoundException e) {
            // log error
            if (logger != null) {
              logger.error("Unable to open file \"" + filename + "\"", e);
            }
            throw new IllegalStateException("Unable to open file \"" + filename + "\"", e);
          }
        }
      };
    }
  }

}
