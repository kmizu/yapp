/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import java.util.HashMap
import java.util.Map

final object Symbol {
  def intern(key: String): Symbol = {
    if (key == null) return null
    var value: Symbol = cache.get(key)
    if (value == null) {
      value = new Symbol(key)
      cache.put(key, value)
    }
    return value
  }

  private var cache: Map[String, Symbol] = new HashMap[String, Symbol]
}

final class Symbol extends Comparable[Symbol] {
  private def this(key: String) {
    this()
    this.key = key
  }

  def getKey: String = {
    return key
  }

  override def toString: String = {
    return key
  }

  override def equals(obj: AnyRef): Boolean = {
    return super == obj
  }

  override def hashCode: Int = {
    return super.hashCode
  }

  def compareTo(o: Symbol): Int = {
    if (this eq o) return 0
    return key.compareTo(o.key)
  }

  private var key: String = null
}
