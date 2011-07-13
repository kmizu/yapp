package jp.gr.java_conf.mizu.yapp.tr;

import java.util.HashMap;
import java.util.Map;

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

public class SucceedRuleDetector 
  extends Ast.Visitor<Boolean, SucceedRuleDetector.Context>
  implements Translator<Ast.Grammar, Map<Symbol, Boolean>> {
  public static final SucceedRuleDetector INSTANCE = new SucceedRuleDetector();
  static class Context {
  }

  @Override
  protected Boolean visit(Action node, Context context) {
    return accept(node.body(), context);
  }

  @Override
  protected Boolean visit(AndPredicate node, Context context) {
    return accept(node.body(), context);
  }

  @Override
  protected Boolean visit(CharClass node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(Cut node, Context context) {
    return true;
  }

  @Override
  protected Boolean visit(Empty node, Context context) {
    return true;
  }

  @Override
  protected Boolean visit(Fail node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(N_Alternation node, Context context) {
    for(Expression e:node) {
      if(accept(e, context)) return true;
    }
    return false;
  }

  @Override
  protected Boolean visit(N_Sequence node, Context context) {
    for(Expression e:node) {
      if(!accept(e, context)) return false;
    }
    return true;
  }

  @Override
  protected Boolean visit(NonTerminal node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(NotPredicate node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(Optional node, Context context) {
    return true;
  }

  @Override
  protected Boolean visit(Repetition node, Context context) {
    return true;
  }

  @Override
  protected Boolean visit(RepetitionPlus node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(SemanticPredicate node, Context context) {
    return false;
  }

  @Override
  protected Boolean visit(SetValueAction node, Context context) {
    return accept(node.body(), context);
  }

  @Override
  protected Boolean visit(StringLiteral node, Context context) {
    return node.value().length() == 0;
  }

  @Override
  protected Boolean visit(Wildcard node, Context context) {
    return false;
  }

  public Map<Symbol, Boolean> translate(Grammar from) {
    Map<Symbol, Boolean> mapping = CollectionUtil.map();
    Context context = new Context();
    for(Rule r:from) {
      mapping.put(r.name(), accept(r.body(), context));
    }
    return mapping;
  }  
}
