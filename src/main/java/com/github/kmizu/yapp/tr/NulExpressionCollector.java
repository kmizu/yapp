package com.github.kmizu.yapp.tr;

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
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.util.CollectionUtil;

public class NulExpressionCollector extends Ast.Visitor<Boolean, Set<Ast.Expression>>
  implements Translator<Ast.Grammar, Set<Ast.Expression>> {
  Set<Symbol> nulNonTerminals;
  public synchronized Set<Ast.Expression> translate(Grammar from) {
    Set<Expression> nul = CollectionUtil.set();
    nulNonTerminals = CollectionUtil.set();
    int oldSize;
    do {
      oldSize = nulNonTerminals.size();
      for(Rule r:from){
        if(r.body().accept(this, nul)) nulNonTerminals.add(r.name());
      }
    }while(oldSize != nulNonTerminals.size());
    return nul;
  }

  @Override
  protected Boolean visit(Action node, Set<Expression> context) {
    boolean bodyNil = node.body().accept(this, context);
    if(bodyNil) context.add(node);
    return null;
  }

  @Override
  protected Boolean visit(AndPredicate node, Set<Expression> context) {
    context.add(node);
    return node.body().accept(this, context);
  }

  @Override
  protected Boolean visit(CharClass node, Set<Expression> context) {
    return false;
  }

  @Override
  protected Boolean visit(Cut node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(Empty node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(Fail node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(N_Alternation node, Set<Expression> context) {
    boolean anyNul = false;
    for(Expression e:node){
      if(e.accept(this, context)) anyNul = true;
    }
    if(anyNul) context.add(node);
    return anyNul;
  }

  @Override
  protected Boolean visit(N_Sequence node, Set<Expression> context) {
    boolean allNul = true;
    for(Expression e:node){
      if(!e.accept(this, context)) allNul = false;
    }
    if(allNul) context.add(node);
    return allNul;
  }

  @Override
  protected Boolean visit(NonTerminal node, Set<Expression> context) {
    if(nulNonTerminals.contains(node.name())){
      context.add(node);
      return true;
    }
    return false;
  }

  @Override
  protected Boolean visit(NotPredicate node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return true;
  }

  @Override
  protected Boolean visit(Optional node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return true;
  }

  @Override
  protected Boolean visit(Repetition node, Set<Expression> context) {
    context.add(node);
    node.body().accept(this, context);
    return true;
  }

  @Override
  protected Boolean visit(RepetitionPlus node, Set<Expression> context) {
    boolean bodyNil = node.body().accept(this, context);
    if(bodyNil) context.add(node);
    return bodyNil;
  }

  @Override
  protected Boolean visit(SemanticPredicate node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(SetValueAction node, Set<Expression> context) {
    boolean bodyNil = node.body().accept(this, context);
    if(bodyNil) context.add(node);
    return bodyNil;
  }

  @Override
  protected Boolean visit(StringLiteral node, Set<Expression> context) {
    if(node.value().length() == 0) {
      context.add(node);
      return true;
    }
    return false;
  }

  @Override
  protected Boolean visit(Wildcard node, Set<Expression> context) {
    return false;
  }
}
