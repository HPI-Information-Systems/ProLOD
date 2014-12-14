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

package de.hpi.fgis.ldp.clienttest.util.test;

import org.junit.Assert;
import org.junit.Test;

import de.hpi.fgis.ldp.client.util.StringUtil;

public class StringUtilTest {

	@Test
	public void testShortenURL() {
		Assert.assertEquals("dbpedia:Albert_Einstein", StringUtil.getInstance().shortenURL("http://dbpedia.org/resource/Albert_Einstein"));
		Assert.assertEquals("dbpedia:Albert_Einstein", StringUtil.getInstance().shortenURL("http://dbpedia.org/resource/Albert_Einstein/"));
		Assert.assertEquals("fileformat:regex", StringUtil.getInstance().shortenURL("http://www.fileformat.info/tool/regex"));
		Assert.assertEquals("fileformat:regex", StringUtil.getInstance().shortenURL("http://www.fileformat.info/bla/tool/regex"));
		Assert.assertEquals("fileformat:regex", StringUtil.getInstance().shortenURL("http://www.fileformat.info/regex"));
		Assert.assertEquals("wiwiss.fu-berlin:brandName", StringUtil.getInstance().shortenURL("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/brandName"));
		
		Assert.assertEquals("das ist ein text/string", StringUtil.getInstance().shortenURL("das ist ein text/string"));
		Assert.assertEquals("http://www.web.de/", StringUtil.getInstance().shortenURL("http://www.web.de/"));
	}
	@Test
	public void testRemoveHTMLTags() {
		Assert.assertEquals("&lt;&quot;&amp;&#039;&gt;", StringUtil.getInstance().removeHTMLTags("<\"&'>"));
	}
}
