package jp.gr.java_conf.mizu.yapp.tr;

import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
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

public class FailExpressionCollector extends Ast.Visitor<Boolean, Set<Ast.Expression>>
  implements Translator<Ast.Grammar, Set<Ast.Expression>> {
  Set<Symbol> failNonTerminals;
  public synchronized Set<Ast.Expression> translate(Grammar from) {
    Set<Expression> fail = CollectionUtil.set();
    failNonTerminals = CollectionUtil.set();
    int oldSize;
    do {
      oldSize = failNonTerminals.size();
      for(Rule r:from){
        if(r.body().accept(this, fail)) failNonTerminals.add(r.name());
      }
    }while(oldSize != failNonTerminals.size());
    return fail;
  }

  @Override
  protected Boolean visit(Action node, Set<Expression> context) {
    boolean bodyFail = node.body().accept(this, context);
    if(bodyFail) context.add(node);
    return bodyFail;
  }

  @Override
  protected Boolean visit(AndPredicate node, Set<Expression> context) {
    boolean bodyFail = node.body().accept(this, context);
    if(bodyFail) context.add(node);
    return bodyFail;
  }

  @Override
  protected Boolean visit(CharClass node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(Cut node, Set<Expression> context) {
    return false;
  }

  @Override
  protected Boolean visit(Empty node, Set<Expression> context) {
    return false;
  }

  @Override
  protected Boolean visit(Fail node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(N_Alternation node, Set<Expression> context) {
    boolean allFail = true;
    for(Expression e:node){
      if(!e.accept(this, context)) allFail = false;
    }
    if(allFail) context.add(node);
    return allFail;
  }

  @Override
  protected Boolean visit(N_Sequence node, Set<Expression> context) {
    boolean anyFail = false;
    for(Expression e:node){
      if(e.accept(this, context)) anyFail = true;
    }
    if(anyFail) context.add(node);
    return anyFail;
  }

  @Override
  protected Boolean visit(NonTerminal node, Set<Expression> context) {
    if(failNonTerminals.contains(node.name())){
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
    node.body().accept(this, context);
    return false;
  }

  @Override
  protected Boolean visit(Repetition node, Set<Expression> context) {
    node.body().accept(this, context);
    return false;
  }

  @Override
  protected Boolean visit(RepetitionPlus node, Set<Expression> context) {
    boolean bodyFail = node.body().accept(this, context);
    if(bodyFail) context.add(node);
    return bodyFail;
  }

  @Override
  protected Boolean visit(SemanticPredicate node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(SetValueAction node, Set<Expression> context) {
    boolean bodyFail = node.body().accept(this, context);
    if(bodyFail) context.add(node);
    return bodyFail;
  }

  @Override
  protected Boolean visit(StringLiteral node, Set<Expression> context) {
    if(node.value().length() != 0) {
      context.add(node);
      return true;
    }
    return false;
  }

  @Override
  protected Boolean visit(Wildcard node, Set<Expression> context) {
    context.add(node);
    return true;
  }
}
