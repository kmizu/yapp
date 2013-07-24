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
 * Creates a Position's instance.
 *
 * @param line line number
 * @param column column number
 */
final case class Position(line: Int, column: Int) {
  /**
   * Gets line number.
   * TODO should be removed after fixing all callers to call `line` instead of this method
   * @return line number
   */
  //TODO should be removed after fixing callers
  def getLine: Int = line

  /**
   * Gets column number.
   * TODO should be removed after fixing all callers to call `column` instead of this method
   * @return column number
   */
  def getColumn: Int = column

  override def toString: String = line + ":" + column
}
