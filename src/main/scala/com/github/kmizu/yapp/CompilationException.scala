package com.github.kmizu.yapp

class CompilationException(val reason: Exception) extends RuntimeException(reason) {
  def getReason: Throwable = {
    return getCause
  }
}
