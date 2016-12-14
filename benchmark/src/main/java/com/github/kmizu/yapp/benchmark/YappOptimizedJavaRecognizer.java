package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.OptimizedJavaRecognizer;
import com.github.kmizu.yapp.runtime.Result;

public class YappOptimizedJavaRecognizer implements GenericParser {
  private OptimizedJavaRecognizer recognizer;
  
  public void setInput(Reader input) {
    recognizer = new OptimizedJavaRecognizer(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage());
    }
    return true;
  }
}
