package com.github.kmizu.yapp.translator;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.Rule;

public class RuleCounter extends IdentityTranslator<Ast.Grammar> {
  @Override
  public Grammar translate(Grammar from) {
    int count = 0;
    for(Rule r:from) {
      count++;
    }
    System.out.println("count of rules = " + count);
    return from;
  }
}
