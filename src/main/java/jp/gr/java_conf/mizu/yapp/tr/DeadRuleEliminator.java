package jp.gr.java_conf.mizu.yapp.tr;

import java.util.List;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.DirectedGraph;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

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
