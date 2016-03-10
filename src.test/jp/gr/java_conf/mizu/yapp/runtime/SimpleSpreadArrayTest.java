/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.runtime;

import junit.framework.TestCase;

public class SimpleSpreadArrayTest extends TestCase {
  private SpreadArray<String> target;
  
  @Override
  protected void setUp() throws Exception {
    target = new SimpleSpreadArray<String>();
  }

  public void testAll() {
    assertEquals(0, target.size());
    target.set(0, "A");
    assertEquals("A", target.get(0));
    assertEquals(1, target.size());
    
    target.set(1, "B");
    assertEquals("A", target.get(0));
    assertEquals("B", target.get(1));
    assertEquals(2, target.size());
    
    target.set(4, "C");
    assertEquals("A", target.get(0));
    assertEquals("B", target.get(1));
    assertNull(target.get(2));
    assertNull(target.get(3));
    assertEquals("C", target.get(4));
    assertEquals(5, target.size());
    
    target.truncate(2);
    assertNull(target.get(0));
    assertNull(target.get(1));
    assertEquals("C", target.get(2));
    assertEquals(3, target.size());
    
    target.truncate(3);
    assertEquals(target.size(), 0);
  }
}
