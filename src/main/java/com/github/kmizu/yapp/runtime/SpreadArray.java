package com.github.kmizu.yapp.runtime;

import java.util.List;

/**
 * This interface represents arrays which grow and shrink
 * automatically if needed.
 * 
 * @author Kota Mizushima
 *
 * @param <T> an element type of array
 */
public interface SpreadArray<T> {

  /**
   * Sets an element of array.
   * @param index index of array
   * @param element an element to be set
   */
  void set(int index, T element);

  /**
   * Gets an element of array.
   * @param index index of array
   * @return an element of array
   */
  T get(int index);

  /**
   * Gets size of array.
   * @return size of array.
   */
  int size();
  
  /**
   * Sets size of array.
   * @param newSize new size of array
   */
  void resize(int newSize);

  /**
   * Remove all elements which index is between 0 and toIndex.
   * @param toIndex the next index of last element removed
   */
  void truncate(int toIndex);

  /**
   * Translates this array to List and returns it.
   * @return a List object translated from this array
   */
  List<T> toList();
}