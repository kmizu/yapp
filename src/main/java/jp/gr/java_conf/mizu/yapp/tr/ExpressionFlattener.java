package jp.gr.java_conf.mizu.yapp.tr;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;

import static jp.gr.java_conf.mizu.yapp.util.CollectionUtil.*;

public class ExpressionFlattener implements Translator<Grammar, Grammar> {
  private Translator<Grammar, Grammar> f1 = new AbstractGrammarExpander<Void>(){
    @Override
    protected Expression visit(N_Sequence node, Void context) {
      List<Expression> es = node.body();
      List<Expression> result = new ArrayList<Expression>();
      Expression last = es.get(0);
      for(Expression e : es.subList(1, es.size())){
        if(e instanceof N_Alternation){
          N_Alternation a = (N_Alternation)e;
          List<Expression> newE2 = new ArrayList<Expression>();
          int i = 0;
          for(Expression e2 : a){
            newE2.add(new N_Sequence(
              last.pos(), list(last, e2.accept(this, context))
            ));
          }
          last = new N_Alternation(last.pos(), newE2);
        }else {
          result.add(last);
          last = e.accept(this, context);
        }
      }
      result.add(last);
      return new N_Sequence(node.pos(), result);
    }
  };
  private Translator<Grammar, Grammar> f2 = new AbstractGrammarExpander<Void>(){
    @Override
    protected Expression visit(N_Sequence node, Void context) {
      List<Expression> result = new ArrayList<Expression>();
      for(Expression e : node){
        Expression newE = e.accept(this, context);
        if(newE instanceof N_Sequence){
          result.addAll(((N_Sequence)newE).body());
        }else{
          result.add(newE);
        }
      }
      return new N_Sequence(node.pos(), result);
    }
    
    @Override
    protected Expression visit(N_Alternation node, Void context) {
      List<Expression> result = new ArrayList<Expression>();
      for(Expression e : node){
        Expression newE = e.accept(this, context);
        if(newE instanceof N_Alternation){
          result.addAll(((N_Alternation)newE).body());
        }else{
          result.add(newE);
        }
      }
      return new N_Alternation(node.pos(), result);
    }
  };
  public Grammar translate(Grammar from) {
    return f2.translate(f1.translate(from));
  }
}
