package com.github.kmizu.yapp.tr;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.SemanticException;
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

public class SemanticChecker 
  extends Ast.DepthFirstVisitor<SemanticChecker.Context>
  implements Translator<Grammar, Grammar> {
  private enum Scope { IN_PREDICATE, IN_REPETITION_PLUS, IN_ALTERNATION }
  static class Context {
    final Set<Symbol> rules = CollectionUtil.set();
    final Stack<Scope> scopes = new Stack<Scope>();
  }

  public Grammar translate(Grammar from) {
    Context context = new Context();
    for(Rule r:from) {
      context.rules.add(r.name());
    }
    accept(from, context);
    return null;
  }

  @Override
  protected Void visit(Cut node, Context context) {
    if(!context.scopes.isEmpty()) {
      switch(context.scopes.peek()) {
      case IN_PREDICATE:
        throw new SemanticException(
          node.pos(), "cut cannot be used in predicate(& or !)"
        );
      case IN_REPETITION_PLUS:
        throw new SemanticException(
          node.pos(), "cut cannot be used in +(...)"
        );
      case IN_ALTERNATION:
        //nothing to do since it's enable to use cut
        break;
      default:
        throw new SemanticException(
          node.pos(), "cut cannot be used outside of /"
        );
      }
    }
    return null;
  }

  @Override
  protected Void visit(N_Alternation node, Context context) {
    List<Expression> body = node.body();
    try {
      context.scopes.push(Scope.IN_ALTERNATION);
      for(Expression e:body.subList(0, body.size() - 1)){
        accept(e, context);
      }
    }finally {
      context.scopes.pop();
    }
    accept(body.get(body.size() - 1), context);
    return null;
  }

  @Override
  protected Void visit(NonTerminal node, Context context) {
    if(context.rules.contains(node.name())) {
      throw new SemanticException(node.pos(), "undefined rule: " + node.name());
    }
    return null;
  }

  @Override
  protected Void visit(NotPredicate node, Context context) {
    try {
      context.scopes.push(Scope.IN_PREDICATE);
      super.visit(node, context);
    } finally {
      context.scopes.pop();
    }
    return null;
  }
  
  @Override
  protected Void visit(AndPredicate node, Context context) {
    try {
      context.scopes.push(Scope.IN_PREDICATE);
      super.visit(node, context);
    } finally {
      context.scopes.pop();
    }
    return null; 
  }
  
  @Override
  protected Void visit(Optional node, Context context) {
    try {
      context.scopes.push(Scope.IN_ALTERNATION);
      super.visit(node, context);
    } finally {
      context.scopes.pop();
    }
    return null;
  }
  
  @Override
  protected Void visit(Repetition node, Context context) {
    try {
      context.scopes.push(Scope.IN_ALTERNATION);
      super.visit(node, context);
    } finally {
      context.scopes.pop();
    }
    return null;
  }

  @Override
  protected Void visit(RepetitionPlus node, Context context) {
    try {
      context.scopes.push(Scope.IN_REPETITION_PLUS);
      super.visit(node, context);
    } finally {
      context.scopes.pop();
    }
    return null;
  }
}
