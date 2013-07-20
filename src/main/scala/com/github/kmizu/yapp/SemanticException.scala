package com.github.kmizu.yapp

object SemanticException {
  private final val serialVersionUID: Long = -3752445565800942042L
}

class SemanticException extends RuntimeException {
  def this(pos: Position, message: String) {
    this()
    `super`(message)
    this.line = pos.getLine
    this.column = pos.getColumn
  }

  def getErrorMessage: String = {
    return line + ":" + column + ":" + getMessage
  }

  private var line: Int = 0
  private var column: Int = 0
}
