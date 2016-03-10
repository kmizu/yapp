package jp.gr.java_conf.mizu.yapp.tr;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Pair;
import jp.gr.java_conf.mizu.yapp.Symbol;
import jp.gr.java_conf.mizu.yapp.Ast.Action;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
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
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

public class FollowSetCollector extends Visitor<Void, Pair<Expression, Set<Expression>>> 
  implements Translator<Expression, Set<Expression>>{
  private final Map<Symbol, Expression> bindings;
  private final Map<Expression, Set<Expression>> callerBindings;
  private final Set<Expression> nul;
  private final Set<Expression> fail;
  private Set<NonTerminal> visit;
  
  private static boolean isEmpty(Expression e) {
    return e instanceof Empty ||
            (e instanceof StringLiteral && ((StringLiteral)e).value().equals(""));
  }
  
  public FollowSetCollector(
    Map<Symbol, Expression> bindings, Set<Expression> nul, Set<Expression> fail
  ) {
    this.bindings = bindings;
    this.nul = nul;
    this.fail = fail;
    CallerRelationCreator visitor = new CallerRelationCreator();
    Context context = new Context();
    for(Expression e:bindings.values()) e.accept(visitor, context);
    callerBindings = context.callerBindings;
  }
  
  private static class Context {
    Map<Expression, Set<Expression>> callerBindings = CollectionUtil.map();
    void addCaller(Expression callee, Expression caller) {
      Set<Expression> cs = callerBindings.get(callee);
      if(cs == null) {
        cs = CollectionUtil.set();
        callerBindings.put(callee, cs);
      }
      cs.add(caller);
    }
  }
  
  private class CallerRelationCreator extends Visitor<Void, Context> {

    @Override
    protected Void visit(Action node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(AndPredicate node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(N_Alternation node, Context context) {
      for(Expression e:node) {
        e.accept(this, context);
        context.addCaller(e, node);
      }
      return null;
    }

    @Override
    protected Void visit(N_Sequence node, Context context) {
      for(Expression e:node) {
        e.accept(this, context);
        context.addCaller(e, node);
      }
      return null;
    }

    @Override
    protected Void visit(NonTerminal node, Context context) {
      context.addCaller(bindings.get(node.name()), node);
      return null;
    }

    @Override
    protected Void visit(NotPredicate node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(Optional node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(Repetition node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(RepetitionPlus node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }

    @Override
    protected Void visit(SetValueAction node, Context context) {
      node.body().accept(this, context);
      context.addCaller(node.body(), node);
      return null;
    }    
  }  
  
  public synchronized Set<Expression> translate(Expression from) {
    Set<Expression> set = CollectionUtil.set();
    visit = CollectionUtil.set();
    for(Expression e:callersOf(from)){
      e.accept(this, Pair.make(from, set));
    }
    return set;
  }

  @Override
  protected Void visit(Action node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(AndPredicate node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(CharClass node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(Cut node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(Empty node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(Fail node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(N_Alternation node, Pair<Expression, Set<Expression>> context) {
    List<Expression> body = node.body();
    if(body.get(body.size() - 1) == context.fst) {
      for(Expression e:callersOf(node)){
        e.accept(this, Pair.make((Expression)node, context.snd));
      }
    }
    return null;
  }

  @Override
  protected Void visit(N_Sequence node, Pair<Expression, Set<Expression>> context) {
    List<Expression> body = node.body();
    int found = body.indexOf(context.fst);
    if(found < body.size() - 1) {
      context.snd.add(body.get(found + 1));
    }else {
      for(Expression e:callersOf(node)){
        e.accept(this, Pair.make((Expression)node, context.snd));
      }
    }
    return null;
  }

  @Override
  protected Void visit(NonTerminal node, Pair<Expression, Set<Expression>> context) {
    if(visit.contains(node)) return null;
    visit.add(node);
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(NotPredicate node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(Optional node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(Repetition node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(RepetitionPlus node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(Rule node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(SemanticPredicate node, Pair<Expression, Set<Expression>> context) {
    return null;
  }

  @Override
  protected Void visit(SetValueAction node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(StringLiteral node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }

  @Override
  protected Void visit(Wildcard node, Pair<Expression, Set<Expression>> context) {
    for(Expression e:callersOf(node)){
      e.accept(this, Pair.make((Expression)node, context.snd));
    }
    return null;
  }
  
  private Set<Expression> callersOf(Expression node) {
    Set<Expression> callers = callerBindings.get(node);
    return callers != null ? callers : new HashSet<Expression>();
  }
}
