package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;

public class UnboundedContext {
  public final Symbol parent;
  public final Expression unboundedExpression;
  public UnboundedContext(Symbol parent, Expression unboundedExpression) {
    super();
    this.parent = parent;
    this.unboundedExpression = unboundedExpression;
  }
}
