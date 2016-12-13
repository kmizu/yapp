package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import com.github.kmizu.esll.parser.ESLLParserByJavaCC;
import com.github.kmizu.yapp.benchmark.parser.ESLLParser;
import com.github.kmizu.yapp.benchmark.parser.JSONParser;
import com.github.kmizu.yapp.benchmark.parser.JavaRecognizer;
import com.github.kmizu.yapp.runtime.ParseError;
import com.github.kmizu.yapp.runtime.Result;

public class YappESLLRecognizer implements GenericParser<Object> {
  private ESLLParser recognizer;
  public YappESLLRecognizer(){    
  }
  
  public void setInput(Reader input) {
    recognizer = new ESLLParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
