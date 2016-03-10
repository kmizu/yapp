package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedJSONParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedJavaRecognizer;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

public class YappOptimizedJSONRecognizer implements GenericParser {
  private OptimizedJSONParser recognizer;
  
  public void setInput(Reader input) {
    recognizer = new OptimizedJSONParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage());
    }
    return true;
  }
}
