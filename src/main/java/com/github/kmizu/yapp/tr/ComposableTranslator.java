package com.github.kmizu.yapp.tr;

public class ComposableTranslator<F, T> implements Translator<F, T> {
  private final Translator<F, T> impl;
  public ComposableTranslator(Translator<F, T> impl) {
    this.impl = impl;
  }
  public T translate(F from) {
    return impl.translate(from);
  }
  public final <H> ComposableTranslator<F, H> compose(final Translator<T, H> obj) {
    return new ComposableTranslator<F, H>(null) {
      public H translate(F from) {
        return obj.translate(ComposableTranslator.this.translate(from));
      }
    };
  }
  public static <F, T> ComposableTranslator<F, T> wrap(Translator<F, T> translator) {
    return new ComposableTranslator<F, T>(translator);
  }
  public static <F> ComposableTranslator<F, F> empty() {
    return new ComposableTranslator<F, F>(null) {
      public F translate(F from) {
        return from;
      }
    };
  }
}
