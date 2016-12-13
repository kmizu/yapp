package com.github.kmizu.yapp

@SerialVersionUID(-3752445565800942042L)
case class SemanticException(pos: Position, message: String) extends RuntimeException(message) {
  val line  : Int = 0
  val column: Int = 0

  def getErrorMessage: String = {
    line + ":" + column + ":" + getMessage
  }
}