package com.github.kmizu.yapp

class SymbolGenerator(val prefix: String) {
  private[this] var count = 0

  def gensym: Symbol = Symbol.intern(prefix + { val old = count; count += 1; old })

  def reset: Unit = count = 0
}
