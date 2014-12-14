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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.algorithms.dataimport.datatypes.Tuple;
import de.hpi.fgis.ldp.server.algorithms.dataimport.file.NTFileParser;
import de.hpi.fgis.ldp.server.util.progress.CMDProgress;

@SuppressWarnings("deprecation")
public class NTFileParserTest {
  protected Iterator<Tuple> parser;

  @Before
  public void setUp() {
    final InputStream resStream = NTFileParserTest.class.getResourceAsStream("./test.data");

    this.parser = new NTFileParser.Factory() {}.build(resStream, -1, CMDProgress.getInstance());
  }

  @After
  public void tearDown() {
    this.parser = null;
  }

  @Test
  public void testNext() {
    Assert.assertArrayEquals(new String[] {"http://www.w3.org/2001/08/rdf-test/",
        "http://purl.org/dc/elements/1.1/creator", "Dave Beckett"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://www.w3.org/2001/08/rdf-test/",
        "http://purl.org/dc/elements/1.1/creator", "Jan Grant"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://www.w3.org/2001/08/rdf-test/",
        "http://purl.org/dc/elements/1.1/publisher", "a"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"a", "http://purl.org/dc/elements/1.1/title",
        "World Wide Web Consortium"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"a", "http://purl.org/dc/elements/1.1/source",
        "http://www.w3.org/"}, this.parser.next().toArray());
    Assert.assertNull(this.parser.next());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/name", "!!!(CHK CHK CHK)"}, this.parser.next().toArray());
    Assert.assertNull(this.parser.next());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/img", "Chkchkchk.jpg"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/landscape", "yes"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/background", "group_or_band"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/origin",
        "http://dbpedia.org/resource/Sacramento%2C_California"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/origin", "http://dbpedia.org/resource/California"},
        this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/yearsActive", "1996\\u2013Present"}, this.parser.next()
        .toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/wikiPageUsesTemplate",
        "http://dbpedia.org/resource/Template:infobox_musical_artist"}, this.parser.next()
        .toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/date", "2009-06"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/wikiPageUsesTemplate",
        "http://dbpedia.org/resource/Template:expand-section"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/id", "f26c72d3-e52c-467b-b651-679c73d8e1a7"}, this.parser
        .next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/name", "!!!"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21",
        "http://dbpedia.org/property/wikiPageUsesTemplate",
        "http://dbpedia.org/resource/Template:musicbrainz_artist"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/name", "!!!Fuck You!!!"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/type", "EP"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/artist", "http://dbpedia.org/resource/Overkill_%28band%29"},
        this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/cover", "OverkillFUoriginal.jpg"}, this.parser.next()
        .toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/released", "1987"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21Fuck_You%21%21%21",
        "http://dbpedia.org/property/genre", "http://dbpedia.org/resource/Thrash_metal"},
        this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/title", "The Step"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/length", "6:08"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/title", "Hammerhead"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/length", "5:04"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/title", "KooKooKa Fuk-U"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist1",
        "http://dbpedia.org/property/length", "7:26"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist3",
        "http://dbpedia.org/property/title", "There's No Fucking Rules, Dude"}, this.parser.next()
        .toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist3",
        "http://dbpedia.org/property/length", "8:49"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist3",
        "http://dbpedia.org/property/wikiPageUsesTemplate",
        "http://dbpedia.org/resource/Template:tracklist"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21_%28album%29",
        "http://dbpedia.org/property/relatedInstance",
        "http://dbpedia.org/resource/%21%21%21_%28album%29/tracklist3"}, this.parser.next()
        .toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%21%21%21_%28album%29",
        "http://dbpedia.org/property/id", "1b105601-d2d3-4da2-a7d9-114f981b1766"}, this.parser
        .next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/%22C%22_Is_for_Corpse",
        "http://dbpedia.org/property/name", "\"C\" Is for Corpse"}, this.parser.next().toArray());
    Assert.assertArrayEquals(new String[] {"http://dbpedia.org/resource/Aston_Martin_Vanquish",
        "http://dbpedia.org/property/power", "520.0"}, this.parser.next().toArray());

    Assert.assertFalse(this.parser.hasNext());
    try {
      this.parser.next();
      Assert.fail("NoSuchElementException expected!");
    } catch (NoSuchElementException e) {
      // nothing to do
    }
  }
}
