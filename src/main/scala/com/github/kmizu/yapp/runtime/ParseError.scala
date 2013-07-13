package com.github.kmizu.yapp.runtime

import java.lang.{Integer => JInteger}
import java.util.{Formatter => JFormatter}

case class ParseError(location: Location, message: String) {
  def line: Int = location.getLine

  def column: Int = location.getColumn

  def getLocation: Location = this.location

  def getLine: Int = location.getLine

  def getColumn: Int = location.getColumn

  def getMessage: String = message

  def getErrorMessage: String = {
    val builder = new StringBuilder

    val formatter = new JFormatter()(builder)
    formatter.format("%d, %d: %s", new JInteger(line), new JInteger(column), message)
    formatter.flush

    new String(builder.toString())
  }
}
