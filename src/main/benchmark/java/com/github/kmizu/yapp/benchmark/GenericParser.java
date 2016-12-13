package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

public interface GenericParser<T> {
  void setInput(Reader input);
  T parse();
}
