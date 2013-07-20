package com.github.kmizu.yapp

class CompilationException extends RuntimeException {
  def this(reason: Exception) {
    this()
    `super`(reason)
  }

  def getReason: Throwable = {
    return getCause
  }
}
