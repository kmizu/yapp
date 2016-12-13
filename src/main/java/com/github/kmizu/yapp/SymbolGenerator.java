package com.github.kmizu.yapp;

public class SymbolGenerator {
  private String prefix;
  private int   count;
  
  public SymbolGenerator(String prefix) {
    this.prefix = prefix;
  }
  
  public Symbol gensym() {
    return Symbol.intern(prefix + (count++));
  }
  
  public void reset() {
    count = 0;
  }
}
