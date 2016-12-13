package com.github.kmizu.yapp.translator;

public class VoidTranslator<T> implements Translator<T, Void> {
  public Void translate(T from) {
    return null;
  }
}
