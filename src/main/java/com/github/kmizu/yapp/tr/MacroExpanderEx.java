package com.github.kmizu.yapp.tr;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.BoundedExpression;

/**
 * A special MacroExpander which doesn't desugar BoundedExpression.
 */
public class MacroExpanderEx extends MacroExpander {
  @Override
  protected Expression visit(BoundedExpression node, MacroEnvironment env) {
    return new BoundedExpression(node.body().accept(this, env));
  }
}
