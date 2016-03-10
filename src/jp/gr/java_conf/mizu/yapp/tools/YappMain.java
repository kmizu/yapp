package jp.gr.java_conf.mizu.yapp.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.CompilationException;
import jp.gr.java_conf.mizu.yapp.Pair;
import jp.gr.java_conf.mizu.yapp.SemanticException;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;

import jp.gr.java_conf.mizu.yapp.parser.ParseException;
import jp.gr.java_conf.mizu.yapp.parser.Token;
import jp.gr.java_conf.mizu.yapp.parser.YappParser;
import jp.gr.java_conf.mizu.yapp.tools.CommandLineParser.OptionType;
import jp.gr.java_conf.mizu.yapp.tr.AstPrinter;
import jp.gr.java_conf.mizu.yapp.tr.AutoCutInserter;
import jp.gr.java_conf.mizu.yapp.tr.AutoCutInserterUsingFirstSet;
import jp.gr.java_conf.mizu.yapp.tr.AutoCutInserterUsingFollowSet;
import jp.gr.java_conf.mizu.yapp.tr.BoundedQualifierCounter;
import jp.gr.java_conf.mizu.yapp.tr.CodeGeneratorFromIst;
import jp.gr.java_conf.mizu.yapp.tr.ComposableTranslator;
import jp.gr.java_conf.mizu.yapp.tr.CutCounter;
import jp.gr.java_conf.mizu.yapp.tr.DeadRuleEliminator;
import jp.gr.java_conf.mizu.yapp.tr.DebugPrinterForFirstSet;
import jp.gr.java_conf.mizu.yapp.tr.GrammarToIstTranslator;
import jp.gr.java_conf.mizu.yapp.tr.IdentityTranslator;
import jp.gr.java_conf.mizu.yapp.tr.MacroExpander;
import jp.gr.java_conf.mizu.yapp.tr.MacroExpanderEx;
import jp.gr.java_conf.mizu.yapp.tr.MemoizedCountPrinter;
import jp.gr.java_conf.mizu.yapp.tr.NeedlessMemoDetector;
import jp.gr.java_conf.mizu.yapp.tr.Parser;
import jp.gr.java_conf.mizu.yapp.tr.RuleCounter;
import jp.gr.java_conf.mizu.yapp.tr.RuleInliner;
import jp.gr.java_conf.mizu.yapp.tr.SpaceComplexityChecker;
import jp.gr.java_conf.mizu.yapp.tr.SyntaxSugarExpander;
import jp.gr.java_conf.mizu.yapp.tr.Translator;
import jp.gr.java_conf.mizu.yapp.tr.UnboundedContext;
import jp.gr.java_conf.mizu.yapp.tr.VoidTranslator;

import static jp.gr.java_conf.mizu.yapp.util.SystemProperties.*;


public class YappMain {
  private final String USAGE;
  
  private File srcFile;
  private File dstDir;
  
  CommandLineParser parser = new CommandLineParser();
  
  public YappMain(String... args) throws CommandLineException {    
    parser.opt("pre",      "--pre",      OptionType.STR,  "--pre        specify parser class' prefix")
          .opt("time",     "--time",     OptionType.NONE, "--time       measure time elapsed")
          .opt("pkg",      "--pkg",      OptionType.STR,  "--pkg        specify parser class' package")
          .opt("pm",       "--pm",       OptionType.NONE, "--pm         print the number of rules that should be memoized")
          .opt("edr",      "--edr",      OptionType.NONE, "--edr        eliminate dead rules")
          .opt("em",       "--em",       OptionType.NONE, "--em        eliminate needless memoizations")
          .opt("reg",      "--reg",      OptionType.NONE, "--reg        insert cut automatically by regex like method")
          .opt("Onj",      "--Onj",      OptionType.NONE, "--Onj        turn off 'join column optimization'")
          .opt("ac",       "--ac",       OptionType.NONE, "--ac         turn on 'auto cut insertion' optimization")
          .opt("inl",      "--inl",      OptionType.INT,  "--inl        inline nonterminal expression")
          .opt("ACfirst",  "--ACfirst",  OptionType.INT,  "--ACfirst    auto cut insertion by first set method")
          .opt("ACfollow", "--ACfollow", OptionType.NONE, "--ACfollow   auto cut insertion by follow set method")
          .opt("space",    "--space",    OptionType.NONE,  "--space      calculate space complexity of specified rule");
    StringBuilder usage = new StringBuilder();
    usage.append("Usage: java jp.gr.java_conf.mizu.yapp.tools.YappMain [options ...] <file name> [<dest dir>]" + LINE_SEPARATOR)
         .append("options:" + LINE_SEPARATOR);
    for(String desc:parser.descriptions) {
      usage.append("  " + desc + LINE_SEPARATOR);
    }
    USAGE = new String(usage);
    parser.parse(args);
    if(parser.values.size() == 0) {
      throw new CommandLineException(USAGE);
    }
    srcFile = new File(parser.values.get(0));
    dstDir = parser.values.size() > 1 ?          
      new File(parser.values.get(1)) :
      new File(".");  
  }
  
  public static void main(String[] args) throws Exception {
    try {
      new YappMain(args).generate();
    }catch(CommandLineException e) {
      System.err.println(e.getMessage());
    }
  }
  
  public boolean generate() throws IOException {
    try {
      generateMain();
      return true;
    }catch(CompilationException ex){
      if(ex.getReason() instanceof ParseException){
        ParseException e = (ParseException)ex.getReason();
        Token error = e.currentToken.next;
        String expected = e.tokenImage[e.expectedTokenSequences[0][0]];
        System.err.printf(
          "%d:%d: expected %s, but %s.%n", 
          error.beginLine, error.beginColumn,
          expected, error
        );
      }else if(ex.getReason() instanceof SemanticException){
        SemanticException e = (SemanticException)ex.getReason();
        System.err.println(e.getErrorMessage());
      }else{
        ex.printStackTrace();
      }
      return false;
    }
  }
  
  private static <T> void removeDuplicated(List<T> list) {
    Set<T> set = new HashSet<T>();
    for(Iterator<T> it = list.iterator(); it.hasNext();) {
      T t = it.next();
      if(set.contains(t)) {
        it.remove();
      }
      set.add(t);
    }
  }

  private void generateMain() {
    long start = -1;
    if(has("time")) {
      start = System.currentTimeMillis();
    }
    if(has("ac")) {
      Translator<File, Void> compiler = ComposableTranslator.<File>empty()
        .compose(new Parser(getString("pre", "")))
        .compose(new MacroExpander())
        .compose(new SyntaxSugarExpander())
        .compose(has("ACfirst") ? new AutoCutInserterUsingFirstSet(getInt("ACfirst")) : new IdentityTranslator<Grammar>())
        .compose(new CutCounter())
        .compose(has("ACfollow") ? new AutoCutInserterUsingFollowSet() : new IdentityTranslator<Grammar>())
        .compose(new CutCounter())
        .compose(new AstPrinter())
        .compose(new GrammarToIstTranslator(false))
        .compose(new CodeGeneratorFromIst(has("pkg") ? getString("pkg") : null, dstDir, !has("Onj")));        
      compiler.translate(srcFile);
    }else if(has("em")) {   
      Translator<File, Void> compiler = ComposableTranslator.<File>empty()
        .compose(new Parser(getString("pre", "")))
        .compose(new MacroExpander())
        .compose(new SyntaxSugarExpander())
        .compose(new NeedlessMemoDetector())
        .compose(has("pm") ? new MemoizedCountPrinter() : new VoidTranslator<Pair<Ast.Grammar, Map<Symbol, Boolean>>>());
      compiler.translate(srcFile);
    }else if(has("reg")){
      int inlineLimit = has("inl") ? getInt("inl") : 1;
      Translator<File, Void> compiler = ComposableTranslator.<File>empty()
        .compose(new Parser(getString("pre", "")))
        .compose(new MacroExpander())
        .compose(new SyntaxSugarExpander())
        .compose(inlineLimit > 0 ? new RuleInliner(inlineLimit) : new IdentityTranslator<Grammar>())
        .compose(new AutoCutInserter())
        .compose(has("edr") ? new DeadRuleEliminator() : new IdentityTranslator<Grammar>())
        .compose(new CutCounter())
        .compose(new AstPrinter())
        .compose(new VoidTranslator<Ast.Grammar>());
      compiler.translate(srcFile);
    }else if(has("space")) {
      Translator<File, List<UnboundedContext>> compiler = ComposableTranslator.<File>empty()
        .compose(new Parser(getString("pre", "")))
        .compose(new MacroExpanderEx())
        .compose(new RuleCounter())
        .compose(new BoundedQualifierCounter())
        .compose(new SpaceComplexityChecker()); 
      List<UnboundedContext> unboundedExpressions = compiler.translate(srcFile);
      if(unboundedExpressions.size() == 0) {
        System.out.printf("A parser generated from this grammar requires only bounded space.%n");        
      }else {
        System.out.printf("A parser generated from this grammar may require unbounded space%n");
        System.out.printf("because the following expressions are unbounded:%n");
        for(UnboundedContext c:unboundedExpressions) {
          System.out.printf("%s in %s%n", c.unboundedExpression, c.parent);
        }
      }
    }else {
      Translator<File, Void> compiler = ComposableTranslator.<File>empty()
        .compose(new Parser(getString("pre", "")))
        .compose(new MacroExpander())
        .compose(new SyntaxSugarExpander())
        .compose(new RuleCounter())
        .compose(new GrammarToIstTranslator(has("em")))
        .compose(new CodeGeneratorFromIst(has("pkg") ? getString("pkg") : null, dstDir, !has("onj")));
      compiler.translate(srcFile);
    }
    if(has("time")) {
      System.out.printf("time: %d[ms]%n", System.currentTimeMillis() - start);
    }
  }
  
  private boolean has(String key) {
    return parser.hasOption(key);
  }
  
  private int getInt(String key) {
    return parser.getInt(key);
  }
  
  private int getInt(String key, int defaultValue) {
    return parser.getInt(key, defaultValue);
  }
  
  private String getString(String key) {
    return parser.getString(key);
  }
  
  private String getString(String key, String defaultValue) {
    return parser.getString(key, defaultValue);
  }
}
