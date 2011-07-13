package jp.gr.java_conf.mizu.yapp.tr;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.SemanticException;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.CharClass;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Empty;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Fail;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;
import jp.gr.java_conf.mizu.yapp.Ast.NonTerminal;
import jp.gr.java_conf.mizu.yapp.Ast.NotPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Optional;
import jp.gr.java_conf.mizu.yapp.Ast.Repetition;
import jp.gr.java_conf.mizu.yapp.Ast.RepetitionPlus;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;
import jp.gr.java_conf.mizu.yapp.Ast.SemanticPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.SetValueAction;
import jp.gr.java_conf.mizu.yapp.Ast.StringLiteral;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

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
