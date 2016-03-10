package jp.gr.java_conf.mizu.yapp.tr;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.DirectedGraph;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.NonTerminal;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;

public class RefGraphMaker 
  extends Ast.DepthFirstVisitor<RefGraphMaker.Context>
  implements Translator<Ast.Grammar, DirectedGraph<Symbol, Boolean>> {
  public static final RefGraphMaker INSTANCE = new RefGraphMaker();
  static class Context {
    Symbol current;
    DirectedGraph<Symbol, Boolean> graph;
  }
  
  public DirectedGraph<Symbol, Boolean> translate(Grammar from) {
    Context context = new Context();
    context.graph = new DirectedGraph<Symbol, Boolean>(false);
    from.accept(this, context);
    for(Symbol s:context.graph){
      if(context.graph.hasCyclicity(s)) {
        context.graph.setInfo(s, true);
      }
    }
    return context.graph;
  }

  @Override
  protected Void visit(Grammar node, Context context) {
    for(Rule r:node){
      context.graph.add(r.name());
    }
    for(Rule r:node){
      r.accept(this, context);
    }
    return null;
  }
  
  @Override
  protected Void visit(Rule node, Context context) {
    context.current = node.name();
    return node.body().accept(this, context);
  }

  @Override
  protected Void visit(NonTerminal node, Context context) {
    context.graph.addEdge(context.current, node.name());
    return null;
  }  
}
