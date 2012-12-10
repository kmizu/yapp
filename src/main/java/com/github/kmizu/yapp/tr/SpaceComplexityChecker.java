package com.github.kmizu.yapp.tr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.BoundedExpression;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.N_Sequence;
import com.github.kmizu.yapp.Ast.NonTerminal;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.Rule;
import com.github.kmizu.yapp.Ast.SemanticPredicate;
import com.github.kmizu.yapp.Ast.SetValueAction;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Visitor;
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.util.Sets;

public class SpaceComplexityChecker extends Visitor<Boolean, Set<Symbol>> 
  implements Translator<Grammar, List<UnboundedContext>> {
  private class BoundedExpressionCollector extends Visitor<Boolean, Set<Symbol>> {
    private Set<Expression> boundedExpressions;
    
    public Set<Expression> collect() {
      boundedExpressions = new HashSet<Expression>();
      for(Symbol name:bindings.keySet()){
        if(boundedRules.contains(name)) {
          boundedExpressions.add(bindings.get(name));
        }
      }
      int oldSize;
      do {
        oldSize = boundedExpressions.size();
        for(Symbol name:bindings.keySet()) {
          bindings.get(name).accept(this, null);
        }
      }while(oldSize != boundedExpressions.size());
      return boundedExpressions;
    }

    @Override
    protected Boolean visit(Action node, Set<Symbol> context) {
      if(node.body().accept(this, context)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(AndPredicate node, Set<Symbol> context) {
      if(node.body().accept(this, context)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(CharClass node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(Cut node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(Empty node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(Fail node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(N_Alternation node, Set<Symbol> context) {
      boolean hasUnbounded = false;
      for(Expression e:node) {
        if(!e.accept(this, context)) hasUnbounded = true;
      }
      if(hasUnbounded) {
        return false;
      }else {
        boundedExpressions.add(node);
        return true;
      }
    }

    @Override
    protected Boolean visit(N_Sequence node, Set<Symbol> context) {
      boolean hasUnbounded = false;
      for(Expression e:node) {
        if(!e.accept(this, context)) hasUnbounded = true;
      }
      if(hasUnbounded) {
        return false;
      }else {
        boundedExpressions.add(node);
        return true;
      }
    }
    
    @Override
    protected Boolean visit(BoundedExpression node, Set<Symbol> context) {
      node.body().accept(this, context);
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(NonTerminal node, Set<Symbol> context) {
      if(boundedRules.contains(node.name())) {
        boundedExpressions.add(node);
        return true;
      }
      Expression body = bindings.get(node.name());
      if(boundedExpressions.contains(body)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(NotPredicate node, Set<Symbol> context) {
      if(node.body().accept(this, context)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(Optional node, Set<Symbol> context) {
      if(node.body().accept(this, context)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(Repetition node, Set<Symbol> context) {
      node.body().accept(this, context);
      return false;
    }

    @Override
    protected Boolean visit(RepetitionPlus node, Set<Symbol> context) {
      node.body().accept(this, context);
      return false;
    }

    @Override
    protected Boolean visit(SemanticPredicate node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(SetValueAction node, Set<Symbol> context) {
      if(node.body().accept(this, context)) {
        boundedExpressions.add(node);
        return true;
      }
      return false;
    }

    @Override
    protected Boolean visit(StringLiteral node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }

    @Override
    protected Boolean visit(Wildcard node, Set<Symbol> context) {
      boundedExpressions.add(node);
      return true;
    }
    
  }
  private Map<Symbol, Expression> bindings;
  private Set<Symbol> boundedRules;
  private Set<Expression> boundedExpressions;
  private List<UnboundedContext> unboundedExpressions;
  private Symbol current;
  
  private boolean passed;
  public SpaceComplexityChecker() {
  }
  public List<UnboundedContext> translate(Grammar grammar) {
    passed = true;
    bindings = new HashMap<Symbol, Expression>();
    boundedRules = new HashSet<Symbol>();    
    unboundedExpressions = new ArrayList<UnboundedContext>();
    for(Rule r:grammar) {
      bindings.put(r.name(), r.body());
      if((r.flags() & Rule.BOUNDED) != 0){
        boundedRules.add(r.name());
      }
    }
    boundedExpressions = new BoundedExpressionCollector().collect();
    Rule start = grammar.getRules().get(0);
    current = start.name();
    boolean sbounded = start.body().accept(this, Sets.add(new HashSet<Symbol>(), start.name()));
    if(!sbounded) {
      return unboundedExpressions;
    }else{
      unboundedExpressions = new ArrayList<UnboundedContext>();
      return unboundedExpressions;
    }
  }
  @Override
  protected Boolean visit(Action node, Set<Symbol> context) {
    if(passed) {
      return node.body().accept(this, context);
    }else {
      boolean bounded = boundedExpressions.contains(node);
      if(!bounded) {
        unboundedExpressions.add(new UnboundedContext(current, node.body()));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(AndPredicate node, Set<Symbol> context) {
    boolean bounded = boundedExpressions.contains(node.body());
    if(!bounded) {
      unboundedExpressions.add(new UnboundedContext(current, node.body()));
    }
    return bounded;
  }
  @Override
  protected Boolean visit(CharClass node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(Cut node, Set<Symbol> context) {
    if(passed) throw new RuntimeException("illegal usage of cut operator");
    passed = true;
    return true;
  }
  @Override
  protected Boolean visit(Empty node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(Fail node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(N_Alternation node, Set<Symbol> context) {
    boolean old = passed;
    for(Expression e:node.body().subList(0, node.body().size() - 1)){
      passed = false;
      if(!e.accept(this, context)) return false;
    }
    passed = old;
    boolean sbounded =  node.body().get(node.body().size() - 1).accept(this, context);
    if(passed) {
      return sbounded;
    }else {
      boolean bounded = boundedExpressions.contains(node);
      if(!bounded){
        unboundedExpressions.add(new UnboundedContext(current, node));
      }
      return bounded;      
    }
  }
  @Override
  protected Boolean visit(N_Sequence node, Set<Symbol> context) {
    for(Expression e:node) {
      if(!e.accept(this, context)) return false;
    }
    if(passed){
      return true;
    }else {
      boolean bounded = boundedExpressions.contains(node);
      if(!bounded){
        unboundedExpressions.add(new UnboundedContext(current, node));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(NonTerminal node, Set<Symbol> context) {
    if(boundedRules.contains(node.name())) return true;
    if(passed) {
      boolean old = passed;
      Symbol oldCurrent = current;
      passed = true;
      current = node.name();
      boolean sbounded = context.contains(node.name()) ? true : bindings.get(node.name()).accept(this, Sets.add(context, node.name()));
      passed = old;
      current = oldCurrent;
      return sbounded;
    }else {
      boolean bounded = boundedExpressions.contains(node);
      if(!bounded){
        unboundedExpressions.add(new UnboundedContext(current, node));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(NotPredicate node, Set<Symbol> context) {
    boolean bounded = boundedExpressions.contains(node.body());
    if(!bounded) {
      unboundedExpressions.add(new UnboundedContext(current, node.body()));
    }
    return bounded;
  }
  @Override
  protected Boolean visit(Optional node, Set<Symbol> context) {
    if(passed) {
      boolean old = passed;
      passed = false;
      boolean sbounded = node.body().accept(this, context);
      passed = old;
      return sbounded;
    }else {
      boolean bounded = boundedExpressions.contains(node.body());
      if(!bounded) {
        unboundedExpressions.add(new UnboundedContext(current, node.body()));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(Repetition node, Set<Symbol> context) {
    if(passed) {
      boolean old = passed;
      passed = false;
      boolean sbounded = node.body().accept(this, context);
      passed = old;
      return sbounded;
    }else {
      boolean bounded = boundedExpressions.contains(node.body());
      if(!bounded) {
        unboundedExpressions.add(new UnboundedContext(current, node.body()));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(RepetitionPlus node, Set<Symbol> context) {
    if(passed) {
      boolean old = passed;
      passed = false;
      boolean sbounded = node.body().accept(this, context);
      passed = old;
      return sbounded;
    }else {
      boolean bounded = boundedExpressions.contains(node.body());
      if(!bounded) {
        unboundedExpressions.add(new UnboundedContext(current, node.body()));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(SemanticPredicate node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(SetValueAction node, Set<Symbol> context) {
    if(passed) {
      return node.body().accept(this, context);
    }else {
      boolean bounded = boundedExpressions.contains(node.body());
      if(!bounded) {
        unboundedExpressions.add(new UnboundedContext(current, node.body()));
      }
      return bounded;
    }
  }
  @Override
  protected Boolean visit(StringLiteral node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(Wildcard node, Set<Symbol> context) {
    return true;
  }
  @Override
  protected Boolean visit(BoundedExpression node, Set<Symbol> context) {
    return true;
  }
}
