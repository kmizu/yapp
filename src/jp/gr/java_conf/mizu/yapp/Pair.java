/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp;

public class Pair<F, S> {
  public final F fst;
  public final S snd;
  
  public Pair(F fst, S snd) {
    this.fst = fst;
    this.snd = snd;
  }
  
  public static <F, S> Pair<F, S> make(F fst, S snd) {
    return new Pair<F, S>(fst, snd);
  }
}
