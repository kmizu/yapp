package com.github.kmizu.yapp.util

import java.util.HashSet
import java.util.Set

object Sets {
  def plus[T](a: Set[T], b: Set[T]): Set[T] = {
    val result = new HashSet[T](a)
    result.addAll(b)
    result
  }

  def add[T](a: Set[T], b: T): Set[T] = {
    val result = new HashSet[T](a)
    result.add(b)
    result
  }
}