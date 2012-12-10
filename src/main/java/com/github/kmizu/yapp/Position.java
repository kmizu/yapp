/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp;

/**
 * A position information in a source file.
 * @author Kota Mizushima
 *
 */
public class Position {
  private int line;
  private int column;
  
  /**
   * Creates a Position's instance.
   * @param line line number
   * @param column column number
   */
  public Position(int line, int column) {
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
