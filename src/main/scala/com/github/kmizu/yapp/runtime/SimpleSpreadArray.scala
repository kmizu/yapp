package com.github.kmizu.yapp.runtime

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List

/**
 *
 * @author Kota Mizushima
 *
 */
@SuppressWarnings(value = Array("unchecked")) object SimpleSpreadArray {
  private final val DEFAULT_INCREASING: Int = 2
  private final val DEFAULT_INITIAL_CAPACITY: Int = 100
}

/*
 * @tparam T element of this SimpleSpreadArray
 */
class SimpleSpreadArray[T](capacity: Int) extends SpreadArray[T] {
  import SimpleSpreadArray._

  private[this] var elements = new Array[T](capacity)
  private[this] val increasing = DEFAULT_INCREASING

  var size: Int = 0

  def this() {
    this(DEFAULT_INITIAL_CAPACITY)
  }

  def set(index: Int, element: T) {
    assert(index >= 0, "index must be >= 0")
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index)
      }
      size = index + 1
    }
    elements(index) = element
  }

  def get(index: Int): T = {
    assert(index >= 0, "index must be >= 0")
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index)
      }
      size = index + 1
    }
    elements(index)
  }

  def truncate(toIndex: Int) {
    assert(toIndex >= 0, "toIndex must be >= 0")

    val newElements = new Array[T](size)
    val removeCount = if (toIndex < size) toIndex else size

    size -= removeCount
    System.arraycopy(elements, removeCount, newElements, 0, size)
    elements = newElements
  }

  def resize(newSize: Int) {
    if (newSize >= elements.length) {
      increaseCapacity(newSize)
    }
    else if (newSize < size) {
      Arrays.fill(elements.asInstanceOf[Array[AnyRef]], newSize, size, null)
    }
    size = newSize
  }

  def toList: List[T] = {
    return new ArrayList[T](Arrays.asList(elements).subList(0, size).asInstanceOf[Collection[T]])
  }

  private def increaseCapacity(requiredSize: Int) {
    val newCapacity: Int = (requiredSize + 1) * increasing
    val newElements = new Array[T](newCapacity)
    System.arraycopy(elements, 0, newElements, 0, elements.length)
    elements = newElements
  }
}
