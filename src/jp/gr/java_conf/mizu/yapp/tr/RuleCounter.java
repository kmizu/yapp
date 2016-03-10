package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;

public class RuleCounter extends IdentityTranslator<Ast.Grammar> {
  @Override
  public Grammar translate(Grammar from) {
    int count = 0;
    for(Rule r:from) {
      count++;
    }
    System.err.println("count of rules = " + count);
    return from;
  }
}
