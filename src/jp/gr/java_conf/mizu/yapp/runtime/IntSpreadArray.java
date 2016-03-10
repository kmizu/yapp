package jp.gr.java_conf.mizu.yapp.runtime;

import java.util.List;

import static jp.gr.java_conf.mizu.yapp.util.CollectionUtil.*;

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
@SuppressWarnings(value="unchecked")
public class IntSpreadArray {
  private static final int CAPACITY_INCREASING = 2;
  private static final int DEFAULT_INITIAL_CAPACITY = 100;
  private int[] elements;
  private int base;
  private int size;
  private int mask;
  
  /**
   * Creates a new IntSpreadArray with DEFAULT_INITIAL_CAPACITY.
   */
  public IntSpreadArray() {
    this(DEFAULT_INITIAL_CAPACITY);
  }
  
  /**
   * Creates a new IntSpreadArray with initialCapacity.
   * @param initialCapacity initial value of length of array
   */
  public IntSpreadArray(int initialCapacity) {
    elements = new int[exp2(initialCapacity)];
    base = 0;
    size = 0;
    mask = elements.length - 1;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#set(int, T)
   */
  public void set(int index, int element) {
    if(index >= size){
      if(index >= elements.length){
        increaseCapacity(index + 1);
      }
      size = index + 1;
    }
    _set(index, element);
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#get(int)
   */
  public int get(int index) {
    if(index >= size){
      if(index >= elements.length){
        increaseCapacity(index + 1);
      }
      size = index + 1;
    }
    return _get(index);
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#size()
   */
  public int size() {
    return size;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#resize(int)
   */
  public void resize(int newSize) {
    if(newSize > elements.length){
      increaseCapacity(newSize);
    }else if(newSize < size){
      for(int i = newSize; i < size; i++){
        _set(i, -2);
      }
    }
    size = newSize;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#truncate(int)
   */
  public void truncate(int toIndex) {
    int removeCount = toIndex < size ? toIndex : size;
    for(int i = 0; i < removeCount; i++){
      _set(i, -2);
    }
    base = realIndex(removeCount);
    size -= removeCount;
  }
  
  private void increaseCapacity(int requiredSize) {
    int newCapacity = exp2(requiredSize);
    int[] newElements = new int[newCapacity];
    int part1Length = Math.min(size, elements.length - base);
    System.arraycopy(elements, base, newElements, 0, part1Length);
    int part2Length = size - part1Length;
    System.arraycopy(elements, 0, newElements, part1Length, part2Length);
    this.elements = newElements;
    this.base = 0;
    this.mask = newCapacity - 1;
  }
  
  private void _set(int index, int element) {    
    elements[(base + index) & mask] = element;
  }
  
  private int _get(int index) {
    return elements[(base + index) & mask];
  }
  
  private int realIndex(int index) {
    return (base + index) & mask;
  }
  
  private static int exp2(int n){
    int p = 0;
    for (int i = n - 1; i != 0; i >>= 1) p = (p << 1) + 1;
    return p + 1;
  }
}
