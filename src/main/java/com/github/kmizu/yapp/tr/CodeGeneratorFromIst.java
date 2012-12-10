package com.github.kmizu.yapp.tr;

import static com.github.kmizu.yapp.util.SystemProperties.FILE_SEPARATOR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.CompilationException;
import com.github.kmizu.yapp.Ist;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ist.Accept;
import com.github.kmizu.yapp.Ist.ActionStatement;
import com.github.kmizu.yapp.Ist.BackupCursor;
import com.github.kmizu.yapp.Ist.Block;
import com.github.kmizu.yapp.Ist.DecrDepth;
import com.github.kmizu.yapp.Ist.EscapeFrom;
import com.github.kmizu.yapp.Ist.Fail;
import com.github.kmizu.yapp.Ist.Function;
import com.github.kmizu.yapp.Ist.GenerateFailure;
import com.github.kmizu.yapp.Ist.GenerateSuccess;
import com.github.kmizu.yapp.Ist.IncrDepth;
import com.github.kmizu.yapp.Ist.Loop;
import com.github.kmizu.yapp.Ist.MatchAny;
import com.github.kmizu.yapp.Ist.MatchCharClass;
import com.github.kmizu.yapp.Ist.MatchRule;
import com.github.kmizu.yapp.Ist.MatchString;
import com.github.kmizu.yapp.Ist.NewCursorVar;
import com.github.kmizu.yapp.Ist.Nop;
import com.github.kmizu.yapp.Ist.ParserUnit;
import com.github.kmizu.yapp.Ist.RewindCursor;
import com.github.kmizu.yapp.Ist.SetSemanticValue;
import com.github.kmizu.yapp.Ist.Statement;
import com.github.kmizu.yapp.Ist.Visitor;
import com.github.kmizu.yapp.util.CollectionUtil;

public class CodeGeneratorFromIst 
  extends Visitor<Void, CodeGeneratorFromIst.CodeContext> 
  implements Translator<Ist.ParserUnit, Void> {
  static class CodeContext {
    Symbol type;
  }
  
  private static final int INDENT_SPACE = 2;
  private static final int CHUNK_SIZE   = 10;
  private static final String FUNC_PREFIX     = "parse"     ;
  private static final String MEMO_PREFIX     = "memo"      ;
  private static final String VALUE_NAME      = "value"     ;
  private static final String BASE_NAME       = "baseIndex" ;
  private static final String REGISTER_NAME   = "result"    ;
  private static final String CURSOR_NAME     = "cursor"    ;
  private static final String LOCAL_DEPTH     = "localDepth";
  private static final String DEPTH           = "depth"     ;
  private static final String INDEX_METHOD    = "realIndex" ;
  private static final String IS_ZERO         = "isZero"    ;

  private String dstPackage;
  private File dstDir;
  private PrintWriter out;
  private int indentLevel;
  private boolean joinColumns;

  public CodeGeneratorFromIst(String dstPackage, File dstDir, boolean joinColumns) {
    this.dstPackage = dstPackage;
    this.dstDir = dstDir;
    this.joinColumns = joinColumns;
  }

  
  public Void translate(ParserUnit from) {
    File dstFile = new File(dstDir + FILE_SEPARATOR + from.getName() + ".java");
    try {
      out = new PrintWriter(new FileWriter(dstFile));
      try {
        from.accept(this, new CodeContext());
        out.flush();
        return null;
      }finally{
        out.close();
      }
    }catch(IOException e) {
      throw new CompilationException(e);
    }
  }
    
  @Override
  public Void visit(Accept node, CodeContext context) {
    n("%s = %s;", DEPTH, LOCAL_DEPTH);
    n("return new Result<%s>(%s, %s);", context.type, CURSOR_NAME, VALUE_NAME);
    return null;
  }


  @Override
  public Void visit(ActionStatement node, CodeContext context) {
    n("%s", node.getCode());
    return null;
  }


  @Override
  public Void visit(BackupCursor node, CodeContext context) {
    n("%s = %s;", node.getVar(), CURSOR_NAME);
    return null;
  }


  @Override
  public Void visit(Block node, CodeContext context) {
    Symbol label = node.getLabel();
    if(label != null) {
      n("%s: {", label);
      indent();
      for(Ist.Statement s : node.getStatements()) s.accept(this, context);
      dedent();
      n("}");
    }else{
      for(Ist.Statement s : node.getStatements()){
        if(s == null) {
          System.out.println("s == null!");
        }
        s.accept(this, context);
      }
    }
    return null;
  }


  @Override
  public Void visit(DecrDepth node, CodeContext context) {
    n("%s--;", DEPTH);
    n("tryOptimize(%s);", CURSOR_NAME);
    return null;
  }


  @Override
  public Void visit(EscapeFrom node, CodeContext context) {
    n("if(true) break %s;", node.getLabel());
    return null;
  }


  @Override
  public Void visit(Fail node, CodeContext context) {
    n("if(%s()){", IS_ZERO);
    n("  if(true) return createFailure(%s, \"fail\");", CURSOR_NAME);
    n("}else{");
    n("  %s = %s; if(true) return Result.fail();", DEPTH, LOCAL_DEPTH);
    n("}");
    return null;
  }


  @Override
  public Void visit(Function node, CodeContext context) {
    Symbol name = node.getName();
    Symbol type = node.getType();
    String func = FUNC_PREFIX + "_" + name;
    context.type = type;
    n("");
    n("private Result<%s> %s(int %s) {", type, func, CURSOR_NAME);
    indent();
    n("int %s = %s;", LOCAL_DEPTH, DEPTH);
    n("%s %s = null;", type, VALUE_NAME);
    n("Result<? extends Object> %s = null;", REGISTER_NAME);
    if(node.getCode() != null){
      n("%s", node.getCode());
    }
    node.getStatement().accept(this, context);
    dedent();
    n("}");
    n("");
    return null;
  }


  @Override
  public Void visit(GenerateSuccess node, CodeContext context) {
    n("%s = new Result<%s>(%s, null);", REGISTER_NAME, context.type, CURSOR_NAME);
    return null;
  }
  
  private String escapeQuote(String src) {
    StringBuilder builder = new StringBuilder();
    for(char c: src.toCharArray()) {
      if(c == '\"') builder.append('\\');
      else if(c == '\\') builder.append('\\');
      builder.append(c);
    }
    return new String(builder);
  }
  
  @Override
  public Void visit(GenerateFailure node, CodeContext context) {    
    n("if(%s()){", IS_ZERO);
    n("  %s = createFailure(%s, \"expected: %s\");", REGISTER_NAME, CURSOR_NAME, escapeQuote(node.expected()));
    n("}else{");
    n("  %s = Result.fail();", REGISTER_NAME);
    n("}");
    return null;
  }

  @Override
  public Void visit(IncrDepth node, CodeContext context) {
    n("%s++;", DEPTH);
    return null;
  }


  @Override
  public Void visit(Loop node, CodeContext context) {
    if(node.getLabel() != null){
      n("%s:", node.getLabel());
    }
    n("while(true){");
    indent();
    for(Ist.Statement s : node.getStatements()) s.accept(this, context);
    dedent();
    n("}");
    return null;
  }


  @Override
  public Void visit(MatchAny node, CodeContext context) {
    n("%s = match(%s);", REGISTER_NAME, CURSOR_NAME);
    n("if(%s.isFailure()){", REGISTER_NAME);
    n("  if(%s()){", IS_ZERO);
    n("    %s = createFailure(%s, \"expected: except EOF\");", REGISTER_NAME, CURSOR_NAME);
    n("  }");
    if(node.getLabel() == null){
      n("  %s = %s;", DEPTH, LOCAL_DEPTH);
      n("  if(true) return (Result<%s>)%s;", context.type, REGISTER_NAME);
    }else{
      n("  if(true) break %s;", node.getLabel());
    }
    n("}else{");
    n("  %s = %s.getPos();", CURSOR_NAME, REGISTER_NAME);
    n("}");
    return null;
  }


  @Override
  public Void visit(MatchCharClass node, CodeContext context) {
    if(node.isPositive()){
      n("%s = matchPositive(%s, %s);", REGISTER_NAME, CURSOR_NAME, node.getName());
    }else{
      n("%s = matchNegative(%s, %s);", REGISTER_NAME, CURSOR_NAME, node.getName());
    }
    n("if(%s.isFailure()){", REGISTER_NAME);
    n("  if(%s()){", IS_ZERO);
    n("    %s = createFailure(%s, \"expected: \");", REGISTER_NAME, CURSOR_NAME);
    n("  }");
    if(node.getLabel() == null){
      n("  %s = %s;", DEPTH, LOCAL_DEPTH);
      n("  if(true) return (Result<%s>)%s;", context.type, REGISTER_NAME);
    }else{
      n("  if(true) break %s;", node.getLabel());
    }
    n("}else{");
    n("  %s = %s.getPos();", CURSOR_NAME, REGISTER_NAME);
    n("}");
    return null;
  }


  @Override
  public Void visit(MatchRule node, CodeContext context) {
    n("%s = %s(%s);", REGISTER_NAME, node.getRule(), CURSOR_NAME);
    n("if(%s.isFailure()){", REGISTER_NAME);
    if(node.getLabel() == null){
      n("  %s = %s;", DEPTH, LOCAL_DEPTH);
      n("  if(true) return (Result<%s>)%s;", context.type, REGISTER_NAME);
    }else{
      n("  if(true) break %s;", node.getLabel());
    }
    n("}else{");
    n("  %s = %s.getPos();", CURSOR_NAME, REGISTER_NAME);
    n("}");
    return null;
  }


  @Override
  public Void visit(MatchString node, CodeContext context) {
    n("%s = match(%s, \"%s\");", REGISTER_NAME, CURSOR_NAME, node.getValue());
    n("if(%s.isFailure()){", REGISTER_NAME);
    n("  if(%s()){", IS_ZERO);
    n("    %s = createFailure(%s, \"expected: %s\");", REGISTER_NAME, CURSOR_NAME, node.getValue());
    n("  }");
    if(node.getLabel() == null){
      n("  %s = %s;", DEPTH, LOCAL_DEPTH);
      n("  if(true) return (Result<%s>)%s;", context.type, REGISTER_NAME);
    }else{
      n("  if(true) break %s;", node.getLabel());
    }
    n("}else{");
    n("  %s = %s.getPos();", CURSOR_NAME, REGISTER_NAME);
    n("}");
    return null;
  }


  @Override
  public Void visit(NewCursorVar node, CodeContext context) {
    n("int %s = 0;", node.getName());
    return null;
  }


  @Override
  public Void visit(Nop node, CodeContext context) {
    return null;
  }


  @Override
  public Void visit(ParserUnit node, CodeContext context) {
    Symbol startName = node.getStartName();
    Symbol startType = node.getStartType();
    if(dstPackage != null){
      n("package %s;", dstPackage);
    }
    n("import java.util.*;");
    n("import java.io.*;");
    n("import com.github.kmizu.yapp.runtime.*;");
    n("public class %s extends %s<%s> {", node.getName(), "AbstractPackratParser", startType);
    indent();
    n("private int %s = 0;", DEPTH);
    n("private boolean %s() { return %s == 0; }", IS_ZERO, DEPTH);
    n("private void tryOptimize(int cursor) {");
    n("  if(%s == 0) {", DEPTH);
    n("    %s(cursor);", BASE_NAME);
    n("  }");
    n("}");
    Map<Symbol, Set<Character>> map = node.getNameToCharSet();
    for(Symbol name : map.keySet()){
      n("private static final CharacterSet %s = new TreeCharacterSet();", name);
    }
    int count = 0;
    n("private static void initialize0() {");
    indent();
    for(Map.Entry<Symbol, Set<Character>> e : map.entrySet()){
      for(Character c : e.getValue()){
        n("%s.add('%s');", e.getKey(), escape(Character.toString(c)));
        count++;
        if(count % 1000 == 0){
          dedent();
          n("}");
          n("private static void initialize%d() {", count / 1000);
          indent();
        }
      }
    }
    dedent();
    n("}");
    n("static {");
    indent();
    for(int i = 0; i <= count / 1000; i++){
      n("initialize%d();", i);
    }
    dedent();
    n("}");
    n("");
    n("public %s(String input) {", node.getName());
    n("  super(input);");
    n("}");
    n("");
    n("public %s(Reader input) {", node.getName());
    n("  super(input);");
    n("}");
    n("");
    n("");
    if(joinColumns) {
      int columnIndex = 0;
      int chunkIndex = 0;
      Map<String, Integer> chunkMap = CollectionUtil.map();
      n("private static class ResultColumn {");
      for(Function fn : node.getRules()){
        if(!fn.isMemoized()) continue;
        if(columnIndex == 0) {
          n("  static final class Chunk%d {", chunkIndex);
        }
        chunkMap.put(MEMO_PREFIX + "_" + fn.getName(), chunkIndex);
        n("    Result<%s> %s;", fn.getType(), MEMO_PREFIX + "_" + fn.getName());
        if(columnIndex == CHUNK_SIZE - 1){
          n("  }");          
          columnIndex = 0;
          chunkIndex++;
        }else {
          columnIndex++;
        }
      }
      
      if(columnIndex != 0) {
        n("  }");
      }else{
        chunkIndex--;
      }
      for(int i = 0; i <= chunkIndex; i++){
        n("  Chunk%d chunk%d;", i, i);
      }
      n("}");
      n("private CircularSpreadArray<ResultColumn> columns = new CircularSpreadArray<ResultColumn>();");
      for(Function fn : node.getRules()){
        Symbol name = fn.getName();
        Symbol type = fn.getType();
        String func = FUNC_PREFIX + "_" + name;
        if(fn.isMemoized()){
          String memo = MEMO_PREFIX + "_" + name;
          n("public Result<%s> %s(int cursor) {", type, name);
          n("  ResultColumn column = columns.get(%s(cursor));", INDEX_METHOD);
          n("  if(column == null){");
          n("    column = new ResultColumn();");
          n("    if(cursor >= %s()){", BASE_NAME);
          n("      columns.set(%s(cursor), column);", INDEX_METHOD);
          n("    }");
          n("  }");
          int chunkNumber = chunkMap.get(memo);
          n("  if(column.chunk%d == null){", chunkNumber);
          n("    column.chunk%d = new ResultColumn.Chunk%d();", chunkNumber, chunkNumber);
          n("  }");
          n("  if(column.chunk%d.%s == null){", chunkNumber, memo);
          n("    column.chunk%d.%s = %s(cursor);", chunkNumber, memo, func);
          n("  }");
          n("  return column.chunk%d.%s;", chunkNumber, memo);
          n("}");
        }else{
          n("public Result<%s> %s(int cursor) {", type, name);
          n("  return %s(cursor);", func);
          n("}");
        }
      }
      n("");
      n("@Override");
      n("protected void truncate(int toIndex) {");
      n("  super.truncate(toIndex);");
      n("  columns.truncate(toIndex);");
      n("}");
    }else {
      for(Function fn : node.getRules()){
        n("");
        Symbol name = fn.getName();
        Symbol type = fn.getType();
        String func = FUNC_PREFIX + "_" + name;
        if(fn.isMemoized()){
          String memo = MEMO_PREFIX + "_" + name;
          n("private CircularSpreadArray<Result<%s>> %s = new CircularSpreadArray<Result<%s>>();", type, memo, type);
          n("public Result<%s> %s(int cursor) {", type, name);
          n("  Result<%s> result = %s.get(%s(cursor));", type, memo, INDEX_METHOD);
          n("  if(result == null){");
          n("    result = %s(cursor);", func);
          n("    if(cursor >= %s()){", BASE_NAME);
          n("      %s.set(%s(cursor), result);", memo, INDEX_METHOD);
          n("    }");
          n("  }");
          n("  return result;");
          n("}");
        }else{
          n("public Result<%s> %s(int cursor) {", type, name);
          n("  return %s(cursor);", func);
          n("}");
        }
      }
      n("");
      n("@Override");
      n("protected void truncate(int toIndex) {");
      n("  super.truncate(toIndex);");
      for(Function fn : node){
        if(fn.isMemoized()){
          n("  %s.truncate(toIndex);", MEMO_PREFIX + "_" + fn.getName());
        }
      }
      n("}");
    }
    n("");
    n("public Result<%s> %s() {", startType, FUNC_PREFIX);
    n("  return %s(0);", startName);
    n("}");
    for(Function fn : node){
      fn.accept(this, context);
    }
    dedent();
    n("}");
    return null;
  }


  @Override
  public Void visit(RewindCursor node, CodeContext context) {
    n("%s = %s;", CURSOR_NAME, node.getVar());
    return null;
  }


  @Override
  public Void visit(SetSemanticValue node, CodeContext context) {
    n("%s = %s;", VALUE_NAME, node.getCode());
    return null;
  }

  private void indent() {
    indentLevel++;
  }
  
  private void dedent() {
    indentLevel--;
  }
  
  private void n(String format, Object... args) {
    int spaces = indentLevel * INDENT_SPACE;
    for(int i = 0; i < spaces; i++) out.print(" ");
    out.printf(format, args);
    out.println();
  }
  
  private String escape(String src) {
    StringBuffer buffer = new StringBuffer();
    for(int i = 0; i < src.length(); i++){
      char c = src.charAt(i);
      switch(c){
      case '\r':
        buffer.append("\\r");
        break;
      case '\n':
        buffer.append("\\n");
        break;
      case '\t':
        buffer.append("\\t");
        break;
      case '\b':
        buffer.append("\\b");
        break;
      case '\f':
        buffer.append("\\f");
        break;
      case '\'':
        buffer.append("\\'");
        break;
      case '\\':
        buffer.append("\\\\");
        break;
      default:
        buffer.append(c);
        break;
      }
    }
    return new String(buffer);
  }
}
