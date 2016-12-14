package com.github.kmizu.yapp.translator;

import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.SemanticPredicate;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Visitor;
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.util.CollectionUtil;

public class EofDetector extends Visitor<Boolean, Pair<Boolean, Set<Symbol>>>
  implements Translator<Expression, Boolean> {
  private final Map<Symbol, Expression> bindings;
  public EofDetector(Map<Symbol, Expression> bindings) {
    this.bindings = bindings;
  }
  public Boolean translate(Expression from) {
    Set<Symbol> visit = CollectionUtil.set();
    return from.accept(this, Pair.make(false, visit));
  }
  @Override
  protected Boolean visit(Action node, Pair<Boolean, Set<Symbol>> context) {
    return node.body().accept(this, context);
  }
  @Override
  protected Boolean visit(AndPredicate node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(CharClass node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(Cut node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(Empty node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(Fail node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(N_Alternation node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(N_Sequence node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(NonTerminal node, Pair<Boolean, Set<Symbol>> context) {
    if(context.snd().contains(node.name())) return false;
    else {
      context.snd().add(node.name());
      Expression body = bindings.get(node.name());
      return body.accept(this, context);
    }
  }
  @Override
  protected Boolean visit(NotPredicate node, Pair<Boolean, Set<Symbol>> context) {
    if(context.fst()) {
      return false;
    }else {
      return node.body().accept(this, Pair.make(true, context.snd()));
    }
  }
  @Override
  protected Boolean visit(Optional node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(Repetition node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(RepetitionPlus node,
      Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(SemanticPredicate node,
      Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(SetValueAction node,
      Pair<Boolean, Set<Symbol>> context) {
    return node.body().accept(this, context);
  }
  @Override
  protected Boolean visit(StringLiteral node, Pair<Boolean, Set<Symbol>> context) {
    return false;
  }
  @Override
  protected Boolean visit(Wildcard node, Pair<Boolean, Set<Symbol>> context) {
    if(context.fst()) return true;
    else return false;
  }
}
