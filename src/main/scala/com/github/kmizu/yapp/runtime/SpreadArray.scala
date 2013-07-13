package com.github.kmizu.yapp.runtime

import java.util.List

/**
 * This interface represents arrays which grow and shrink
 * automatically if needed.
 *
 * @author Kota Mizushima
 *
 * @param <T> an element type of array
 */
abstract trait SpreadArray {
  /**
   * Sets an element of array.
   * @param index index of array
   * @param element an element to be set
   */
  def set(index: Int, element: T)

  /**
   * Gets an element of array.
   * @param index index of array
   * @return an element of array
   */
  def get(index: Int): T

  /**
   * Gets size of array.
   * @return size of array.
   */
  def size: Int

  /**
   * Sets size of array.
   * @param newSize new size of array
   */
  def resize(newSize: Int)

  /**
   * Remove all elements which index is between 0 and toIndex.
   * @param toIndex the next index of last element removed
   */
  def truncate(toIndex: Int)

  /**
   * Translates this array to List and returns it.
   * @return a List object translated from this array
   */
  def toList: List[T]
}
