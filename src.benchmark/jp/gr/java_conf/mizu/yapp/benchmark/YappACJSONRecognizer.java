package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.ACJSONParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.ACXMLParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.OptimizedXMLParser;
import jp.gr.java_conf.mizu.yapp.benchmark.parser.XMLParser;
import jp.gr.java_conf.mizu.yapp.runtime.Result;

public class YappACJSONRecognizer implements GenericParser<Object> {
  private ACJSONParser recognizer;

  public void setInput(Reader input) {
    recognizer = new ACJSONParser(input);
  }
  
  public Object parse() {
    Result<?> r = recognizer.parse();
    if(r.isFailure()){      
      throw new RuntimeException(r.getError().getErrorMessage(), r.getDebugInfo());
    }
    return true;
  }
}
