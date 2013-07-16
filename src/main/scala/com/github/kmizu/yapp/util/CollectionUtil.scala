/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.util

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Set
import com.github.kmizu.yapp.Pair

object CollectionUtil {
  def list[T](elements: T*): List[T] = new ArrayList[T](Arrays.asList(elements:_*))

  def t[A, B](fst: A, snd: B): Pair[A, B] = new Pair[A, B](fst, snd)

  def map[K, V](elements: Pair[_ <: K, _ <: V]*): Map[K, V] = {
    val map = new HashMap[K, V]
    for (e <- elements)  map.put(e.fst, e.snd)
    map
  }

  def set[T](elements: T*): Set[T] =  new HashSet[T](Arrays.asList(elements:_*))

  def setFrom[T](collection: Collection[_ <: T]): Set[T] = new HashSet[T](collection)
}