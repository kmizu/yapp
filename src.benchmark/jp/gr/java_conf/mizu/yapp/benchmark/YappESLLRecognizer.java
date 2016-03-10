package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.esll.parser.ESLLParserByJavaCC;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.ESLLParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.JSONParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.JavaRecognizer;
import jp.gr.java_conf.mizu.yapp.runtime.ParseError;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

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
