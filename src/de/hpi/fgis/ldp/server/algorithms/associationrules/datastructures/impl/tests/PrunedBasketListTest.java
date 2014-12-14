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
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.PrunedBasketList;
import de.hpi.fgis.ldp.server.datastructures.impl.EntitySchemaList;
import de.hpi.fgis.ldp.server.datastructures.impl.PrunedEntitySchemaList;

/**
 * Test for the {@link PrunedEntitySchemaList}
 * 
 * 
 */
public class PrunedBasketListTest {
  private BasketList originalList;
  private PrunedBasketList list;

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

    this.originalList = new BasketList(entityList);

    int[] basketIDs = new int[] {1, 2};

    this.list = new PrunedBasketList(this.originalList, basketIDs);
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
  public void testPrunedBasketList() {
    int[] basketIDs = new int[] {4, 2, 0};

    try {
      this.list = new PrunedBasketList(this.originalList, basketIDs);
      Assert.fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException ex) {
      // nothing to do
    }
    try {
      this.list = new PrunedBasketList(null, basketIDs);
      Assert.fail("NullPointerException expected");
    } catch (NullPointerException ex) {
      // nothing to do
    }
    try {
      this.list = new PrunedBasketList(this.originalList, null);
      Assert.fail("NullPointerException expected");
    } catch (NullPointerException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetBaskedID() {
    Assert.assertEquals(2, this.list.getBasketID(0));
    Assert.assertEquals(3, this.list.getBasketID(1));

    try {
      this.list.getBasketID(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getBasketID(4);
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
    Assert.assertEquals(2, this.list.getBasketCount());
  }

  /**
   * test
   */
  @Test
  public void testGetItems() {
    Assert.assertArrayEquals(new int[] {12, 45, 74}, this.list.getItems(0));
    Assert.assertArrayEquals(new int[] {12, 74}, this.list.getItems(1));

    try {
      this.list.getItems(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getItems(4);
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

    IBasketList newList = this.list.prune();

    Assert.assertEquals(1, newList.getBasketCount());

    Assert.assertEquals(3, newList.getBasketID(0));

    Assert.assertArrayEquals(new int[] {12, 74}, newList.getItems(0));
  }

  /**
   * test
   */
  @Test
  public void testSetUsedBasket() {
    try {
      this.list.setUsedBasket(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.setUsedBasket(4);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
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

    Assert.assertTrue(this.list.containsAll(1, new int[] {12, 74}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {12}));
    Assert.assertTrue(this.list.containsAll(1, new int[] {74}));

    Assert.assertFalse(this.list.containsAll(1, new int[] {45}));
    Assert.assertFalse(this.list.containsAll(1, new int[] {63}));

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
