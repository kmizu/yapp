package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.OptimizedXMLParser;
import com.github.kmizu.yapp.benchmark.parser.XMLParser;
import com.github.kmizu.yapp.runtime.Result;

public class YappOptimizedXMLRecognizer implements GenericParser<Object> {
  private OptimizedXMLParser recognizer;

  public void setInput(Reader input) {
    recognizer = new OptimizedXMLParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
