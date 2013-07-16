package com.github.kmizu.yapp.tools

object CommandLineException {
  private final val serialVersionUID: Long = -4221400901082236315L
}

class CommandLineException(message: String) extends RuntimeException(message)
