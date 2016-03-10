package jp.gr.java_conf.mizu.yapp.tr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Pair;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.BoundedExpression;
import jp.gr.java_conf.mizu.yapp.Ast.CharClass;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Empty;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Fail;
import jp.gr.java_conf.mizu.yapp.Ast.Grammar;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;
import jp.gr.java_conf.mizu.yapp.Ast.NonTerminal;
import jp.gr.java_conf.mizu.yapp.Ast.NotPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Optional;
import jp.gr.java_conf.mizu.yapp.Ast.Repetition;
import jp.gr.java_conf.mizu.yapp.Ast.RepetitionPlus;
import jp.gr.java_conf.mizu.yapp.Ast.Rule;
import jp.gr.java_conf.mizu.yapp.Ast.SemanticPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.SetValueAction;
import jp.gr.java_conf.mizu.yapp.Ast.StringLiteral;
import jp.gr.java_conf.mizu.yapp.Ast.Visitor;
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.util.Sets;

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
