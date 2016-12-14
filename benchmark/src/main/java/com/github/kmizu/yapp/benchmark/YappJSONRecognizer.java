package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.JSONParser;
import com.github.kmizu.yapp.benchmark.parser.JavaRecognizer;
import com.github.kmizu.yapp.runtime.ParseError;
import com.github.kmizu.yapp.runtime.Result;

public class YappJSONRecognizer implements GenericParser<Object> {
  private JSONParser recognizer;
  public YappJSONRecognizer(){    
  }
  
  public void setInput(Reader input) {
    recognizer = new JSONParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
