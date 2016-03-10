package jp.gr.java_conf.mizu.yapp.tr;

import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.*;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

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
