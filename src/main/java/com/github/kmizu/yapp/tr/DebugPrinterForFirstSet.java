package com.github.kmizu.yapp.tr;

import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.*;
import com.github.kmizu.yapp.util.CollectionUtil;

public class DebugPrinterForFirstSet implements Translator<Ast.Grammar, Void> {
  public Void translate(Grammar from) {
    Set<Expression> nul = new NulExpressionCollector().translate(from);
    Set<Expression> fail = new FailExpressionCollector().translate(from);
    Map<Symbol, Expression> bindings = CollectionUtil.map();
    for(Rule r:from) {
      bindings.put(r.name(), r.body());
    }
    FirstSetCollector firstc = new FirstSetCollector(bindings, nul, fail);
    for(Rule r:from) {
      System.out.printf("FIRST(%s) = %s%n", r.name(), firstc.translate(r.body()));
    }
    return null;
  }
}
