package com.github.kmizu.yapp.tr;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.Rule;
import com.github.kmizu.yapp.Ast.Visitor;

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
