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

public class NonterminalCollector extends Ast.Visitor<Set<Symbol>, NonterminalCollector.Context> 
  implements Translator<Ast.Expression, Set<Symbol>> {

  static class Context {
  }
  
  public NonterminalCollector() {
  }
  
  public Set<Symbol> translate(Expression from) {
    return from.accept(this, null);
  }
  
  @Override
  protected Set<Symbol> visit(Wildcard node, Context context) {
    return empty();
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
      nonterminals = union(nonterminals, expressions.get(i).accept(this, context));
    }

    nonterminals = union(nonterminals, expressions.get(expressions.size() - 1).accept(this, context));

    return nonterminals;
  }

  @Override
  public Set<Symbol> visit(Cut node, Context context) {
    return empty();
  }

  @Override
  public Set<Symbol> visit(Fail node, Context context) {
    return empty();
  }

  @Override
  public Set<Symbol> visit(AndPredicate node, Context context) {
    return node.body().accept(this, context);
  }

  @Override
  public Set<Symbol> visit(NotPredicate node, Context context) {
    return node.body().accept(this, context);
  }

  @Override
  public Set<Symbol> visit(Empty node, Context context) {
    return empty();
  }

  @Override
  public Set<Symbol> visit(NonTerminal node, Context context) {
    Symbol name = node.name();
    Set<Symbol> n = empty();
    n.add(name);
    return n;
  }

  @Override
  public Set<Symbol> visit(Repetition node, Context context) {
    return node.body().accept(this, context);
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
  public Set<Symbol> visit(CharClass node, Context context) {
    return empty();
  }

  private static <T> Set<T> union(Set<T> a, Set<T> b) {
    Set<T> c = new HashSet<T>();
    c.addAll(a);
    c.addAll(b);
    return c;
  }
  
  private static <T> Set<T> empty() {
    return new HashSet<T>();
  }
}

