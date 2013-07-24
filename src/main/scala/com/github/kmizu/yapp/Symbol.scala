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
  private[this] val cache = new HashMap[String, Symbol]

  def intern(key: String): Symbol = {
    if (key == null) return null

    var value = cache.get(key)
    if (value == null) {
      value = new Symbol(key)
      cache.put(key, value)
    }

    value
  }
}

final class Symbol private(key: String) extends AnyRef with Comparable[Symbol] {
  def getKey: String = key

  override def toString: String = key

  override def equals(obj: Any): Boolean = this eq obj.asInstanceOf[AnyRef]

  override def hashCode: Int = super.hashCode

  def compareTo(o: Symbol): Int = if (this eq o) 0 else key.compareTo(o.key)
}
