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
import java.lang.Comparable

final class Symbol private (val key: String) extends Comparable[Symbol] {
  override def toString: String = key

  override def equals(obj: Any): Boolean = super.equals(obj)

  override def hashCode(): Int = super.hashCode()

  override def compareTo(o: Symbol): Int = {
    if(this == o) return 0
    key.compareTo(o.key)
  }
}
object Symbol {
  private val cache: Map[String, Symbol] = new HashMap[String, Symbol]
  
  def intern(key: String): Symbol = {
    if(key == null) return null;
    var value = cache.get(key);
    if(value == null){
      value = new Symbol(key);
      cache.put(key, value);
    }
    value
  }
}
