package com.github.kmizu.yapp.runtime

import java.util.TreeSet

final class TreeCharacterSet extends CharacterSet {
  def this(chars: Char*) {
    this()
    for (ch <- chars) set.add(ch)
  }

  def contains(ch: Char): Boolean = {
    return set.contains(ch)
  }

  def add(ch: Char) {
    set.add(ch)
  }

  private var set: TreeSet[Character] = new TreeSet[Character]
}
