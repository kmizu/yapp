package com.github.kmizu.yapp.translator;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.Rule;

public class RuleInliner extends AbstractGrammarExpander<RuleInliner.Context> {
  static class Context {
    LinkedHashMap<Symbol, Rule> env;
    int     depth;
    boolean changed;
  }
  
  private final int inlineLimit;
  
  public RuleInliner(int inlineLimit) {
    this.inlineLimit = inlineLimit;
  }  
  
  @Override
  public Context newContext() {
    return new Context();
  }
  
  @Override
  public Grammar expand(Grammar node, Context context) {
    context.env = new LinkedHashMap<Symbol, Rule>();
    for(Rule rule : node) {
      context.env.put(rule.name(), rule);
    }
    for(Rule rule : node) {
      context.env.put(rule.name(), expand(rule, context));
    }
    return new Grammar(node.pos(), node.name(), node.macros(), new ArrayList<Rule>(context.env.values()));
  }
  
  @Override
  public Rule expand(Rule node, Context context) {
    Expression e = node.body();
    context.depth = 0;
    context.changed = true;
    while(context.changed && context.depth < inlineLimit){
      context.changed = false;
      e = e.accept(this, context);
    }
    return new Rule(node.pos(), node.flags(), node.name(), node.type(), e, node.code());
  }
  
  @Override
  protected Expression visit(NonTerminal node, Context context) {
    context.changed = true;
    context.depth++;
    return context.env.get(node.name()).body();
  }
}
