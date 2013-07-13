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
 * @param <T> element type of array
 */
@SuppressWarnings(value = Array("unchecked")) object IntSpreadArray {
  private def exp2(n: Int): Int = {
    var p: Int = 0
    {
      var i: Int = n - 1
      while (i != 0) {
        p = (p << 1) + 1
        i >>= 1
      }
    }
    return p + 1
  }

  private final val CAPACITY_INCREASING: Int = 2
  private final val DEFAULT_INITIAL_CAPACITY: Int = 100
}

@SuppressWarnings(value = Array("unchecked")) class IntSpreadArray {
  /**
   * Creates a new IntSpreadArray with DEFAULT_INITIAL_CAPACITY.
   */
  def this() {
    this()
    `this`(DEFAULT_INITIAL_CAPACITY)
  }

  /**
   * Creates a new IntSpreadArray with initialCapacity.
   * @param initialCapacity initial value of length of array
   */
  def this(initialCapacity: Int) {
    this()
    elements = new Array[Int](exp2(initialCapacity))
    base = 0
    size = 0
    mask = elements.length - 1
  }

  def set(index: Int, element: Int) {
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index + 1)
      }
      size = index + 1
    }
    _set(index, element)
  }

  def get(index: Int): Int = {
    if (index >= size) {
      if (index >= elements.length) {
        increaseCapacity(index + 1)
      }
      size = index + 1
    }
    return _get(index)
  }

  def size: Int = {
    return size
  }

  def resize(newSize: Int) {
    if (newSize > elements.length) {
      increaseCapacity(newSize)
    }
    else if (newSize < size) {
      {
        var i: Int = newSize
        while (i < size) {
          {
            _set(i, -2)
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    size = newSize
  }

  def truncate(toIndex: Int) {
    val removeCount: Int = if (toIndex < size) toIndex else size
    {
      var i: Int = 0
      while (i < removeCount) {
        {
          _set(i, -2)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    base = realIndex(removeCount)
    size -= removeCount
  }

  private def increaseCapacity(requiredSize: Int) {
    val newCapacity: Int = exp2(requiredSize)
    val newElements: Array[Int] = new Array[Int](newCapacity)
    val part1Length: Int = Math.min(size, elements.length - base)
    System.arraycopy(elements, base, newElements, 0, part1Length)
    val part2Length: Int = size - part1Length
    System.arraycopy(elements, 0, newElements, part1Length, part2Length)
    this.elements = newElements
    this.base = 0
    this.mask = newCapacity - 1
  }

  private def _set(index: Int, element: Int) {
    elements((base + index) & mask) = element
  }

  private def _get(index: Int): Int = {
    return elements((base + index) & mask)
  }

  private def realIndex(index: Int): Int = {
    return (base + index) & mask
  }

  private var elements: Array[Int] = null
  private var base: Int = 0
  private var size: Int = 0
  private var mask: Int = 0
}
