/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Pair;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.CharClass;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Empty;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Fail;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.NonTerminal;
import jp.gr.java_conf.mizu.yapp.Ast.NotPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Optional;
import jp.gr.java_conf.mizu.yapp.Ast.Repetition;
import jp.gr.java_conf.mizu.yapp.Ast.RepetitionPlus;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;
import jp.gr.java_conf.mizu.yapp.Ast.SetValueAction;
import jp.gr.java_conf.mizu.yapp.Ast.StringLiteral;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;

public class NeedlessMemoWrongDetector extends Ast.Visitor<Set<Symbol>, NeedlessMemoWrongDetector.Context>
  implements Translator<Grammar, Pair<Ast.Grammar, Set<Symbol>>> {
  private Map<Symbol, Ast.Expression> map = new HashMap<Symbol, Ast.Expression>();
  private NonterminalCollector collector = new NonterminalCollector();

  static class Context {    
    Stack<Boolean> cutUsable;
    Stack<Boolean> backtrack;
    
    Context() {
      cutUsable = new Stack<Boolean>();
      backtrack = new Stack<Boolean>();
    }        
  }
  
  public NeedlessMemoWrongDetector() {
  }
  
  public Pair<Ast.Grammar, Set<Symbol>> translate(Grammar from) {
    return Pair.make(from, detect(from));
  }

  public Set<Symbol> detect(Grammar grammar) {
    return grammar.accept(this, null);
  }

  @Override
  public Set<Symbol> visit(Action node, Context context) {
    return node.body().accept(this, context);
  }
  
  @Override
  public Set<Symbol> visit(SetValueAction node, Context context) {
    return node.body().accept(this, context);
  }

  @Override
  public Set<Symbol> visit(N_Alternation node, Context context) {
    List<Expression> expressions = node.body();
    Set<Symbol> nonterminals = empty();

    for(int i = 0; i < expressions.size() - 1; i++){
      context.backtrack.push(true);
      context.cutUsable.push(true);
      int size = context.backtrack.size();
      {
        nonterminals = union(nonterminals, expressions.get(i).accept(this, context));
      }
      //カット演算子によってスタックが変化する可能性があるため
      if(context.backtrack.size() == size){
        context.backtrack.pop();
        context.cutUsable.pop();
      }
    }

    nonterminals = union(nonterminals, expressions.get(expressions.size() - 1).accept(this, context));

    return nonterminals;
  }

  @Override
  public Set<Symbol> visit(Cut node, Context context) {
    if(context.cutUsable.peek()){
      context.backtrack.pop();
      context.cutUsable.pop();
    }
    return empty();
  }

  @Override
  public Set<Symbol> visit(Fail node, Context context) {
    return empty();
  }

  @Override
  public Set<Symbol> visit(AndPredicate node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(false);
    node.body().accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    return empty();
  }

  @Override
  public Set<Symbol> visit(NotPredicate node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(false);
    node.body().accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    return empty();
  }

  @Override
  public Set<Symbol> visit(Empty node, Context context) {
    return empty();
  }

  @Override
  public Set<Symbol> visit(Grammar node, Context context) {
    Set<Symbol> all = empty();
    for(Rule rule : node){
      map.put(rule.name(), rule.body());
      all.add(rule.name());
    }
    
    Symbol start = node.getRules().get(0).name();
    Expression e = map.get(start);    
    context = new Context();
    context.backtrack.push(false);
    context.cutUsable.push(false);
    Set<Symbol> backtracks = e.accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    
    boolean changed = true;
    while(changed) {
      System.out.println(backtracks);
      Set<Symbol> newBacktracks = empty();
      for(Symbol s : backtracks) {
        context = new Context();
        context.backtrack.push(false);
        context.cutUsable.push(false);
        e = map.get(s);
        newBacktracks = union(newBacktracks, collector.translate(e));
      }
      newBacktracks = union(newBacktracks, backtracks);
      if(newBacktracks.size() == backtracks.size()) {
        changed = false;
      } else {
        backtracks = newBacktracks;
        changed = true;
      }
    }
    
    return backtracks;
  }

  @Override
  public Set<Symbol> visit(NonTerminal node, Context context) {
    Symbol name = node.name();
    Set<Symbol> n = empty();
    if(context.backtrack.peek()){
      n.add(name);
    }
    return n;
  }

  @Override
  public Set<Symbol> visit(Repetition node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(true);
    int size = context.backtrack.size();
    {
      node.body().accept(this, context);
    }
    //カット演算子によってスタックが変化する可能性があるため
    if(context.backtrack.size() == size){
      context.backtrack.pop();
      context.cutUsable.pop();
    }

    return empty();
  }

  @Override
  public Set<Symbol> visit(RepetitionPlus node, Context context) {
    throw new UnsupportedOperationException("visit(RepetitionPlus node, Context context)");
  }

  @Override
  public Set<Symbol> visit(Optional node, Context context) {
    throw new UnsupportedOperationException("visit(Optional node, Context context)");
  }

  @Override
  public Set<Symbol> visit(N_Sequence node, Context context) {
    List<Expression> expressions = node.body();
    Set<Symbol> nonterminals = empty();
    for(Expression expression : expressions.subList(0, expressions.size() - 1)){
      nonterminals = union(nonterminals, expression.accept(this,  context));
    }
    nonterminals = union(nonterminals, expressions.get(expressions.size() - 1).accept(this, context));
    return nonterminals;
  }

  @Override
  public Set<Symbol> visit(StringLiteral node, Context context) {
    return empty();
  }

  
  @Override
  protected Set<Symbol> visit(Wildcard node, Context context) {
    return empty();
  }
  
  @Override
  public Set<Symbol> visit(CharClass node, Context context) {
    return empty();
  }

  private static <T> List<T> list(T... args) {
    return new ArrayList<T>(Arrays.asList(args));
  }
  
  private static <T> Set<T> union(Set<T> a, Set<T> b) {
    Set<T> c = new HashSet<T>();
    c.addAll(a);
    c.addAll(b);
    return c;
  }
  
  private static <T> Set<T> diff(Set<T> a, Set<T> b) {
    Set<T> c = new HashSet<T>();
    c.addAll(a);
    c.removeAll(b);
    return c;
  }
  
  private static <T> Set<T> set(T... args) {
    Set<T> set = new HashSet<T>();
    for(T e : args) set.add(e);
    return set;
  }

  
  private static <T> Set<T> empty() {
    return new HashSet<T>();
  }
}

