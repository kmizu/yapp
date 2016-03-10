package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedJavaRecognizer;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

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
