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

package de.hpi.fgis.ldp.server.algorithms.dataimport.file.tests;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.algorithms.dataimport.file.GzipFileReaderFactory;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.NTFileParser;
import de.hpi.fgis.ldp.server.util.progress.CMDProgress;

@SuppressWarnings("deprecation")
public class GzipFileReaderTest {
  private NTFileParserTest ntFileTest;

  @Before
  public void setUp() {
    ntFileTest = new NTFileParserTest();
    final InputStream resStream = GzipFileReaderTest.class.getResourceAsStream("./test.data.gz");

    ntFileTest.parser =
        new GzipFileReaderFactory() {}.setUnzippedReader(new NTFileParser.Factory() {}).build(
            resStream, -1, CMDProgress.getInstance());
  }

  @After
  public void tearDown() {
    ntFileTest.parser = null;
    ntFileTest = null;
  }

  @Test
  public void testNext() {
    ntFileTest.testNext();
  }
}
