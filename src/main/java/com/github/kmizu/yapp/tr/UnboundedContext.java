package com.github.kmizu.yapp.tr;

import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Expression;

public class UnboundedContext {
  public final Symbol parent;
  public final Expression unboundedExpression;
  public UnboundedContext(Symbol parent, Expression unboundedExpression) {
    super();
    this.parent = parent;
    this.unboundedExpression = unboundedExpression;
  }
}
