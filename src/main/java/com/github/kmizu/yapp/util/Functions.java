package com.github.kmizu.yapp.util;

public class Functions {
  public static interface Fn0<R> {
    R x();
  }
  public static interface Fn1<T, R> {
    R x(T a);
  }
  public static interface Fn2<T1, T2, R> {
    R x(T1 a1, T2 a2);
  }
  public static interface Fn3<T1, T2, T3, R> {
    R x(T1 a1, T2 a2, T3 a3);
  }
  public static interface Fn4<T1, T2, T3, T4, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4);
  }
  public static interface Fn5<T1, T2, T3, T4, T5, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5);
  }
  public static interface Fn6<T1, T2, T3, T4, T5, T6, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6);
  }
  public static interface Fn7<T1, T2, T3, T4, T5, T6, T7, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7);
  }
  public static interface Fn8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7, T8 a8);
  }
  public static interface Fn9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
    R x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7, T8 a8, T9 a9);
  }
  public static interface Pr0 extends Fn0<Boolean> {
    Boolean x();
  }
  public static interface Pr1<T> extends Fn1<T, Boolean> {
    Boolean x(T a);
  }
  public static interface Pr2<T1, T2> extends Fn2<T1, T2, Boolean> {
    Boolean x(T1 a1, T2 a2);
  }
  public static interface Pr3<T1, T2, T3> extends Fn3<T1, T2, T3, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3);
  }
  public static interface Pr4<T1, T2, T3, T4> extends Fn4<T1, T2, T3, T4, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4);
  }
  public static interface Pr5<T1, T2, T3, T4, T5> extends Fn5<T1, T2, T3, T4, T5, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5);
  }
  public static interface Pr6<T1, T2, T3, T4, T5, T6> extends Fn6<T1, T2, T3, T4, T5, T6, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6);
  }
  public static interface Pr7<T1, T2, T3, T4, T5, T6, T7> extends Fn7<T1, T2, T3, T4, T5, T6, T7, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7);
  }
  public static interface Pr8<T1, T2, T3, T4, T5, T6, T7, T8> extends Fn8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7, T8 a8);
  }
  public static interface Pr9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Fn9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean> {
    Boolean x(T1 a1, T2 a2, T3 a3, T4 a4, T5 a5, T6 a6, T7 a7, T8 a8, T9 a9);
  }
}
