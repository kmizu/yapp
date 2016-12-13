package com.github.kmizu.yapp.benchmark;

import java.io.IOException;
import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.RatsJSONParser;
import com.github.kmizu.yapp.benchmark.parser.RatsXMLParser;

import xtc.lang.JavaRecognizer;
import xtc.parser.ParseException;
import xtc.parser.Result;

public class RatsJSONRecognizer implements GenericParser<Object> {
  private RatsJSONParser recognizer;
  
  public void setInput(Reader input) {
    recognizer = new RatsJSONParser(input, "<generated>");
  }
  
  public Object parse() {
    try {
      return recognizer.value(recognizer.pJSON(0));
    }catch(IOException e){
      throw new RuntimeException(e);
    }catch(ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
