/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.Rule;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Visitor;

public class NeedlessMemoDetector extends Ast.Visitor<Void, NeedlessMemoDetector.Context> 
  implements Translator<Grammar, Pair<Grammar, Map<Symbol, Boolean>>> {
  static class Context {
    Stack<Boolean> cutUsable;
    Stack<Boolean> backtrack;
    Map<Symbol, Boolean> ruleToContext;
    Map<Symbol, List<Symbol>> referenceGraph;
    Symbol currentRule;
    
    Context() {
      cutUsable = new Stack<Boolean>();
      backtrack = new Stack<Boolean>();
      ruleToContext = new HashMap<Symbol, Boolean>();
      referenceGraph = new HashMap<Symbol, List<Symbol>>();
    }
        
    void setCurrentRule(Symbol currentRule) {
      this.currentRule = currentRule;
    }
    
    Symbol getCurrentRule() {
      return currentRule;
    }
    
    void addRule(Symbol rule) {
      ruleToContext.put(rule, false);
      referenceGraph.put(rule, new ArrayList<Symbol>());
    }
    
    void setRuleContext(Symbol rule, boolean context) {
      ruleToContext.put(rule, context);
    }
    
    boolean getRuleContext(Symbol rule) {
      return ruleToContext.get(rule);
    }
    
    public void addReferer(Symbol referenced, Symbol referer) {
      List<Symbol> referers = referenceGraph.get(referenced);
      referers.add(referer);
    }
    
    public List<Symbol> getReferers(Symbol referenced) {
      return referenceGraph.get(referenced);
    }
    
    public Map<Symbol, Boolean> getContexts() {
      return ruleToContext;
    }
  }
  
  public NeedlessMemoDetector() {
  }
  
  public Pair<Grammar, Map<Symbol, Boolean>> translate(Grammar from) {
    return Pair.make(from, detect(from));
  }

  public Map<Symbol, Boolean> detect(Grammar grammar) {
    Context context = new Context();
    grammar.accept(this, context);
    return context.getContexts();
  }

  @Override
  public Void visit(Action node, Context context) {
    node.body().accept(this, context);
    return null;
  }
  
  @Override
  public Void visit(SetValueAction node, Context context) {
    node.body().accept(this, context);
    return null;
  }

  @Override
  public Void visit(N_Alternation node, Context context) {

    List<Expression> expressions = node.body();

    for(int i = 0; i < expressions.size() - 1; i++){
      context.backtrack.push(true);
      context.cutUsable.push(true);
      int size = context.backtrack.size();
      {
        expressions.get(i).accept(this, context);
      }
      //�J�b�g���Z�q�ɂ���ăX�^�b�N���ω�����\�������邽��
      if(context.backtrack.size() == size){
        context.backtrack.pop();
        context.cutUsable.pop();
      }
    }

    expressions.get(expressions.size() - 1).accept(this, context);

    return null;
  }

  @Override
  public Void visit(Cut node, Context context) {
    if(context.cutUsable.peek()){
      context.backtrack.pop();
      context.cutUsable.pop();
    }
    return null;
  }

  @Override
  public Void visit(Fail node, Context context) {
    return null;
  }

  @Override
  public Void visit(AndPredicate node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(false);
    node.body().accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    return null;
  }

  @Override
  public Void visit(NotPredicate node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(false);
    node.body().accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    return null;
  }

  @Override
  public Void visit(Empty node, Context context) {
    return null;
  }

  @Override
  public Void visit(Grammar node, Context context) {
    for(Rule rule : node){
      context.addRule(rule.name());
    }
    for(Rule rule : node){
      rule.accept(this, context);
    }
    boolean changed = true;
    while(changed) {
      changed = false;
      OUTER:
      for(Rule rule : node) {
        boolean rc = context.getRuleContext(rule.name());
        if(rc) continue;
        for(Symbol referer : context.getReferers(rule.name())){
          if(context.getRuleContext(referer)){
            context.setRuleContext(rule.name(), true);
            changed = true;
            continue OUTER;
          }
        }
      }
    }
    return null;
  }

  @Override
  public Void visit(NonTerminal node, Context context) {
    Symbol name = node.name();
    List<Symbol> referers = context.getReferers(name);
    if(referers.indexOf(context.getCurrentRule()) == -1) {
      referers.add(context.getCurrentRule());
    }
    if(context.backtrack.peek()){
      context.setRuleContext(name, true);
    }
    return null;
  }

  @Override
  public Void visit(Repetition node, Context context) {
    context.backtrack.push(true);
    context.cutUsable.push(true);
    int size = context.backtrack.size();
    {
      node.body().accept(this, context);
    }
    //�J�b�g���Z�q�ɂ���ăX�^�b�N���ω�����\�������邽��
    if(context.backtrack.size() == size){
      context.backtrack.pop();
      context.cutUsable.pop();
    }

    return null;
  }

  @Override
  public Void visit(RepetitionPlus node, Context context) {
    throw new UnsupportedOperationException("visit(RepetitionPlus node, Context context)");
  }

  @Override
  public Void visit(Optional node, Context context) {
    throw new UnsupportedOperationException("visit(Optional node, Context context)");
  }

  @Override
  public Void visit(Rule node, Context context) {
    context.setCurrentRule(node.name());
    context.backtrack.push(false);
    context.cutUsable.push(false);
    node.body().accept(this, context);
    context.backtrack.pop();
    context.cutUsable.pop();
    return null;
  }

  @Override
  public Void visit(N_Sequence node, Context context) {
    List<Expression> expressions = node.body();
    for(Expression expression : expressions.subList(0, expressions.size() - 1)){
      expression.accept(this,  context);
    }
    expressions.get(expressions.size() - 1).accept(this, context);
    return null;
  }

  @Override
  public Void visit(StringLiteral node, Context context) {
    return null;
  }

  @Override
  public Void visit(CharClass node, Context context) {
    return null;
  }

  private static <T> List<T> list(T... args) {
    return new ArrayList<T>(Arrays.asList(args));
  }
}

