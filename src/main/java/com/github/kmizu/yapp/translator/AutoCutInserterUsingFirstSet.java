package com.github.kmizu.yapp.translator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Action;
import com.github.kmizu.yapp.Ast.AndPredicate;
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
import com.github.kmizu.yapp.util.CollectionUtil;

import static com.github.kmizu.yapp.util.CollectionUtil.*;

public class AutoCutInserterUsingFirstSet extends Visitor<Expression, AutoCutInserterUsingFirstSet.Context>
  implements Translator<Ast.Grammar, Ast.Grammar> {  
  static final class Context {
    Map<Symbol, Expression> bindings;
    Set<Expression> nul;
    Set<Expression> fail;
    Set<Expression> finite;
    FirstSetCollector firstSet;
        int countCutInserted;
    public Context(Map<Symbol, Expression> bindings, Set<Expression> nul, Set<Expression> fail, Set<Expression> finite) {
      this.bindings = bindings;
      this.fail = fail;
      this.nul = nul;
      this.finite = finite;
      this.firstSet = new FirstSetCollector(bindings, nul, fail);
    }    
  }
  
  private final int limit;
  
  public AutoCutInserterUsingFirstSet(int limit) {
    this.limit = limit;
  }

  public Grammar translate(Grammar from) {
    List<Rule> rules = list();
    Set<Expression> nul = new NulExpressionCollector().translate(from);
    Set<Expression> fail = new FailExpressionCollector().translate(from);
    Set<Expression> finite = new FiniteLengthExpressionCollector().translate(from);
    Map<Symbol, Expression> mapping = CollectionUtil.map();
    for(Rule r:from) {
      mapping.put(r.name(), r.body());
    }
    Context context = new Context(mapping, nul, fail, finite);
    for(Rule r:from) {
      rules.add(
        new Rule(
          r.pos(),
          r.flags(),
          r.name(),
          r.type(),
          accept(r.body(), context),
          r.code()
        )
      );
    }
    return new Grammar(from.pos(), from.name(), from.macros(), rules);
  }

  @Override
  protected Expression visit(Action node, Context context) {
    return new Action(node.pos(), accept(node.body(), context), node.code());
  }

  @Override
  protected Expression visit(AndPredicate node, Context context) {
    return new AndPredicate(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(CharClass node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(Cut node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(Empty node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(Fail node, Context context) {
    return node;
  }
  
  @Override
  protected Expression visit(N_Alternation node, Context context) {
    List<Expression> body = node.body();
    List<Expression> result = list();
    //Because a FirstSetCollector computes FIRST set using AST node's identity,
    //FIRST set of `e` must not be computed by the result of `e`.accept(...).
    OUTER:
    for(int i = 0; i < body.size() - 1; i++) {
      Expression e1 = body.get(i);
      Set<Expression> f_e1 = context.firstSet.translate(e1);
      Set<Expression> f = CollectionUtil.set();
      for(int j = i + 1; j < body.size(); j++) {
        Expression e2 = body.get(j);
        Set<Expression> f_e2 = context.firstSet.translate(e2);
        if((!ExpressionUtil.disjoint(f_e1, f_e2)) || context.nul.contains(e1) || context.nul.contains(e2)) {
          result.add(accept(e1, context)); continue OUTER;
        }
        f.addAll(f_e2);
      }
      if(context.finite.contains(e1) || (limit >= 0 && f.size() > limit)) {
        result.add(accept(e1, context));
      }else{
        result.add(
          new Ast.N_Sequence(
            node.pos(),
            list(
              new Ast.NotPredicate(node.pos(), ExpressionUtil.newAlternation(node.pos(), ExpressionUtil.compact(f))),
              new Ast.Cut(node.pos()),
              accept(e1, context)
            )
          )
        );
      }
    }
    result.add(accept(body.get(body.size() - 1), context));
    return new N_Alternation(node.pos(), result);
  }
  
  @Override
  protected Expression visit(N_Sequence node, Context context) {    
    List<Expression> result = list();
    for(Expression e:node){
      result.add(e.accept(this, context));
    }
    return new Ast.N_Sequence(node.pos(), result);
  }

  @Override
  protected Expression visit(NonTerminal node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(NotPredicate node, Context context) {
    return new NotPredicate(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(Optional node, Context context) {
    return new Optional(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(Repetition node, Context context) {
    return new Repetition(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(RepetitionPlus node, Context context) {
    return new RepetitionPlus(node.pos(), accept(node.body(), context));
  }

  @Override
  protected Expression visit(SemanticPredicate node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(SetValueAction node, Context context) {
    return new SetValueAction(
      node.pos(), accept(node.body(), context), node.code()
    );
  }

  @Override
  protected Expression visit(StringLiteral node, Context context) {
    return node;
  }

  @Override
  protected Expression visit(Wildcard node, Context context) {
    return node;
  }
  
  /**
   * Check whether e1 is prefix of e2 or not.
   * @param e1
   * @param e2
   */
  static boolean isPrefixOf(Expression e1, Expression e2) {
    if(e1 instanceof StringLiteral) {
      if(e2 instanceof StringLiteral) {
        return ((StringLiteral)e2).value().startsWith(((StringLiteral)e1).value());
      }else if(e2 instanceof CharClass) {
        String e1v = ((StringLiteral)e1).value();
        if(e1v.length() > 1) return false;
        char first = e1v.charAt(0);
        return isEqual(first, (CharClass)e2);
      }else if(e2 instanceof Wildcard) {
        return false;
      }else throw new AssertionError("should not reach here");
    }else if(e1 instanceof CharClass) {
      if(e2 instanceof StringLiteral) {
        return isContained(((StringLiteral)e2).value().charAt(0), (CharClass)e1);
      }else if(e2 instanceof CharClass) {
        CharClass c1 = (CharClass)e1;
        CharClass c2 = (CharClass)e2;
        for(CharClass.Element ce:c2.elements){
          if(ce instanceof CharClass.Range) {
            char start = ((CharClass.Range)ce).start;
            char end = ((CharClass.Range)ce).end;
            for(int i = start; i<= end; i++) {
              if(c2.positive != isContained((char)i, (CharClass)c1)) return false;
            }
          }else {
            if(c2.positive != isContained(((CharClass.Char)ce).value, c1)) return false;
          }
        }
        return true;
      }else if(e2 instanceof Wildcard) {
        return false;
      }else throw new AssertionError("should not reach here");
    }else if(e1 instanceof Wildcard) {
      return true;
    }else throw new AssertionError("should not reach here");
  }
  
  public static boolean isEqual(char codePoint, CharClass c) {
    return c.positive
         && c.elements.size() == 1 
         && c.elements.get(0) instanceof CharClass.Char
         && codePoint == ((CharClass.Char)c.elements.get(0)).value;
  }

  public static boolean isContained(char codePoint, CharClass c) {
    boolean contained = false;
    OUTER:
    for(CharClass.Element ce:c.elements){
      if(ce instanceof CharClass.Range) {
        char start = ((CharClass.Range)ce).start;
        char end = ((CharClass.Range)ce).end;
        for(int i = start; i <= end; i++) {
          if(codePoint == i) {
            contained = true;
            break OUTER;
          }
        }
      }else {
        if(codePoint == ((CharClass.Char)ce).value) {
          contained = true;
          break OUTER;
        }
      }
    }
    return c.positive == contained;
  }
  
  private static Set<String> toStringSet(Set<Expression> es) {
    Set<String> s = CollectionUtil.set();
    for(Expression e:es) {
      if(e instanceof StringLiteral) {
        s.add(((StringLiteral)e).value());
      }else if(e instanceof Wildcard) {
        s.addAll(ALPHABETS);
      }else if(e instanceof CharClass) {
        CharClass c = (CharClass)e;
        for(CharClass.Element ce:c.elements) {
          if(ce instanceof CharClass.Range) {
            char start = ((CharClass.Range)ce).start;
            char end = ((CharClass.Range)ce).end;
          }
        }
      }
    }
    return s;
  }
  
  static final Set<String> ALPHABETS = CollectionUtil.set();
  static { 
    for(int i = 0; i <= Character.MAX_VALUE; i++) {
      ALPHABETS.add(Character.toString((char)i));
    }
  }
}
