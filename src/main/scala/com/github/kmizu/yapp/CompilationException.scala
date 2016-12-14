package com.github.kmizu.yapp

case class CompilationException(val reason: Exception) extends RuntimeException(reason) {
  def getReason: Throwable = getCause
}