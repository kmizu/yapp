package com.github.kmizu.yapp.runtime

final class SimpleCharacterSet(chars: Char*) extends CharacterSet {
  private[this] val table = new Array[Boolean](Character.MAX_VALUE)

  {
    for (ch <- chars) table(ch) = true
  }

  def contains(ch: Char): Boolean =  table(ch)

  def add(ch: Char): Unit = table(ch) = true

}
