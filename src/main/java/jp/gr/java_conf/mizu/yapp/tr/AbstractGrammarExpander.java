/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.tr;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.MacroCall;
import jp.gr.java_conf.mizu.yapp.Ast.MacroDefinition;
import jp.gr.java_conf.mizu.yapp.Ast.MacroVariable;
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
import jp.gr.java_conf.mizu.yapp.Ast.SemanticPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;
import jp.gr.java_conf.mizu.yapp.Ast.SetValueAction;
import jp.gr.java_conf.mizu.yapp.Ast.StringLiteral;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;

public abstract class AbstractGrammarExpander<T> extends Visitor<Expression, T> 
  implements Translator<Grammar, Grammar> {
  
  public Grammar translate(Grammar from) {
    return expand(from);
  }
  
  public Grammar expand(Grammar node) {
    return expand(node, newContext());
  }
  
  public T newContext() {
    return null;
  }
  
  public Grammar expand(Grammar node, T context) {
    List<Rule> rules = new ArrayList<Rule>(node.getRules().size());
    for(Rule rule : node) {
      rule = expand(rule, context);
      rules.add(rule);
    }
    return new Grammar(node.pos(), node.name(), node.macros(), rules);
  }
  
  public Rule expand(Rule node, T context) {
    Expression e = node.body().accept(this, context);
    return new Rule(node.pos(), node.flags(), node.name(), node.type(), e, node.code());
  }
  
  public MacroDefinition expand(MacroDefinition node, T context) {
    Expression e = node.body().accept(this, context);
    return new MacroDefinition(node.pos(), node.name(), node.formalParams(), e);
  }
  
  @Override
  protected Expression visit(Action node, T context) {
    Expression e = node.body().accept(this, context);
    return new Action(node.pos(), e, node.code());
  }

  @Override
  protected Expression visit(N_Alternation node, T context) {
    List<Expression> es = new ArrayList<Expression>(node.body().size());
    for(Expression e : node) {
      es.add(e.accept(this, context));
    }
    return new N_Alternation(node.pos(), es);
  }

  @Override
  protected Expression visit(AndPredicate node, T context) {
    return new AndPredicate(
      node.pos(), node.body().accept(this, context)
    );
  }

  @Override
  protected Expression visit(CharClass node, T context) {
    return new CharClass(node.pos(), node.positive, node.elements, node.var());
  }

  @Override
  protected Expression visit(Cut node, T context) {
    return new Cut(node.pos());
  }

  @Override
  protected Expression visit(Empty node, T context) {
    return new Empty(node.pos());
  }

  @Override
  protected Expression visit(Fail node, T context) {
    return new Fail(node.pos());
  }

  @Override
  protected Expression visit(NonTerminal node, T context) {
    return new NonTerminal(node.pos(), node.name(), node.var());
  }
  
  @Override
  protected Expression visit(MacroVariable node, T context) {
    return new MacroVariable(node.pos(), node.name(), node.var());
  }  

  @Override
  protected Expression visit(MacroCall node, T context) {
    List<Expression> newParams = new ArrayList<Expression>();
    for(Expression param:node.params()) {
      newParams.add(param.accept(this, context));
    }
    return new MacroCall(node.pos(), node.name(), newParams);
  }

  @Override
  protected Expression visit(NotPredicate node, T context) {
    return new NotPredicate(
      node.pos(), node.body().accept(this, context)
    );
  }

  @Override
  protected Expression visit(Optional node, T context) {
    return new Optional(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(Repetition node, T context) {
    return new Repetition(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(RepetitionPlus node, T context) {
    return new RepetitionPlus(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(SemanticPredicate node, T context) {
    return new SemanticPredicate(node.pos(), node.predicate());
  }
  
  @Override
  protected Expression visit(N_Sequence node, T context) {
    List<Expression> es = new ArrayList<Expression>(node.body().size());
    for(Expression e : node) {
      es.add(accept(e, context));
    }
    return new N_Sequence(node.pos(), es);
  }

  @Override
  protected Expression visit(SetValueAction node, T context) {
    return new SetValueAction(
      node.pos(), node.body().accept(this, context), node.code()
    );
  }

  @Override
  protected Expression visit(StringLiteral node, T context) {
    return new StringLiteral(node.pos(), node.value(), node.var());
  }

  @Override
  protected Expression visit(Wildcard node, T context) {
    return new Wildcard(node.pos(), node.var());
  }
}
