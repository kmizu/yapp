package com.github.kmizu.yapp.runtime

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List

/**
 *
 * �P���ȃA���S���Y���ɂ��A�v�f���ɉ����Ď����ŐL�яk�݂���z��N���X�̎���
 *
 * @author Kota Mizushima
 *
 * @param <T> �z��̗v�f�^
 */
@SuppressWarnings(value = Array("unchecked")) object SimpleSpreadArray {
  private final val DEFAULT_INCREASING: Int = 2
  private final val DEFAULT_INITIAL_CAPACITY: Int = 100
}

@SuppressWarnings(value = Array("unchecked")) class SimpleSpreadArray extends SpreadArray[T] {
  /**
   * �v�f���ɉ����Ď����ŐL�яk�݂���z��𐶐����܂��B
   */
  def this() {
    this()
    `this`(DEFAULT_INITIAL_CAPACITY)
  }

  /**
   * �v�f���ɉ����Ď����ŐL�яk�݂���z��𐶐����܂��B
   */
  def this(capacity: Int) {
    this()
    elements = new Array[AnyRef](capacity)
    increasing = DEFAULT_INCREASING
    size = 0
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
    return elements(index).asInstanceOf[T]
  }

  def size: Int = {
    return size
  }

  def truncate(toIndex: Int) {
    assert(toIndex >= 0, "toIndex must be >= 0")
    var newElements: Array[AnyRef] = null
    val removeCount: Int = if (toIndex < size) toIndex else size
    size -= removeCount
    newElements = new Array[AnyRef](size)
    System.arraycopy(elements, removeCount, newElements, 0, size)
    elements = newElements
  }

  def resize(newSize: Int) {
    if (newSize >= elements.length) {
      increaseCapacity(newSize)
    }
    else if (newSize < size) {
      Arrays.fill(elements, newSize, size, null)
    }
    size = newSize
  }

  def toList: List[T] = {
    return new ArrayList[T](Arrays.asList(elements).subList(0, size).asInstanceOf[Collection[T]])
  }

  private def increaseCapacity(requiredSize: Int) {
    val newCapacity: Int = (requiredSize + 1) * increasing
    val newElements: Array[AnyRef] = new Array[AnyRef](newCapacity)
    System.arraycopy(elements, 0, newElements, 0, elements.length)
    elements = newElements
  }

  private var elements: Array[AnyRef] = null
  private var increasing: Int = 0
  private var size: Int = 0
}
