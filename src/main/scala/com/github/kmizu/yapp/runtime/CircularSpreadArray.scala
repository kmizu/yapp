package com.github.kmizu.yapp.runtime

import java.util.List
import com.github.kmizu.yapp.util.CollectionUtil

/**
 * An implementation of SpreadArray, which grows and shrinks
 * automatically.  This class is implemented by circular buffer.
 *
 * @author Kota Mizushima
 * @
 */
object CircularSpreadArray {
  private def exp2(n: Int): Int = {
    var p: Int = 0
    var i: Int = n - 1
    while (i != 0) {
      p = (p << 1) + 1
      i >>= 1
    }
    p + 1
  }

  private final val DEFAULT_INITIAL_CAPACITY: Int = 100
}

/**
 * Creates a new CircularSpreadArray with initialCapacity
 * @param initialCapacity initial value of length of array
 * @tparam T element type of array
 */
class CircularSpreadArray[T >: Null](initialCapacity: Int) extends AnyRef with SpreadArray[T] {
  import CircularSpreadArray._

  private[this] var elements = new Array[T](exp2(initialCapacity))
  private[this] var base = 0
  private[this] var mask = elements.length - 1

  var size = 0

  /**
   * Creates a new CircularSpreadArray with DEFAULT_INITIAL_CAPACITY.
   */
  def this() {
    this(DEFAULT_INITIAL_CAPACITY)
  }

  def set(index: Int, element: T): Unit = {
    assert(index >= 0, "index must be >= 0")

    if (index >= size) {
      if (index >= elements.length) increaseCapacity(index + 1)
      size = index + 1
    }
    _set(index, element)
  }

  def get(index: Int): T = {
    assert(index >= 0, "index must be >= 0")

    if (index >= size) {
      if (index >= elements.length)  increaseCapacity(index + 1)
      size = index + 1
    }

    _get(index).asInstanceOf[T]
  }

  def resize(newSize: Int): Unit = {
    assert(newSize >= 0, "newSize must be >= 0")
    if (newSize > elements.length) {
      increaseCapacity(newSize)
    } else if (newSize < size) {
      for(i <- newSize until size) {
        _set(i, null)
      }
    }

    size = newSize
  }

  def truncate(toIndex: Int): Unit = {
    assert(toIndex >= 0, "toIndex must be >= 0")
    val removeCount = (if (toIndex < size) toIndex else size)
    for(i <- 0 until removeCount) {
      _set(i, null)
    }
    base = realIndex(removeCount)
    size -= removeCount
  }

  def toList: List[T] = {
    val copy = CollectionUtil.list[T]()
    for(i <- 0 until size)  copy.add(_get(i).asInstanceOf[T])
    copy
  }

  private def increaseCapacity(requiredSize: Int): Unit = {
    val newCapacity = exp2(requiredSize)
    val newElements = new Array[T](newCapacity)

    val part1Length: Int = Math.min(size, elements.length - base)
    System.arraycopy(elements, base, newElements, 0, part1Length)
    val part2Length: Int = size - part1Length
    System.arraycopy(elements, 0, newElements, part1Length, part2Length)

    this.elements = newElements
    this.base = 0
    this.mask = newCapacity - 1
  }

  private def _set(index: Int, element: T): Unit = elements((base + index) & mask) = element

  private def _get(index: Int): T = elements((base + index) & mask)

  private def realIndex(index: Int): Int = (base + index) & mask
}
