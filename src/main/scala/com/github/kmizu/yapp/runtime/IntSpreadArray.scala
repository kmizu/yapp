package com.github.kmizu.yapp.runtime

import java.util.List
import com.github.kmizu.yapp.util.CollectionUtil._

/**
 *
 * An specialized implementation of SpreadArray, which grows
 * and shrinks automatically.  This class is implemented by
 * circular buffer .
 *
 * @author Kota Mizushima
 *
 */
object IntSpreadArray {
  private def exp2(n: Int): Int = {
    var p = 0
    var i = n - 1
    while (i != 0) {
      p = (p << 1) + 1
      i >>= 1
    }
    p + 1
  }

  private final val DEFAULT_INITIAL_CAPACITY: Int = 100
}

class IntSpreadArray(initialCapacity: Int) {
  import IntSpreadArray._

  private[this] var elements = new Array[Int](exp2(initialCapacity))
  private[this] var base = 0
  private[this] var size = 0
  private[this] var mask = elements.length - 1

  /**
   * Creates a new IntSpreadArray with DEFAULT_INITIAL_CAPACITY.
   */
  def this() {
    this(DEFAULT_INITIAL_CAPACITY)
  }

  def set(index: Int, element: Int) {
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index + 1)
      }
      this.size = index + 1
    }
    _set(index, element)
  }

  def get(index: Int): Int = {
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index + 1)
      }
      this.size = index + 1
    }
    _get(index)
  }

  def resize(newSize: Int): Unit =  {
    if (newSize > elements.length) {
      increaseCapacity(newSize)
    } else if (newSize < size) {
      for(i <- newSize until size) {
        _set(i, -2)
      }
    }
    size = newSize
  }

  def truncate(toIndex: Int): Unit = {
    val removeCount = if (toIndex < size) toIndex else size
    for(i <- 0 until removeCount) {
      _set(i, -2)
    }
    base = realIndex(removeCount)
    size -= removeCount
  }

  private def increaseCapacity(requiredSize: Int): Unit = {
    val newCapacity= exp2(requiredSize)
    val newElements = new Array[Int](newCapacity)

    val part1Length = Math.min(size, elements.length - base)
    System.arraycopy(elements, base, newElements, 0, part1Length)
    val part2Length: Int = size - part1Length
    System.arraycopy(elements, 0, newElements, part1Length, part2Length)

    this.elements = newElements
    this.base = 0
    this.mask = newCapacity - 1
  }

  private def _set(index: Int, element: Int): Unit =  elements((base + index) & mask) = element

  private def _get(index: Int): Int = elements((base + index) & mask)

  private def realIndex(index: Int): Int = (base + index) & mask
}
