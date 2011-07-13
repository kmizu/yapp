package jp.gr.java_conf.mizu.yapp.tr;

import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Pair;
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
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

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
    if(context.snd.contains(node.name())) return false;
    else {
      context.snd.add(node.name());
      Expression body = bindings.get(node.name());
      return body.accept(this, context);
    }
  }
  @Override
  protected Boolean visit(NotPredicate node, Pair<Boolean, Set<Symbol>> context) {
    if(context.fst) {
      return false;
    }else {
      return node.body().accept(this, Pair.make(true, context.snd));
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
    if(context.fst) return true;
    else return false;
  }
}
