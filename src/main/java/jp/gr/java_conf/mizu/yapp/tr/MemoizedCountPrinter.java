package jp.gr.java_conf.mizu.yapp.tr;
import java.util.Map;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Pair;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;

public class MemoizedCountPrinter implements
  Translator<Pair<Ast.Grammar, Map<Symbol, Boolean>>, Void> {
  public Void translate(Pair<Ast.Grammar, Map<Symbol, Boolean>> from) {
    Ast.Grammar g = from.fst;
    Map<Symbol, Boolean> map = from.snd;
    int countMemoized = 0;
    for(Boolean v : map.values()) if(v) countMemoized++;
    System.out.printf("%d --> %d%n", g.getRules().size(), countMemoized);
    return null;
  }
}
