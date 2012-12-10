package com.github.kmizu.yapp.tr;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.Rule;
import com.github.kmizu.yapp.Ast.SemanticPredicate;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Terminal;
import com.github.kmizu.yapp.Ast.Visitor;
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.util.CollectionUtil;

public class FirstSetCollector extends Visitor<Void, Set<Expression>>
  implements Translator<Expression, Set<Expression>> {
  public static class FirstSetCannotBeComputed extends RuntimeException {}
  private Map<Symbol, Expression> bindings;
  private Set<Expression> nul;
  private Set<Expression> fail;
  private boolean notPredicateAsError;
  
  public FirstSetCollector(
    Map<Symbol, Expression> bindings, Set<Expression> nul, Set<Expression> fail
  ) {
	this(bindings, nul, fail, false);
  }
  
  public FirstSetCollector(
    Map<Symbol, Expression> bindings, Set<Expression> nul, Set<Expression> fail,
    boolean notPredicateAsError
  ) {
    this.bindings = bindings;
    this.nul = nul;
    this.fail = fail;
    this.notPredicateAsError = notPredicateAsError;
  }
  
  public Set<Expression> translate(Expression from) {
    Set<Expression> first = CollectionUtil.set();
    from.accept(this, first);
    Set<Expression> FIRST = CollectionUtil.set();
    for(Expression e:first) {
      if(e instanceof Empty || !(e instanceof Terminal)) continue;
      if((e instanceof StringLiteral) && ((StringLiteral)e).value().equals("")) continue;
      FIRST.add(e);
    }
    return FIRST;
  }

  @Override
  protected Void visit(Action node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(AndPredicate node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(CharClass node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(Cut node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(Empty node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(Fail node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(N_Alternation node, Set<Expression> context) {
    context.add(node);
    Iterator<Expression> it = node.iterator();
    Expression e1 = it.next();
    e1.accept(this, context);
    while(it.hasNext()) {
      Expression e = it.next();
      if(!fail.contains(e1)) break; 
      e.accept(this, context);
      e1 = e;
    }
    return null;
  }

  @Override
  protected Void visit(N_Sequence node, Set<Expression> context) {
    context.add(node);
    Iterator<Expression> it = node.iterator();
    Expression e1 = it.next();
    e1.accept(this, context);
    while(it.hasNext()) {
      Expression e = it.next();
      if(!nul.contains(e1)) break; 
      e.accept(this, context);
      e1 = e;
    }
    return null;
  }

  @Override
  protected Void visit(NonTerminal node, Set<Expression> context) {
    if(context.contains(node)) return null;
    context.add(node);
    bindings.get(node.name()).accept(this, context);
    return null;
  }

  @Override
  protected Void visit(Optional node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(NotPredicate node, Set<Expression> context) {    
  	if(notPredicateAsError) throw new FirstSetCannotBeComputed();
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(Repetition node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(RepetitionPlus node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(SemanticPredicate node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(SetValueAction node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return null;
  }

  @Override
  protected Void visit(StringLiteral node, Set<Expression> context) {
    context.add(node);
    return null;
  }

  @Override
  protected Void visit(Wildcard node, Set<Expression> context) {
    context.add(node);
    return null;
  }
}
