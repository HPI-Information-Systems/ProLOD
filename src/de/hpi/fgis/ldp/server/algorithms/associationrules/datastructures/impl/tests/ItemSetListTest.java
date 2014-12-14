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

import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.IItemSetList;
import de.hpi.fgis.ldp.server.algorithms.associationrules.datastructures.impl.ItemSetList;

/**
 * Test for the {@link ItemSetList}
 * 
 * 
 */
public class ItemSetListTest {
  private ItemSetList list;

  /**
   * set up
   * 
   * @throws Exception
   */
  @Before
  public void setUp() {
    this.list = new ItemSetList(3, 5);

    this.list.setItemSet(0, 1, 2, 3);
    this.list.setItemSet(1, 2, 3, 5);
    this.list.setItemSet(2, 3, 4, 5);
    this.list.setItemSet(3, 1, 2, 4);
    this.list.setItemSet(4, 1, 4, 5);
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
  public void testItemSetList() {
    try {
      new ItemSetList(-1, 1);
      Assert.fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException ex) {
      // nothing to do
    }
    try {
      new ItemSetList(1, -1);
      Assert.fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testSetItemSet() {
    try {
      this.list.setItemSet(-1, 1, 2, 3);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.setItemSet(15, 1, 2, 3);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.setItemSet(2, 1, 2);
      Assert.fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException ex) {
      // nothing to do
    }
    try {
      this.list.setItemSet(2, 1, 2, 3, 4);
      Assert.fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetItemSet() {
    Assert.assertArrayEquals(new int[] {1, 2, 3}, this.list.getItemSet(0));
    Assert.assertArrayEquals(new int[] {2, 3, 5}, this.list.getItemSet(1));
    Assert.assertArrayEquals(new int[] {3, 4, 5}, this.list.getItemSet(2));
    Assert.assertArrayEquals(new int[] {1, 2, 4}, this.list.getItemSet(3));
    Assert.assertArrayEquals(new int[] {1, 4, 5}, this.list.getItemSet(4));

    try {
      this.list.getItemSet(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getItemSet(15);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetItemSetCount() {
    Assert.assertEquals(5, this.list.getItemSetCount());
  }

  /**
   * test
   */
  @Test
  public void testGetItemSetSize() {
    Assert.assertEquals(3, this.list.getItemSetSize());
  }

  /**
   * test
   */
  @Test
  public void testGetSupport() {
    this.list.increaseFrequency(2);
    this.list.increaseFrequency(3);
    this.list.increaseFrequency(2);
    this.list.increaseFrequency(4);

    Assert.assertEquals(0, this.list.getFrequency(0));
    Assert.assertEquals(0, this.list.getFrequency(1));
    Assert.assertEquals(2, this.list.getFrequency(2));
    Assert.assertEquals(1, this.list.getFrequency(3));
    Assert.assertEquals(1, this.list.getFrequency(4));

    try {
      this.list.getFrequency(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.getFrequency(15);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testIncreaseSupport() {
    try {
      this.list.increaseFrequency(-1);
      Assert.fail("ArrayIndexOutOfBoundsException expected");
    } catch (ArrayIndexOutOfBoundsException ex) {
      // nothing to do
    }
    try {
      this.list.increaseFrequency(15);
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
    IItemSetList newList = this.list.prune(1);

    Assert.assertNull(newList);

    this.list.increaseFrequency(0);

    newList = this.list.prune(1);

    Assert.assertEquals(3, newList.getItemSetSize());
    Assert.assertEquals(1, newList.getItemSetCount());
    Assert.assertArrayEquals(new int[] {1, 2, 3}, newList.getItemSet(0));
    Assert.assertEquals(1, newList.getFrequency(0));

    this.list.increaseFrequency(0);
    this.list.increaseFrequency(1);
    this.list.increaseFrequency(3);

    newList = this.list.prune(1);

    Assert.assertEquals(3, newList.getItemSetSize());
    Assert.assertEquals(3, newList.getItemSetCount());
    Assert.assertArrayEquals(new int[] {1, 2, 3}, newList.getItemSet(0));
    Assert.assertArrayEquals(new int[] {2, 3, 5}, newList.getItemSet(1));
    Assert.assertArrayEquals(new int[] {1, 2, 4}, newList.getItemSet(2));

    Assert.assertEquals(2, newList.getFrequency(0));
    Assert.assertEquals(1, newList.getFrequency(1));
    Assert.assertEquals(1, newList.getFrequency(2));
  }

  /**
   * test
   */
  @Test
  public void testGenerateCandidates() {
    IItemSetList newList = this.list.generateCandidates();

    Assert.assertEquals(4, newList.getItemSetSize());
    Assert.assertEquals(1, newList.getItemSetCount());
    Assert.assertArrayEquals(new int[] {1, 2, 3, 4}, newList.getItemSet(0));
    // assertArrayEquals(new int[] {1,3,4,5}, newList.getItemSet(1));
  }
}
