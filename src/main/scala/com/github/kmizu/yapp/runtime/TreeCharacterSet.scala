package com.github.kmizu.yapp.runtime

import java.util.TreeSet

final class TreeCharacterSet(chars: Char*) extends CharacterSet {
  private[this] val set = new TreeSet[Character]

  {
    for (ch <- chars) set.add(ch)
  }

  def contains(ch: Char): Boolean = set.contains(ch)

  def add(ch: Char): Unit = set.add(ch)
}
