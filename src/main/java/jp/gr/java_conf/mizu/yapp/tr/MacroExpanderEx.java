package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.BoundedExpression;

/**
 * A special MacroExpander which doesn't desugar BoundedExpression.
 */
public class MacroExpanderEx extends MacroExpander {
  @Override
  protected Expression visit(BoundedExpression node, MacroEnvironment env) {
    return new BoundedExpression(node.body().accept(this, env));
  }
}
