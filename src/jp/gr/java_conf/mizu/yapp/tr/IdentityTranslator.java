package jp.gr.java_conf.mizu.yapp.tr;

public class IdentityTranslator<T> implements Translator<T, T> {
  public T translate(T from) {
    return from;
  }
}
