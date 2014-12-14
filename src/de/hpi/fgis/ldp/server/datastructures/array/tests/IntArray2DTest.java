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

package de.hpi.fgis.ldp.server.datastructures.array.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hpi.fgis.ldp.server.datastructures.array.IntArray2D;

/**
 * IntArray2D test class
 * 
 */
public class IntArray2DTest {
  private IntArray2D array;

  /**
   * setUp
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    this.array = new IntArray2D(23, 15, -1);
  }

  /**
   * tearDown
   * 
   * @throws Exception
   */
  @After
  public void tearDown() {
    this.array = null;
  }

  /**
   * test
   */
  @Test
  public void testIntArray2D() {
    try {
      this.array = new IntArray2D(-1, 15, -1);
      Assert.fail("Illegal Argument accepted!");
    } catch (IllegalArgumentException e) {
      // nothing to do
    }
    try {
      this.array = new IntArray2D(23, -1, -1);
      Assert.fail("Illegal Argument accepted!");
    } catch (IllegalArgumentException e) {
      // nothing to do
    }
  }

  /**
   * test
   */
  @Test
  public void testGetValue() {
    // ToDo index out of bounds

    Assert.assertEquals(-1, this.array.getValue(0, 0));
    Assert.assertEquals(-1, this.array.getValue(4, 3));
    Assert.assertEquals(-1, this.array.getValue(22, 14));
  }

  /**
   * test
   */
  @Test
  public void testSetValue() {
    // ToDo index out of bounds
    this.array.setValue(4, 3, 1);
    this.array.setValue(22, 1, 2);
    this.array.setValue(13, 14, 3);
    Assert.assertEquals(1, this.array.getValue(4, 3));
    Assert.assertEquals(2, this.array.getValue(22, 1));
    Assert.assertEquals(3, this.array.getValue(13, 14));

    this.array.setValue(4, 3, 0);
    Assert.assertEquals(0, this.array.getValue(4, 3));
  }

  /**
   * test
   */
  @Test
  public void testSizeOfDim1() {
    Assert.assertEquals(23, this.array.sizeOfDim1());
  }

  /**
   * test
   */
  @Test
  public void testSizeOfDim2() {
    Assert.assertEquals(15, this.array.sizeOfDim2());
  }
}
