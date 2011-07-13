package jp.gr.java_conf.mizu.yapp.tr;

import java.util.Iterator;
import java.util.Map;
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
import jp.gr.java_conf.mizu.yapp.Ast.Terminal;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

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
