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

package de.hpi.fgis.ldp.server.datastructures.impl.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.datastructures.impl.EntitySchemaList;

/**
 * Test for the {@link EntitySchemaList}
 * 
 */
public class EntitySchemaListTest {
  private EntitySchemaList list;

  /**
   * set up
   * 
   * @throws Exception
   */
  @Before
  public void setUp() {

    /*-
     * the data contains the following tuples
     * +-------+---------+
     * |subject|predicate|
     * +-------+---------+
     * |   3   |    74   |
     * |   1   |    12   |
     * |   1   |    74   |
     * |   1   |    45   |
     * |   2   |    74   |
     * |   2   |    45   |
     * |   2   |    12   |
     * |   3   |    12   |
     * |   1   |    12   |
     * |   1   |    45   |
     * +-------+---------+
     */
    final int[] subjectIDs = new int[] {3, 1, 1, 1, 2, 2, 2, 3, 1, 1};
    final int[] predicateIDs = new int[] {74, 12, 74, 45, 74, 45, 12, 12, 12, 45};

    this.list = EntitySchemaList.Factory.getInstance().newInstance(subjectIDs, predicateIDs);
  }

  /**
   * tear down
   * 
   * @throws Exception
   */
  @After
  public void tearDown() {
    this.list = null;
  }

  /**
   * test
   */
  @Test
  public void testGetEntityID() {
    assertEquals(1, this.list.getEntityID(0));
    assertEquals(2, this.list.getEntityID(1));
    assertEquals(3, this.list.getEntityID(2));

    try {
      this.list.getEntityID(3);
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getEntityID(-1);
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetEntityCount() {
    assertEquals(3, this.list.getEntityCount());
  }

  /**
   * test
   */
  @Test
  public void testGetSchema() {
    assertArrayEquals(new int[] {12, 12, 45, 45, 74}, this.list.getSchema(0));
    assertArrayEquals(new int[] {12, 45, 74}, this.list.getSchema(1));
    assertArrayEquals(new int[] {12, 74}, this.list.getSchema(2));

    try {
      this.list.getSchema(3);
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getSchema(-1);
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testContainsAll() {
    assertTrue(this.list.containsAll(0, new int[] {12, 45, 74}));
    assertTrue(this.list.containsAll(0, new int[] {12, 45}));
    assertTrue(this.list.containsAll(0, new int[] {12, 74}));
    assertTrue(this.list.containsAll(0, new int[] {45, 74}));
    assertTrue(this.list.containsAll(0, new int[] {12}));
    assertTrue(this.list.containsAll(0, new int[] {45}));
    assertTrue(this.list.containsAll(0, new int[] {74}));

    assertFalse(this.list.containsAll(0, new int[] {63}));

    assertTrue(this.list.containsAll(1, new int[] {12, 45, 74}));
    assertTrue(this.list.containsAll(1, new int[] {12, 45}));
    assertTrue(this.list.containsAll(1, new int[] {12, 74}));
    assertTrue(this.list.containsAll(1, new int[] {45, 74}));
    assertTrue(this.list.containsAll(1, new int[] {12}));
    assertTrue(this.list.containsAll(1, new int[] {45}));
    assertTrue(this.list.containsAll(1, new int[] {74}));

    assertFalse(this.list.containsAll(1, new int[] {63}));

    assertTrue(this.list.containsAll(2, new int[] {12, 74}));
    assertTrue(this.list.containsAll(2, new int[] {12}));
    assertTrue(this.list.containsAll(2, new int[] {74}));

    assertFalse(this.list.containsAll(2, new int[] {45}));
    assertFalse(this.list.containsAll(2, new int[] {63}));

    try {
      this.list.containsAll(3, new int[] {});
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.containsAll(-1, new int[] {});
      fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }
}
