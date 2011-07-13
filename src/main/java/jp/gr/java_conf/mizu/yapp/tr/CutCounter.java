package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;

public class CutCounter extends IdentityTranslator<Ast.Grammar> {
  @Override
  public Grammar translate(Grammar from) {
    final int[] count = {0};
    Visitor<Void, Void> counter = new Ast.DepthFirstVisitor<Void>() {
      @Override
      protected Void visit(Cut node, Void context) {
        count[0]++;
        return null;
      }
    };
    from.accept(counter, null);
    System.err.println("count of cut = " + count[0]);
    return from;
  }
}
