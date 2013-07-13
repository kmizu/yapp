package com.github.kmizu.yapp.runtime

final class SimpleCharacterSet extends CharacterSet {
  def this(chars: Char*) {
    this()
    for (ch <- chars) table(ch) = true
  }

  def contains(ch: Char): Boolean = {
    return table(ch)
  }

  def add(ch: Char) {
    table(ch) = true
  }

  private final val table: Array[Boolean] = new Array[Boolean](Character.MAX_VALUE)
}
