package jp.gr.java_conf.mizu.yapp.tr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.CompilationException;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.parser.ParseException;
import jp.gr.java_conf.mizu.yapp.parser.YappParser;

public class Parser implements Translator<File, Ast.Grammar> {
  private String prefix;
  public Parser(String prefix) {
    this.prefix = prefix;
  }
  public Ast.Grammar translate(File input) {
    try {
      FileReader in = new FileReader(input);
      try {
        YappParser parser = new YappParser(in);
        Ast.Grammar grammar = parser.parse();
        if(grammar.name() == null) {
          String fileName = input.getName();
          grammar.setName(
            Symbol.intern(prefix + fileName.substring(0, fileName.lastIndexOf('.')))
          );
        }
        return grammar;
      }finally{
        in.close();
      }
    }catch(FileNotFoundException e) {
      throw new CompilationException(e);
    }catch(IOException e){
      throw new CompilationException(e);
    }catch(ParseException e) {
      throw new CompilationException(e);
    }
  }

}
