/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.runtime

/**
 * This class represents a location in a source file.
 * line and column are both 1 origin.
 * @author Kouta Mizushima
 *
 */
class Location {
  /**
   * Creates an object which represents location in a source file.
   * @param line line number (>= 1)
   * @param column column number (>= 1)
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

  private final val line: Int = 0
  private final val column: Int = 0
}
