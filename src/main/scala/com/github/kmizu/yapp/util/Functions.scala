package com.github.kmizu.yapp.util

object Functions {

  abstract trait Fn0[R] {
    def x: R
  }

  abstract trait Fn1[T, R] {
    def x(a: T): R
  }

  abstract trait Fn2[T1, T2, R] {
    def x(a1: T1, a2: T2): R
  }

  abstract trait Fn3[T1, T2, T3, R] {
    def x(a1: T1, a2: T2, a3: T3): R
  }

  abstract trait Fn4[T1, T2, T3, T4, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4): R
  }

  abstract trait Fn5[T1, T2, T3, T4, T5, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5): R
  }

  abstract trait Fn6[T1, T2, T3, T4, T5, T6, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6): R
  }

  abstract trait Fn7[T1, T2, T3, T4, T5, T6, T7, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7): R
  }

  abstract trait Fn8[T1, T2, T3, T4, T5, T6, T7, T8, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8): R
  }

  abstract trait Fn9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] {
    def x(a1: T1, a2: T2, a3: T3, a4: T4, a5: T5, a6: T6, a7: T7, a8: T8, a9: T9): R
  }

  abstract trait Pr0 extends Fn0[Boolean] {
    def x: Boolean
  }

  type Pr1[T] = Fn1[T, Boolean]

  type Pr2[T1, T2] = Fn2[T1, T2, Boolean]

  type Pr3[T1, T2, T3] = Fn3[T1, T2, T3, Boolean]

  type Pr4[T1, T2, T3, T4] = Fn4[T1, T2, T3, T4, Boolean]

  type Pr5[T1, T2, T3, T4, T5] = Fn5[T1, T2, T3, T4, T5, Boolean]

  type Pr6[T1, T2, T3, T4, T5, T6] = Fn6[T1, T2, T3, T4, T5, T6, Boolean]

  type Pr7[T1, T2, T3, T4, T5, T6, T7] = Fn7[T1, T2, T3, T4, T5, T6, T7, Boolean]

  type Pr8[T1, T2, T3, T4, T5, T6, T7, T8] = Fn8[T1, T2, T3, T4, T5, T6, T7, T8, Boolean]

  type Pr9[T1, T2, T3, T4, T5, T6, T7, T8, T9] = Fn9[T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean]
}