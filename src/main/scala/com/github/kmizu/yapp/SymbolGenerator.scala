package com.github.kmizu.yapp

class SymbolGenerator {
  def this(prefix: String) {
    this()
    this.prefix = prefix
  }

  def gensym: Symbol = {
    return Symbol.intern(prefix + (({
      count += 1; count - 1
    })))
  }

  def reset {
    count = 0
  }

  private var prefix: String = null
  private var count: Int = 0
}
