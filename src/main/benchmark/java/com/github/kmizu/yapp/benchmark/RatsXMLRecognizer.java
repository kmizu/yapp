package com.github.kmizu.yapp.benchmark;

import java.io.IOException;
import java.io.Reader;

import com.github.kmizu.yapp.benchmark.parser.RatsXMLParser;

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
