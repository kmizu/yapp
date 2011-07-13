package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Ast.BoundedExpression;
import jp.gr.java_conf.mizu.yapp.Ast.DepthFirstVisitor;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;

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
