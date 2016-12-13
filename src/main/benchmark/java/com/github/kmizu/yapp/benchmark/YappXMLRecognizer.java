package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.XMLParser;
import com.github.kmizu.yapp.runtime.Result;

public class YappXMLRecognizer implements GenericParser<Object> {
  private XMLParser recognizer;

  public void setInput(Reader input) {
    recognizer = new XMLParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
