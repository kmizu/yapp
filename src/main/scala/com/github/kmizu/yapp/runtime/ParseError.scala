package com.github.kmizu.yapp.runtime

import java.util.Formatter

class ParseError {
  def this(location: Location, message: String) {
    this()
    this.location = location
    this.message = message
  }

  def getLocation: Location = {
    return location
  }

  def getLine: Int = {
    return location.getLine
  }

  def getColumn: Int = {
    return location.getColumn
  }

  def getMessage: String = {
    return message
  }

  def getErrorMessage: String = {
    val builder: StringBuilder = new StringBuilder
    val f: Formatter = new Formatter(builder)
    f.format("%d, %d: %s", location.getLine, location.getColumn, message)
    f.flush
    return new String(builder)
  }

  private final val location: Location = null
  private final val message: String = null
}
