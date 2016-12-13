package com.github.kmizu.yapp.tr;
import java.util.Map;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Grammar;

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
