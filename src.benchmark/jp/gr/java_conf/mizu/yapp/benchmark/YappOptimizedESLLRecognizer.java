package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedESLLParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedXMLParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.XMLParser;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

public class YappOptimizedESLLRecognizer implements GenericParser<Object> {
  private OptimizedESLLParser recognizer;

  public void setInput(Reader input) {
    recognizer = new OptimizedESLLParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
