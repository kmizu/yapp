package com.github.kmizu.yapp

class SymbolGenerator(val prefix: String) {
  private var count: Int = 0

  def gensym(): Symbol = {
    val result = Symbol.intern(prefix + count)
    count += 1
    result
  }

  def reset() {
    count = 0
  }
}