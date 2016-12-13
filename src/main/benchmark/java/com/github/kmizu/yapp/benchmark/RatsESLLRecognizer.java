package com.github.kmizu.yapp.benchmark;

import java.io.Reader;

import java.io.IOException;

import com.github.kmizu.yapp.benchmark.parser.RatsESLLParser;

import xtc.parser.ParseException;

public class RatsESLLRecognizer implements GenericParser<Object> {
  private RatsESLLParser recognizer;
  
  public void setInput(Reader input) {
    recognizer = new RatsESLLParser(input, "<generated>");
  }
  
  public Object parse() {
    try {
      return recognizer.value(recognizer.pprogram(0));
    }catch(IOException e){
      throw new RuntimeException(e);
    }catch(ParseException e) {
      throw new RuntimeException(e);
    }
  }
}