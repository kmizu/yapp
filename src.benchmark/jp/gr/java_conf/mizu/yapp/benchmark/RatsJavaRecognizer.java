package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.IOException;
import java.io.Reader;

import xtc.lang.JavaRecognizer;
import xtc.parser.ParseException;
import xtc.parser.Result;

public class RatsJavaRecognizer implements GenericParser<Object> {
  private JavaRecognizer recognizer;
  
  public void setInput(Reader input) {
    recognizer = new JavaRecognizer(input, "<generated>");
  }
  
  public Object parse() {
    try {
      return recognizer.value(recognizer.pCompilationUnit(0));
    }catch(IOException e){
      throw new RuntimeException(e);
    }catch(ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
