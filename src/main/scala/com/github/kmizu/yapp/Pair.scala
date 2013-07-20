/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

object Pair {
  def make(fst: F, snd: S): Pair[F, S] = {
    return new Pair[F, S](fst, snd)
  }
}

class Pair {
  def this(fst: F, snd: S) {
    this()
    this.fst = fst
    this.snd = snd
  }

  final val fst: F = null
  final val snd: S = null
}
