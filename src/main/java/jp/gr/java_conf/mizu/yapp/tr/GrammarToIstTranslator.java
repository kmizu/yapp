/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.tr;

import static jp.gr.java_conf.mizu.yapp.Symbol.*;
import static jp.gr.java_conf.mizu.yapp.util.CollectionUtil.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.CompilationException;
import jp.gr.java_conf.mizu.yapp.Ist;
import jp.gr.java_conf.mizu.yapp.SemanticException;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.SymbolGenerator;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.CharClass;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Empty;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Fail;
import jp.gr.java_conf.mizu.yapp.Ast.NonTerminal;
import jp.gr.java_conf.mizu.yapp.Ast.NotPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Repetition;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;
import jp.gr.java_conf.mizu.yapp.Ast.SetValueAction;
import jp.gr.java_conf.mizu.yapp.Ast.StringLiteral;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.Ist.Accept;
import jp.gr.java_conf.mizu.yapp.Ist.ActionStatement;
import jp.gr.java_conf.mizu.yapp.Ist.BackupCursor;
import jp.gr.java_conf.mizu.yapp.Ist.Block;
import jp.gr.java_conf.mizu.yapp.Ist.DecrDepth;
import jp.gr.java_conf.mizu.yapp.Ist.EscapeFrom;
import jp.gr.java_conf.mizu.yapp.Ist.Function;
import jp.gr.java_conf.mizu.yapp.Ist.GenerateSuccess;
import jp.gr.java_conf.mizu.yapp.Ist.IncrDepth;
import jp.gr.java_conf.mizu.yapp.Ist.Loop;
import jp.gr.java_conf.mizu.yapp.Ist.MatchAny;
import jp.gr.java_conf.mizu.yapp.Ist.MatchCharClass;
import jp.gr.java_conf.mizu.yapp.Ist.MatchRule;
import jp.gr.java_conf.mizu.yapp.Ist.MatchString;
import jp.gr.java_conf.mizu.yapp.Ist.NewCursorVar;
import jp.gr.java_conf.mizu.yapp.Ist.Nop;
import jp.gr.java_conf.mizu.yapp.Ist.ParserUnit;
import jp.gr.java_conf.mizu.yapp.Ist.RewindCursor;
import jp.gr.java_conf.mizu.yapp.Ist.SetSemanticValue;
import jp.gr.java_conf.mizu.yapp.Ist.Statement;
import jp.gr.java_conf.mizu.yapp.Ist.Var;

public class GrammarToIstTranslator extends Ast.Visitor<Ist.Statement, GrammarToIstTranslator.Context> 
  implements Translator<Ast.Grammar, Ist.ParserUnit> {
  private boolean eliminateMemo;
  
  static class Context {
    private Map<Symbol, Boolean> ruleToMemoize;
    private Map<Symbol, Set<Character>> nameToCharSet;
    private Map<Symbol, Symbol> typeMaps;
    private Set<Symbol> ruleNames;
    private Symbol currentRuleName;
    private Stack<Symbol> failures;
    private SymbolGenerator symgen;
    private SymbolGenerator vargen;
    private boolean cutEnabled;

    public Context(Map<Symbol, Boolean> ruleToMemoize) {
      this.ruleToMemoize = ruleToMemoize;
      this.ruleNames = new HashSet<Symbol>();
      this.nameToCharSet = new TreeMap<Symbol, Set<Character>>();
      this.failures = new Stack<Symbol>();
      this.typeMaps  = new HashMap<Symbol, Symbol>();
      this.cutEnabled = false;
    }

    public boolean shouldBeMemoized(Symbol rule) {
      return ruleToMemoize == null ?  true : ruleToMemoize.get(rule);
    }

    public void clearTypeMaps() {
      typeMaps.clear();
    }

    public void putType(Symbol ruleName, Symbol type) {
      typeMaps.put(ruleName, type);
    }

    public Symbol getType(Symbol ruleName) {
      return typeMaps.get(ruleName);
    }

    public Set<Symbol> getRuleNames() {
      return ruleNames;
    }

    public void putCharSet(Symbol name, Set<Character> charSet) {
      nameToCharSet.put(name, charSet);
    }

    public Map<Symbol, Set<Character>> getNameToCharSet() {
      return nameToCharSet;
    }

    public void push(Symbol label) {
      failures.push(label);
    }

    public Symbol pop() {
      return failures.pop();
    }

    public Symbol peek() {
      return failures.peek();
    }

    public boolean isEmpty() {
      return failures.isEmpty();
    }

    public int size() {
      return failures.size();
    }

    public void setCurrentRuleName(Symbol currentRuleName) {
      this.currentRuleName = currentRuleName;
      this.failures = new Stack<Symbol>();
      this.symgen = new SymbolGenerator(currentRuleName.getKey().toUpperCase());
      this.vargen = new SymbolGenerator("var");
    }

    public Symbol getCurrentRuleName() {
      return currentRuleName;
    }

    public void setCutEnabled(boolean cutEnabled){
      this.cutEnabled = cutEnabled;
    }

    public boolean isCutEnabled() {
      return cutEnabled;
    }

    public Symbol gensym() {
      return symgen.gensym();
    }
    
    public Symbol genvar() {
      return vargen.gensym();
    }
  }
  
  public GrammarToIstTranslator(boolean eliminateMemo) {
    this.eliminateMemo = eliminateMemo;
  }
  
  public Ist.ParserUnit translate(Ast.Grammar node) {
    Context context = new Context(
      eliminateMemo ? new NeedlessMemoDetector().detect(node) : null
    );
    List<Ist.Function> rules = list();
    for(Ast.Rule rule : node){
      Symbol name = rule.name();
      Symbol type = rule.type() == null ? intern("Object") : rule.type();
      context.putType(name, type);
    }
    for(Ast.Rule rule : node){
      rules.add(translate(rule, context));
    }
    Rule start = node.iterator().next();
    Symbol startName = start.name();
    Symbol startType = start.type();
    if(startType == null) startType = intern("Object");
    return new Ist.ParserUnit(
      node.pos(),
      node.name(),
      context.getNameToCharSet(), 
      startName,
      startType,
      rules
    );
  }
  
  public Ist.Function translate(Rule node, Context context) {
    context.setCurrentRuleName(node.name());
    return new Ist.Function(
      node.pos(), node.name(), 
      context.getType(node.name()), 
      node.code(),
      context.shouldBeMemoized(node.name()),
      new Ist.Block(
        node.body().pos(), null,
        node.body().accept(this, context),
        new Ist.Accept(node.body().pos())
      )
    );
  }
  
  @Override
  protected Statement visit(Action node, Context context) {
    return new Ist.Block(
      node.pos(),
      null,
      node.body().accept(this, context),
      new Ist.ActionStatement(node.pos(), node.code())
    );
  }

  @Override
  protected Statement visit(N_Alternation node, Context context) {
    boolean backupCutEnabled = context.isCutEnabled();    
    List<Ist.Statement> stmts = list();
    
    List<Expression> expressions = node.body();
    Symbol success = context.gensym();
    Symbol swap = context.genvar();
    stmts.add(new Ist.NewCursorVar(node.pos(), swap));
    for(int i = 0; i < expressions.size() - 1; i++){
      Expression e = expressions.get(i);
      Symbol failure = context.gensym();
      context.setCutEnabled(true);
      context.push(failure);
      Ist.Statement body = accept(e, context);
      //カット演算子によってスタックが変化する可能性があるため
      if(context.isCutEnabled()){
        context.pop();
      }
      stmts.add(
        new Ist.Block(
          e.pos(),
          failure,
          new Ist.BackupCursor(e.pos(), swap),
          new Ist.IncrDepth(e.pos()),
          body,
          context.isCutEnabled() ?  new Ist.DecrDepth(e.pos()) :
                                    new Ist.Nop(e.pos()),
          new Ist.EscapeFrom(e.pos(), success)
        )
      );
      stmts.add(new Ist.RewindCursor(e.pos(), swap));
      stmts.add(new Ist.DecrDepth(e.pos()));
    }
    context.setCutEnabled(false);
    stmts.add(accept(expressions.get(expressions.size() - 1), context));

    context.setCutEnabled(backupCutEnabled);
    return new Ist.Block(
      node.pos(), success,
      stmts
    );
  }

  @Override
  protected Statement visit(CharClass node, Context context) {
    boolean positive = node.positive;
    Set<Character> chars = new HashSet<Character>();
    for(CharClass.Element e:node.elements) {
      if(e instanceof CharClass.Char) {
        chars.add(((CharClass.Char)e).value);
      }else {
        char rangeS = ((CharClass.Range)e).start;
        char rangeE = ((CharClass.Range)e).end;
        for(int c = rangeS; c <= rangeE; c++) chars.add((char)c);
      }
    }
    Symbol name = context.gensym();
    context.putCharSet(name, chars);
    Ist.Var var = new Ist.Var(node.var(), Symbol.intern("Character"));
    if(context.isEmpty()){
      return new Ist.MatchCharClass(node.pos(), name, var, positive, null);
    }else{
      return new Ist.MatchCharClass(node.pos(), name, var, positive, context.peek());
    }
  }

  @Override
  protected Statement visit(Cut node, Context context) {
    if(context.isCutEnabled() && !context.isEmpty()){
      context.pop();
      context.setCutEnabled(false);
      return new Ist.DecrDepth(node.pos());
    }
    return new Ist.Nop(node.pos());
  }

  @Override
  protected Statement visit(Empty node, Context context) {
    return new Ist.GenerateSuccess(node.pos());
  }

  @Override
  protected Statement visit(Fail node, Context context) {
    return context.isEmpty() ? new Ist.Fail(node.pos()) :
                                new Ist.EscapeFrom(node.pos(), context.peek());
  }

  @Override
  protected Statement visit(NonTerminal node, Context context) {
    Ist.Var var = null;
    if(node.var() != null) {
      var = new Ist.Var(node.var(), context.getType(node.var()));
    }
    return new Ist.MatchRule(
      node.pos(), var, node.name(), context.isEmpty() ? null : context.peek()
    );
  }

  @Override
  protected Statement visit(Repetition node, Context context) {
    boolean backupCutEnabled = context.isCutEnabled();
    Symbol failure = context.gensym();
    context.setCutEnabled(true);
    context.push(failure);
    Ist.Statement body = node.body().accept(this, context);
    //カット演算子によってスタックが変化する可能性があるため
    if(context.isCutEnabled()){
      context.pop();
    }
    Symbol swap = context.genvar();
    Ist.Block block = new Ist.Block(
      node.pos(), null,
      new Ist.NewCursorVar(node.pos(), swap),
      new Ist.Loop(
        node.body().pos(), failure,
        new Ist.BackupCursor(node.body().pos(), swap),
        new Ist.IncrDepth(node.body().pos()),
        body,
        context.isCutEnabled() ? new Ist.DecrDepth(node.body().pos()) :
                                 new Ist.Nop(node.body().pos())
      ),
      new Ist.RewindCursor(node.body().pos(), swap),
      new Ist.DecrDepth(node.body().pos()),
      new Ist.GenerateSuccess(node.body().pos())
    );
    context.setCutEnabled(backupCutEnabled);
    return block;
  }
  
  /**
   *   !X
   *    |
   *    v
   * LABEL: {
   *   [compile(X);]
   *   fail
   * }
   * success
   */
  @Override
  protected Statement visit(NotPredicate node, Context context) {
    boolean backupCutEnabled = context.isCutEnabled();
    Symbol failure = context.gensym();
    Symbol swap = context.genvar();
    context.setCutEnabled(false);
    context.push(failure);
    Ist.Statement body = node.body().accept(this, context);
    context.pop();
    Ist.Block block = new Ist.Block(
      node.pos(), null,
      new Ist.NewCursorVar(node.pos(), swap),
      new Ist.BackupCursor(node.pos(), swap),
      new Ist.IncrDepth(node.pos()),
      new Ist.Block(
        node.pos(), failure,
        body,
        new Ist.RewindCursor(node.pos(), swap),
        new Ist.DecrDepth(node.pos()),
        context.isEmpty() ? 
          new Ist.Fail(node.pos()) :
          new Ist.Block(
            node.pos(), null,
            new Ist.GenerateFailure(node.pos(), "not " + node.body().toString()),
            new Ist.EscapeFrom(node.pos(), context.peek())
          )
      ),
      new Ist.RewindCursor(node.pos(), swap),
      new Ist.DecrDepth(node.pos()),
      new Ist.GenerateSuccess(node.pos())
    );
    context.setCutEnabled(backupCutEnabled);
    return block;
  }
  
  @Override
  protected Statement visit(AndPredicate node, Context context) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Statement visit(N_Sequence node, Context context) {
    List<Statement> stmts = list();
    for(Expression e : node.body()){
      stmts.add(e.accept(this, context));
    }
    return new Ist.Block(node.pos(), null, stmts.toArray(new Statement[0]));
  }

  @Override
  protected Statement visit(SetValueAction node, Context context) {
    return new Ist.Block(
      node.pos(),
      null,
      node.body().accept(this, context),
      new Ist.SetSemanticValue(node.pos(), node.code())
    );  
  }

  @Override
  protected Statement visit(StringLiteral node, Context context) {
    return new Ist.MatchString(
      node.pos(), 
      new Ist.Var(node.var(), Symbol.intern("String")), 
      node.value(),
      context.isEmpty() ? null : context.peek()
    );
  }

  @Override
  protected Statement visit(Wildcard node, Context context) {
    return new Ist.MatchAny(
      node.pos(), 
      new Ist.Var(node.var(), Symbol.intern("String")), 
      context.isEmpty() ? null : context.peek()
    );
  }  
}
