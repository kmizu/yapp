/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Pair;

public class CollectionUtil {
  public static <T> List<T> list(T... elements) {
    return new ArrayList<T>(Arrays.asList(elements));
  }
  
  public static <A, B> Pair<A, B> t(A fst, B snd) {
    return new Pair<A, B>(fst, snd);
  }
  
  public static <K, V> Map<K, V> map(Pair<? extends K, ? extends V>... elements) {
    Map<K, V> map = new HashMap<K, V>();
    for(Pair<? extends K, ? extends V> e:elements) {
      map.put(e.fst, e.snd);
    }
    return map;
  }
  
  public static <T> Set<T> set(T... elements) {
    return new HashSet<T>(Arrays.asList(elements));
  }
  
  public static <T> Set<T> setFrom(Collection<? extends T> collection) {
    return new HashSet<T>(collection);
  }
}
