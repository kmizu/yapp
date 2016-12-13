/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.translator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Wildcard;

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

