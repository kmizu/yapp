/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.runtime;

/**
 * This class represents a location in a source file.
 * line and column are both 1 origin.
 * @author Kouta Mizushima
 *
 */
public class Location {
  private final int line;
  private final int column;
  
  /**
   * Creates an object which represents location in a source file.
   * @param line line number (>= 1)
   * @param column column number (>= 1)
   */
  public Location(int line, int column) {
    this.line = line;
    this.column = column;
  }
  
  /**
   * Gets line number.
   * @return line number
   */
  public int getLine() {
    return line;
  }
  
  /**
   * Gets column number.
   * @return column number
   */  
  public int getColumn() {
    return column;
  }
  
  @Override
  public String toString() {
    return line + ":" + column;
  }
}
