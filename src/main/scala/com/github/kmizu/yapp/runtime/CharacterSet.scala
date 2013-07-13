package com.github.kmizu.yapp.runtime

abstract trait CharacterSet {
  def contains(ch: Char): Boolean

  def add(ch: Char)
}
