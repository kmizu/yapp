/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

object Pair {
  def make[F, S](fst: F, snd: S): Pair[F, S] = Pair(fst, snd)
}

final case class Pair[F, S](fst: F, snd: S)
