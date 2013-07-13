package com.github.kmizu.yapp.runtime

object Result {
  def fail: Result[_] = {
    return FAIL
  }

  final val FAIL: Result[Null] = new Result[Null](-1, null, new ParseError(new Location(0, 0), "default failure object"))
}

case class Result[+T](pos : Int, value : T, error : ParseError, debugInfo : Exception) {
  def this(pos: Int, value: T, error: ParseError) {
    this(pos, value, error, null)
  }

  def this(pos: Int, value: T) {
    this(pos, value, null)
  }

  def getPos: Int = pos

  def getValue: T = value

  def getError: ParseError = error

  def isFailure: Boolean = error != null

  def isNotFailure: Boolean = !isFailure

  def getDebugInfo: Exception = debugInfo
}
