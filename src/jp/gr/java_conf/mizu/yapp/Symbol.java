/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp;

import java.util.HashMap;
import java.util.Map;

public final class Symbol implements Comparable<Symbol> {
  private static Map<String, Symbol> cache = new HashMap<String, Symbol>();
  
  public static Symbol intern(String key){
    if(key == null) return null;
    Symbol value = cache.get(key);
    if(value == null){
      value = new Symbol(key);
      cache.put(key, value);
    }
    return value;
  }
  
  private String key;
  
  private Symbol(String key){
    this.key = key;
  }
  
  public String getKey(){
    return key;
  }
  
  @Override
  public String toString() {
    return key;
  }
  
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public int compareTo(Symbol o) {
    if(this == o) return 0;
    return key.compareTo(o.key);
  }
}
