package com.github.kmizu.yapp.tr;

import com.github.kmizu.yapp.Ast.BoundedExpression;
import com.github.kmizu.yapp.Ast.DepthFirstVisitor;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.Rule;

public class BoundedQualifierCounter extends DepthFirstVisitor<Object> implements Translator<Grammar, Grammar> {
  private int nQualifiedRules;
  private int nQualifiedExpressions;
  public Grammar translate(Grammar from) {
    nQualifiedRules = 0;
    nQualifiedExpressions = 0;
    for(Rule r:from) {
      r.accept(this, null);
    }
    System.err.println("bounded rules = " + nQualifiedRules);
    System.err.println("bounded expressions = " + nQualifiedExpressions);
    return from;
  }
  @Override
  protected Void visit(BoundedExpression node, Object context) {
    nQualifiedExpressions++;
    return null;
  }
  @Override
  protected Void visit(Rule node, Object context) {
    super.visit(node, context);
    if((node.flags() & Rule.BOUNDED) != 0) {
      nQualifiedRules++;
    }
    return null;
  }
}
