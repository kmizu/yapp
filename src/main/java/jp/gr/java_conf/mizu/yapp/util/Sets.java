package jp.gr.java_conf.mizu.yapp.util;

import java.util.HashSet;
import java.util.Set;

public class Sets {
  public static <T> Set<T> plus(Set<T> a, Set<T> b) {
    Set<T> result = new HashSet<T>(a);
    result.addAll(b);
    return result;
  }
  public static <T> Set<T> add(Set<T> a, T b) {
    Set<T> result = new HashSet<T>(a);
    result.add(b);
    return result;
  }
}
