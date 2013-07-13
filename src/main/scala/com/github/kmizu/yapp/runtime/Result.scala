package com.github.kmizu.yapp.runtime

object Result {
  def fail: Result[_] = {
    return FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) final val FAIL: Result[_] = new Result[_](-1, null, new ParseError(new Location(0, 0), "default failure object"))
}

class Result {
  def this(pos: Int, value: T) {
    this()
    `this`(pos, value, null)
  }

  def this(pos: Int, value: T, error: ParseError) {
    this()
    `this`(pos, value, error, null)
  }

  def this(pos: Int, value: T, error: ParseError, debugInfo: Exception) {
    this()
    this.pos = pos
    this.value = value
    this.error = error
    this.debugInfo = debugInfo
  }

  def getPos: Int = {
    return pos
  }

  def getValue: T = {
    return value
  }

  def getError: ParseError = {
    return error
  }

  def isFailure: Boolean = {
    return error != null
  }

  def isNotFailure: Boolean = {
    return !isFailure
  }

  def getDebugInfo: Exception = {
    return debugInfo
  }

  private final val pos: Int = 0
  private final val value: T = null
  private final val error: ParseError = null
  private final val debugInfo: Exception = null
}
