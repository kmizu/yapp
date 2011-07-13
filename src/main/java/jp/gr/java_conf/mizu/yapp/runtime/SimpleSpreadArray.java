package jp.gr.java_conf.mizu.yapp.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 
 * ’Pƒ‚ÈƒAƒ‹ƒSƒŠƒYƒ€‚É‚æ‚éA—v‘f”‚É‰‚¶‚Ä©“®‚ÅL‚Ñk‚İ‚·‚é”z—ñƒNƒ‰ƒX‚ÌÀ‘•
 * 
 * @author Kota Mizushima
 *
 * @param <T> ”z—ñ‚Ì—v‘fŒ^
 */
@SuppressWarnings(value="unchecked")
public class SimpleSpreadArray<T> implements SpreadArray<T> {
  private static final int DEFAULT_INCREASING = 2;
  private static final int DEFAULT_INITIAL_CAPACITY = 100;  
  private Object[] elements;
  private int increasing;
  private int size;
  
  /**
   * —v‘f”‚É‰‚¶‚Ä©“®‚ÅL‚Ñk‚İ‚·‚é”z—ñ‚ğ¶¬‚µ‚Ü‚·B
   */
  public SimpleSpreadArray() {
    this(DEFAULT_INITIAL_CAPACITY);
  }
  
  /**
   * —v‘f”‚É‰‚¶‚Ä©“®‚ÅL‚Ñk‚İ‚·‚é”z—ñ‚ğ¶¬‚µ‚Ü‚·B
   */
  public SimpleSpreadArray(int capacity) {
    elements = new Object[capacity];
    increasing = DEFAULT_INCREASING;
    size = 0;
  }

  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#set(int, T)
   */
  public void set(int index, T element) {
    assert index >= 0 : "index must be >= 0";
    if(index >= size){
      if(index >= elements.length){
        increaseCapacity(index);
      }
      size = index + 1;
    }
    elements[index] = element;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#get(int)
   */
  public T get(int index) {
    assert index >= 0 : "index must be >= 0";
    if(index >= size){
      if(index >= elements.length){
        increaseCapacity(index);
      }
      size = index + 1;
    }
    return (T)elements[index];
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#size()
   */
  public int size() {
    return size;
  }
  
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#truncate(int)
   */
  public void truncate(int toIndex) {
    assert toIndex >= 0 : "toIndex must be >= 0";
    Object[] newElements;
    int removeCount = toIndex < size ? toIndex : size;
    size -= removeCount;
    newElements = new Object[size];
    System.arraycopy(elements, removeCount, newElements, 0, size);
    elements = newElements;
  }
      
  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#resize(int)
   */
  public void resize(int newSize) {
    if(newSize >= elements.length){
      increaseCapacity(newSize);
    }else if(newSize < size){
      Arrays.fill(elements, newSize, size, null);
    }
    size = newSize;
  }

  /* (non-Javadoc)
   * @see jp.gr.java_conf.mizu.yapp.runtime.SpreadArray#toList()
   */
  public List<T> toList() {
    return new ArrayList<T>(
      (Collection<T>)Arrays.asList(elements).subList(0, size)
    );
  }
  
  private void increaseCapacity(int requiredSize) {
    int newCapacity = (requiredSize + 1) * increasing;
    Object[] newElements = new Object[newCapacity];
    System.arraycopy(elements, 0, newElements, 0, elements.length);
    elements = newElements;
  }
}
