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
 * Creates an object which represents location in a source file.
 * @param line line number (>= 1)
 * @param column column number (>= 1)
 */
case class Location(line: Int, column: Int) {
  /**
   * Gets line number.
   * @return line number
   */
  def getLine: Int = line

  /**
   * Gets column number.
   * @return column number
   */
  def getColumn: Int = column

  override def toString: String = line + ":" + column
}
