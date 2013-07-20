/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

/**
 * A position information in a source file.
 * @author Kota Mizushima
 *
 */
class Position {
  /**
   * Creates a Position's instance.
   * @param line line number
   * @param column column number
   */
  def this(line: Int, column: Int) {
    this()
    this.line = line
    this.column = column
  }

  /**
   * Gets line number.
   * @return line number
   */
  def getLine: Int = {
    return line
  }

  /**
   * Gets column number.
   * @return column number
   */
  def getColumn: Int = {
    return column
  }

  override def toString: String = {
    return line + ":" + column
  }

  private var line: Int = 0
  private var column: Int = 0
}
