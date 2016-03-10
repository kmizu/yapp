package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.JavaRecognizer;
import jp.gr.java_conf.mizu.yapp.runtime.ParseError;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

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
