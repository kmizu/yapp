package com.github.kmizu.yapp.translator;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.Visitor;

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
