package com.github.kmizu.yapp.tr;

import java.util.HashMap;
import java.util.Map;

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
