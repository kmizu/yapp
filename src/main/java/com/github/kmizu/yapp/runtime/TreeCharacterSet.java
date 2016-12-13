package com.github.kmizu.yapp.runtime;

import java.util.TreeSet;

public final class TreeCharacterSet implements CharacterSet {
  private TreeSet<Character> set = new TreeSet<Character>();
  public TreeCharacterSet(char... chars) {
    for(char ch : chars) set.add(ch);
  }
  public boolean contains(char ch) {
    return set.contains(ch);
  }
  public void add(char ch) {
    set.add(ch);
  }
}
