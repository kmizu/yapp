package jp.gr.java_conf.mizu.yapp.benchmark;

import java.io.IOException;
import java.io.Reader;

import jp.gr.java_conf.mizu.yapp.benchmark.parser.RatsXMLParser;

import xtc.parser.ParseException;

public class RatsXMLRecognizer implements GenericParser<Object> {
  private RatsXMLParser recognizer;
  
  public void setInput(Reader input) {
    recognizer = new RatsXMLParser(input, "<generated>");
  }
  
  public Object parse() {
    try {
      return recognizer.value(recognizer.pDocument(0));
    }catch(IOException e){
      throw new RuntimeException(e);
    }catch(ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
