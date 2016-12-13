package com.github.kmizu.yapp.translator;

public class IdentityTranslator<T> implements Translator<T, T> {
  public T translate(T from) {
    return from;
  }
}
