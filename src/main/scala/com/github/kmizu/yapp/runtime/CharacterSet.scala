package com.github.kmizu.yapp.runtime

trait CharacterSet {
  def contains(ch: Char): Boolean
  def add(ch: Char)
}
