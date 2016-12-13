package com.github.kmizu.yapp.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.github.kmizu.yapp.translator.FirstSetCollector.FirstSetCannotBeComputed;
import com.github.kmizu.yapp.util.CollectionUtil;

import static com.github.kmizu.yapp.util.CollectionUtil.*;

import com.github.kmizu.yapp.Position;

public class AutoCutInserterUsingFollowSet extends Visitor<Expression, AutoCutInserterUsingFollowSet.Context>
  implements Translator<Ast.Grammar, Ast.Grammar> {  
  static final class Context {
    Map<Symbol, Expression> bindings;
    Set<Expression> nul;
    Set<Expression> fail;
    Set<Expression> finite;
    FirstSetCollector firstSet;
    FollowSetCollector followSet;
    EofDetector eofDetector;
    int countCutInserted;
    public Context(Map<Symbol, Expression> bindings, Set<Expression> nul, Set<Expression> fail, Set<Expression> finite) {
      this.bindings = bindings;
      this.fail = fail;
      this.nul = nul;
      this.finite = finite;
      this.firstSet = new FirstSetCollector(bindings, nul, fail, false);
      this.followSet = new FollowSetCollector(bindings, nul, fail);
      this.eofDetector = new EofDetector(bindings);
    }
  }
  
  public AutoCutInserterUsingFollowSet() {
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
    List<Expression> result = list();
    for(Expression e:node.body()) {
      result.add(e.accept(this, context));
    }
    return new Ast.N_Alternation(node.pos(), result);
  }
  
  @Override
  protected Expression visit(N_Sequence node, Context context) {
    List<Expression> result = list();
    for(Expression e:node.body()) {
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
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  protected Expression visit(Repetition node, Context context) {
    List<Expression> follows = new ArrayList<Expression>(context.followSet.translate(node));
    Set<Expression> f_e1 = context.firstSet.translate(node.body());    
    if((!context.finite.contains(node.body())) 
     && follows.size() == 1 
    ) {
      if( (!context.nul.contains(follows.get(0)))
       && (!context.nul.contains(node.body()))
       && ExpressionUtil.disjoint(f_e1, context.firstSet.translate(follows.get(0)))
      ) {
        try {
          Expression predE = 
            ExpressionUtil.newAlternation(
              node.pos(),
              ExpressionUtil.compact(context.firstSet.translate(follows.get(0)))
            );
          return new Ast.Repetition(
            node.pos(),
            new Ast.N_Sequence(
              node.pos(),
              list(
                new Ast.NotPredicate(node.pos(), predE),
                new Ast.Cut(node.pos()),
                node.body().accept(this, context)
              )
            )
          );
        }catch(FirstSetCannotBeComputed ex) {
          return new Repetition(node.pos(), accept(node.body(), context));
        }
      }else if(context.eofDetector.translate(follows.get(0))){
        return new Ast.Repetition(
          node.pos(),
          new Ast.N_Sequence(
            node.pos(),
            list(
              new Ast.NotPredicate(node.pos(), 
                new Ast.NotPredicate(node.pos(),
                  new Ast.Wildcard(node.pos(), null)
                )
              ),
              new Ast.Cut(node.pos()),
              node.body().accept(this, context)
            )
          )
        );
      }else {
        return new Repetition(node.pos(), accept(node.body(), context));
      }
    } else {
      return new Repetition(node.pos(), accept(node.body(), context));
    }
  }

  @Override
  protected Expression visit(RepetitionPlus node, Context context) {
    throw new UnsupportedOperationException("This operation is not supported");
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
  
  private static boolean disjoint(Set<Expression> f1, Set<Expression> f2) {
    for(Expression e1:f1) {
      for(Expression e2:f2) {
        if(isPrefixOf(e1, e2) || isPrefixOf(e2, e1)) return false;
      }
    }
    return true;
  }
  
  private static Expression newAlternation(Position pos, Set<Expression> elements) {
    List<Expression> newElements = CollectionUtil.list();
    newElements.addAll(elements);
    Collections.sort(newElements, new Comparator<Expression>() {
      public int compare(Expression o1, Expression o2) {
        if(o1 instanceof StringLiteral) {
          if(o2 instanceof StringLiteral) {
            int length1 = ((StringLiteral)o1).value().length();
            int length2 = ((StringLiteral)o2).value().length();
            return length1 > length2 ? -1 :
                    length1 < length2 ? 1 :
                    0;
          }else{
            return 1;
          }          
        } else {
          return 0;
        }
      }
    });
    return new Ast.N_Alternation(pos, newElements);
  }
    
  /**
   * Check whether e1 is prefix of e2 or not.
   * @param e1
   * @param e2
   */
  private static boolean isPrefixOf(Expression e1, Expression e2) {
    if(e1 instanceof StringLiteral) {
      if(e2 instanceof StringLiteral) {
        return ((StringLiteral)e2).value().startsWith(((StringLiteral)e1).value());
      }else if(e2 instanceof CharClass) {
        String e1v = ((StringLiteral)e1).value();
        if(e1v.length() > 1) return false;
        char first = e1v.charAt(0);
        return isContained(first, (CharClass)e2);
      }else if(e2 instanceof Wildcard) {
        return true;
      }else throw new AssertionError("should not reach here");
    }else if(e1 instanceof CharClass) {
      if(e2 instanceof StringLiteral) {
        return isContained(((StringLiteral)e2).value().charAt(0), (CharClass)e1);
      }else if(e2 instanceof CharClass) {
        CharClass c1 = (CharClass)e1;
        for(CharClass.Element ce:c1.elements){
          if(ce instanceof CharClass.Range) {
            char start = ((CharClass.Range)ce).start;
            char end = ((CharClass.Range)ce).end;
            for(int i = start; i<= end; i++) {
              if(c1.positive == isContained((char)i, (CharClass)e2)) return true;
            }
          }else {
            if(c1.positive == isContained(((CharClass.Char)ce).value, (CharClass)e1)) return true;
          }
        }
        return false;
      }else if(e2 instanceof Wildcard) {
        return true;
      }else throw new AssertionError("should not reach here");
    }else if(e1 instanceof Wildcard) {
      return true;
    }else throw new AssertionError("should not reach here");
  }

  private static boolean isContained(char codePoint, CharClass c) {
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
