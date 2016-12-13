package com.github.kmizu.yapp.tr;

public class VoidTranslator<T> implements Translator<T, Void> {
  public Void translate(T from) {
    return null;
  }
}
