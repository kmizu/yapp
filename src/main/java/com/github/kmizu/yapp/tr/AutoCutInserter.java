package com.github.kmizu.yapp.tr;

import java.util.List;
import java.util.Map;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Automata;
import com.github.kmizu.yapp.Position;
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

import static com.github.kmizu.yapp.util.CollectionUtil.*;

public class AutoCutInserter extends Visitor<Expression, AutoCutInserter.Context>
  implements Translator<Ast.Grammar, Ast.Grammar> {
  static final class Context {
    Peg2DfaTranslator toDfa;
    Map<Symbol, Boolean> succeedRules;
    int countCutInserted;
    Context(Peg2DfaTranslator toDfa, Map<Symbol, Boolean> succeedRules) {
      this.toDfa = toDfa;
      this.succeedRules = succeedRules;
    }
  }
  

  public Grammar translate(Grammar grammar) {
    List<Rule> rules = list();
    Map<Symbol, Expression> mapping = CollectionUtil.map();
    for(Rule r:grammar) {
      mapping.put(r.name(), r.body());
    }
    Peg2DfaTranslator toDfa = new Peg2DfaTranslator(
      RefGraphMaker.INSTANCE.translate(grammar), mapping
    );
    Map<Symbol, Boolean> succeedRules =
      SucceedRuleDetector.INSTANCE.translate(grammar);
    Context context = new Context(toDfa, succeedRules);
    for(Rule r:grammar) {
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
    return new Grammar(grammar.pos(), grammar.name(), grammar.macros(), rules);
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
    // for e_1 / e_2 / e_3 ... / e_n, do following process.
    OUTER:
    for(int i = 0; i < body.size() - 1; i++) {
      // If e_i is not N_Sequence(a_1 a_2 ... a_m), skip following process
      // since i'ts unable to insert cut in e_i.
      Expression e = body.get(i);
      if(!(e instanceof N_Sequence)) {
        result.add(accept(e, context));
        continue OUTER;
      }
      List<Expression> es = ((N_Sequence)e).body();
      for(int j = 1; j < es.size(); j++) {
        if(
           allDisjoint(
             new N_Sequence(e.pos(), es.subList(0, j)), 
             body.subList(i + 1, body.size()),
             context.toDfa
           ) 
        || // if the last element of a sequence is a certainly succeed rule,
           // enable to insert cut before it.
           (j == es.size() - 1 && 
            es.get(j) instanceof NonTerminal &&
            context.succeedRules.get(((NonTerminal)es.get(j)).name())
           ) 
        ){
          //it's enable to insert cut
          List<Expression> esResult = list();
          for(Expression e2:es.subList(0, j)) {
            esResult.add(accept(e2, context));
          }
          esResult.add(new Cut(new Position(-1, -1)));
          for(Expression e2:es.subList(j, es.size())){
            esResult.add(accept(e2, context));
          }
          result.add(new N_Sequence(e.pos(), esResult));
          continue OUTER;
        }
      }
      result.add(accept(e, context));
    }
    result.add(accept(body.get(body.size() - 1), context));
    return new N_Alternation(node.pos(), result);
  }

  @Override
  protected Expression visit(N_Sequence node, Context context) {
    List<Expression> result = list();
    for(Expression e:node) {
      result.add(accept(e, context));
    }
    return new N_Sequence(node.pos(), result);
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
  
  private boolean allDisjoint(
    Expression a, List<Expression> bs, Peg2DfaTranslator toDfa
  ) {
    Automata.Dfa dfaA = toDfa.translate(a);
    if(dfaA == Automata.Dfa.ERROR) return false;
    for(Expression b:bs) {
      if(b instanceof N_Sequence) {
        List<Expression> seq = ((N_Sequence)b).body();
        int i = seq.size();
        for(; i > 0; i--) {
          Automata.Dfa dfaB = toDfa.translate(
            new N_Sequence(b.pos(), seq.subList(0, i))
          );
          if(dfaB == Automata.Dfa.ERROR) continue;
          Automata.Dfa andDfa = dfaA.and(dfaB);
          if(andDfa.isEmpty()) break;
        }
        if(i == 0) return false;
      }else {
        Automata.Dfa dfaB = toDfa.translate(b);
        if(dfaB == Automata.Dfa.ERROR) return false;
        if(!dfaA.and(dfaB).isEmpty()) return false;
      }
    }
    return true;
  }
}
