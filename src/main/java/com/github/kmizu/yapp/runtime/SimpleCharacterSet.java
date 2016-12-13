package com.github.kmizu.yapp.runtime;

public final class SimpleCharacterSet implements CharacterSet {
  private final boolean[] table = new boolean[Character.MAX_VALUE];
  public SimpleCharacterSet(char... chars) {
    for(char ch : chars) table[ch] = true;
  }
  public boolean contains(char ch) {
    return table[ch];
  }
  public void add(char ch) {
    table[ch] = true;
  }
}
