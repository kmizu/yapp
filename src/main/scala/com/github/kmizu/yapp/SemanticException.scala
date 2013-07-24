package com.github.kmizu.yapp

object SemanticException {
  private final val serialVersionUID: Long = -3752445565800942042L
}

class SemanticException(pos: Position, message: String) extends RuntimeException(message) {
  private[this] val line = pos.getLine
  private[this] val column = pos.getColumn

  def errorMessage: String = getErrorMessage

  def getErrorMessage: String = line + ":" + column + ":" + getMessage //TODO should be removed
}
