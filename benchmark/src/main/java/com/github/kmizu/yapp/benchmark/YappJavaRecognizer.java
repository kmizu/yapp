package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.JavaRecognizer;
import com.github.kmizu.yapp.runtime.ParseError;
import com.github.kmizu.yapp.runtime.Result;

public class YappJavaRecognizer implements GenericParser<Object> {
  private JavaRecognizer recognizer;
  public YappJavaRecognizer(){    
  }
  
  public void setInput(Reader input) {
    recognizer = new JavaRecognizer(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
