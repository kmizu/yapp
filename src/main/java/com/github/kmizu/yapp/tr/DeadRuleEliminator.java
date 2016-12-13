package com.github.kmizu.yapp.tr;

import java.util.List;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.DirectedGraph;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.Visitor;
import com.github.kmizu.yapp.util.CollectionUtil;

public class DeadRuleEliminator 
  implements Translator<Ast.Grammar, Ast.Grammar> {
  public Grammar translate(Grammar from) {
    DirectedGraph<Symbol, Boolean> graph = RefGraphMaker.INSTANCE.translate(from);
    Set<Symbol> mark = CollectionUtil.set(from.getRules().get(0).name());
    while(true) {
      Set<Symbol> tmp = CollectionUtil.setFrom(mark);
      for(Symbol s1:mark) {
        for(Symbol s2:graph.neighbors(s1)){
          tmp.add(s2);
        }
      }
      if(mark.size() == tmp.size()) break;
      mark = tmp;
    }
    List<Ast.Rule> rules = CollectionUtil.list();
    for(Ast.Rule r:from) {
      if(mark.contains(r.name())) rules.add(r);
    }
    return new Ast.Grammar(
      from.pos(), from.name(), from.macros(), rules
    );
  }
}
