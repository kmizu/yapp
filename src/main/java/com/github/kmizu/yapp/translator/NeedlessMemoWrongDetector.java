/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.Rule;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Wildcard;

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

