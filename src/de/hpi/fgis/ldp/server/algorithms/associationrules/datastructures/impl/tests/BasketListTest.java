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

package de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IBasketList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.BasketList;
import de.hpi.fgis.ldp.server.datastructures.impl.EntitySchemaList;

/**
 * Test for the {@link EntitySchemaList}
 * 
 */
public class BasketListTest {
  private BasketList list;

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

    EntitySchemaList entityList =
        EntitySchemaList.Factory.getInstance().newInstance(subjectIDs, predicateIDs);

    this.list = new BasketList(entityList);
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
  public void testGetBaskedID() {
    Assert.assertEquals(1, this.list.getBasketID(0));
    Assert.assertEquals(2, this.list.getBasketID(1));
    Assert.assertEquals(3, this.list.getBasketID(2));

    try {
      this.list.getBasketID(3);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getBasketID(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetBasketCount() {
    Assert.assertEquals(3, this.list.getBasketCount());
  }

  /**
   * test
   */
  @Test
  public void testGetItems() {
    Assert.assertArrayEquals(new int[] {12, 12, 45, 45, 74}, this.list.getItems(0));
    Assert.assertArrayEquals(new int[] {12, 45, 74}, this.list.getItems(1));
    Assert.assertArrayEquals(new int[] {12, 74}, this.list.getItems(2));

    try {
      this.list.getItems(3);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getItems(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testPrune() {
    this.list.setUsedBasket(1);
    this.list.setUsedBasket(2);

    IBasketList newList = this.list.prune();

    Assert.assertEquals(2, newList.getBasketCount());

    Assert.assertEquals(2, newList.getBasketID(0));
    Assert.assertEquals(3, newList.getBasketID(1));

    Assert.assertArrayEquals(new int[] {12, 45, 74}, newList.getItems(0));
    Assert.assertArrayEquals(new int[] {12, 74}, newList.getItems(1));
  }

  /**
   * test
   */
  @Test
  public void testContainsAll() {
    Assert.assertTrue(this.list.containsAll(0, new int[] {12, 45, 74}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {12, 45}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {12, 74}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {45, 74}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {12}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {45}));
    Assert.assertTrue(this.list.containsAll(0, new int[] {74}));

    Assert.assertFalse(this.list.containsAll(0, new int[] {63}));

    Assert.assertTrue(this.list.containsAll(1, new int[] {12, 45, 74}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {12, 45}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {12, 74}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {45, 74}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {12}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {45}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {74}));

    Assert.assertFalse(this.list.containsAll(1, new int[] {63}));

    Assert.assertTrue(this.list.containsAll(2, new int[] {12, 74}));
    Assert.assertTrue(this.list.containsAll(2, new int[] {12}));
    Assert.assertTrue(this.list.containsAll(2, new int[] {74}));

    Assert.assertFalse(this.list.containsAll(2, new int[] {45}));
    Assert.assertFalse(this.list.containsAll(2, new int[] {63}));

    try {
      this.list.containsAll(3, new int[] {});
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.containsAll(-1, new int[] {});
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }
}
