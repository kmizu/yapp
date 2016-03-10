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

public class FiniteLengthExpressionCollector extends Ast.Visitor<Boolean, Set<Ast.Expression>>
  implements Translator<Ast.Grammar, Set<Ast.Expression>> {
  Set<Symbol> finiteNonTerminals;
  public synchronized Set<Ast.Expression> translate(Grammar from) {
    Set<Expression> finiteExpressions = CollectionUtil.set();
    finiteNonTerminals = CollectionUtil.set();
    int oldSize;
    do {
      oldSize = finiteNonTerminals.size();
      for(Rule r:from){
        if(r.body().accept(this, finiteExpressions)) finiteNonTerminals.add(r.name());
      }
    }while(oldSize != finiteNonTerminals.size());
    return finiteExpressions;
  }

  @Override
  protected Boolean visit(Action node, Set<Expression> context) {
    boolean finite = node.body().accept(this, context);
    if(finite) context.add(node);
    return finite;
  }

  @Override
  protected Boolean visit(AndPredicate node, Set<Expression> context) {
    boolean finite =  node.body().accept(this, context);
    if(finite) context.add(node);
    return finite;
  }

  @Override
  protected Boolean visit(CharClass node, Set<Expression> context) {
    context.add(node);
    return true;
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
    boolean allFinite = true;
    for(Expression e:node){
      if(!e.accept(this, context)) allFinite = false;
    }
    if(allFinite) context.add(node);
    return allFinite;
  }

  @Override
  protected Boolean visit(N_Sequence node, Set<Expression> context) {
    boolean allFinite = true;
    for(Expression e:node){
      if(!e.accept(this, context)) allFinite = false;
    }
    if(allFinite) context.add(node);
    return allFinite;
  }

  @Override
  protected Boolean visit(NonTerminal node, Set<Expression> context) {
    if(finiteNonTerminals.contains(node.name())){
      context.add(node);
      return true;
    }
    return false;
  }

  @Override
  protected Boolean visit(NotPredicate node, Set<Expression> context) {
    boolean finite = node.body().accept(this, context);
    if(finite) context.add(node);
    return finite;
  }

  @Override
  protected Boolean visit(Optional node, Set<Expression> context) {    
    boolean finite = node.body().accept(this, context);
    if(finite) context.add(node);
    return finite;
  }

  @Override
  protected Boolean visit(Repetition node, Set<Expression> context) {
    node.body().accept(this, context);
    return false;
  }

  @Override
  protected Boolean visit(RepetitionPlus node, Set<Expression> context) {
    node.body().accept(this, context);
    return false;
  }

  @Override
  protected Boolean visit(SemanticPredicate node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(SetValueAction node, Set<Expression> context) {
    boolean finite = node.body().accept(this, context);
    if(finite) context.add(node);
    return finite;
  }

  @Override
  protected Boolean visit(StringLiteral node, Set<Expression> context) {
    context.add(node);
    return true;
  }

  @Override
  protected Boolean visit(Wildcard node, Set<Expression> context) {
    return true;
  }
}
