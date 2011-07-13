package jp.gr.java_conf.mizu.yapp.runtime;

import java.util.List;

import static jp.gr.java_conf.mizu.yapp.util.CollectionUtil.*;

/**
 * An implementation of SpreadArray, which grows and shrinks 
 * automatically.  This class is implemented by circular buffer.
 * 
 * @author Kota Mizushima
 *
 * @param <T> element type of array
 */
@SuppressWarnings(value="unchecked")
public class CircularSpreadArray<T> implements SpreadArray<T> {
  private static final int CAPACITY_INCREASING = 2;
  private static final int DEFAULT_INITIAL_CAPACITY = 100;
  private Object[] elements;
  private int base;
  private int size;
  private int mask;
  
  /**
   * Creates a new CircularSpreadArray with DEFAULT_INITIAL_CAPACITY.
   */
  public CircularSpreadArray() {
    this(DEFAULT_INITIAL_CAPACITY);
  }
  
  /**
   * Creates a new CircularSpreadArray with initialCapacity
   * @param initialCapacity initial value of length of array
   */
  public CircularSpreadArray(int initialCapacity) {
    elements = new Object[exp2(initialCapacity)];
    base = 0;
    size = 0;
    mask = elements.length - 1;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#set(int, T)
   */
  public void set(int index, T element) {
    assert index >= 0 : "index must be >= 0";
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
  public T get(int index) {
    assert index >= 0 : "index must be >= 0";
    if(index >= size){
      if(index >= elements.length){
        increaseCapacity(index + 1);
      }
      size = index + 1;
    }
    return (T)_get(index);
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
    assert newSize >=0 : "newSize must be >= 0";
    if(newSize > elements.length){
      increaseCapacity(newSize);
    }else if(newSize < size){
      for(int i = newSize; i < size; i++){
        _set(i, null);
      }
    }
    size = newSize;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#truncate(int)
   */
  public void truncate(int toIndex) {
    assert toIndex >= 0 : "toIndex must be >= 0";
    int removeCount = toIndex < size ? toIndex : size;
    for(int i = 0; i < removeCount; i++){
      _set(i, null);
    }
    base = realIndex(removeCount);
    size -= removeCount;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#toList()
   */
  public List<T> toList() {
    List<T> copy = list();
    for(int i = 0; i < size; i++){
      copy.add((T)_get(i));
    }
    return copy;
  }
  
  private void increaseCapacity(int requiredSize) {
    int newCapacity = exp2(requiredSize);
    Object[] newElements = new Object[newCapacity];
    int part1Length = Math.min(size, elements.length - base);
    System.arraycopy(elements, base, newElements, 0, part1Length);
    int part2Length = size - part1Length;
    System.arraycopy(elements, 0, newElements, part1Length, part2Length);
    this.elements = newElements;
    this.base = 0;
    this.mask = newCapacity - 1;
  }
  
  private void _set(int index, Object element) {    
    elements[(base + index) & mask] = element;
  }
  
  private Object _get(int index) {
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
