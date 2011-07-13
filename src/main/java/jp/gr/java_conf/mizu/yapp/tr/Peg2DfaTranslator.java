package jp.gr.java_conf.mizu.yapp.tr;

import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Automata;
import jp.gr.java_conf.mizu.yapp.DirectedGraph;
import jp.gr.java_conf.mizu.yapp.Regex;
import jp.gr.java_conf.mizu.yapp.Regex2Dfa;
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
import jp.gr.java_conf.mizu.yapp.Ast.Wildcard;
import jp.gr.java_conf.mizu.yapp.Automata.Dfa;
import jp.gr.java_conf.mizu.yapp.util.CollectionUtil;

public class Peg2DfaTranslator extends Ast.Visitor<Regex.Expression, Void>
  implements Translator<Ast.Expression, Automata.Dfa> {
  private final DirectedGraph<Symbol, Boolean> refGraph;
  private final Map<Symbol, Expression> mapping;
  
  public Peg2DfaTranslator(
    DirectedGraph<Symbol, Boolean> refGraph,
    Map<Symbol, Expression> mapping) {
    this.refGraph = refGraph;
    this.mapping = mapping;
  }

  public Dfa translate(Expression from) {
    Regex.Expression result = accept(from, null);
    if(result != Regex.ERROR) {
      result = new Regex.Sequence(
        result,
        new Regex.Repetition(new Regex.All())
      );
    }
    return Regex2Dfa.INSTANCE.compile(result);
  }

  @Override
  protected Regex.Expression visit(Action node, Void context) {
    return node.body().accept(this, context);
  }

  @Override
  protected Regex.Expression visit(AndPredicate node, Void context) {
    return Regex.ERROR;
  }

  @Override
  protected Regex.Expression visit(CharClass node, Void context) {
    Set<Character> chars = CollectionUtil.set();
    for(CharClass.Element e:node.elements) {
      if(e instanceof CharClass.Char) {
        chars.add(((CharClass.Char)e).value);
      }else {
        char rangeS = ((CharClass.Range)e).start;
        char rangeE = ((CharClass.Range)e).end;
        for(int i = rangeS; i <= rangeE; i++) {
          chars.add((char)i);
        }
      }
    }
    return new Regex.CharClass(!node.positive, chars);
  }

  @Override
  protected Regex.Expression visit(Cut node, Void context) {
    return Regex.ERROR;
  }

  @Override
  protected Regex.Expression visit(Empty node, Void context) {
    return new Regex.Empty();
  }

  @Override
  protected Regex.Expression visit(Fail node, Void context) {
    return Regex.ERROR;
  }

  @Override
  protected Regex.Expression visit(Grammar node, Void context) {
    return unsupported("Grammar");
  }

  @Override
  protected Regex.Expression visit(N_Alternation node, Void context) {
    Regex.Expression result = node.body().get(0).accept(this, context);
    if(result == Regex.ERROR) return Regex.ERROR;
    for (Expression e : node.body().subList(1, node.body().size())) {
      Regex.Expression newE = e.accept(this, context);
      if(newE == Regex.ERROR) return Regex.ERROR;
      result = new Regex.Alternation(result, newE);
    }
    return result;
  }

  @Override
  protected Regex.Expression visit(N_Sequence node, Void context) {
    Regex.Expression result = node.body().get(0).accept(this, context);
    if(result == Regex.ERROR) return Regex.ERROR;
    for (Expression e : node.body().subList(1, node.body().size())) {
      Regex.Expression newE = e.accept(this, context);
      if(newE == Regex.ERROR) return Regex.ERROR;
      result = new Regex.Sequence(result, e.accept(this, context));
    }
    return result;
  }

  @Override
  protected Regex.Expression visit(NonTerminal node, Void context) {
    if(!refGraph.getInfo(node.name())) {
      return mapping.get(node.name()).accept(this, context);
    } else {
      return Regex.ERROR;
    }
  }

  @Override
  protected Regex.Expression visit(NotPredicate node, Void context) {
    return Regex.ERROR;
  }

  @Override
  protected Regex.Expression visit(Optional node, Void context) {
    Regex.Expression e = node.body().accept(this, context);
    return e == Regex.ERROR ? Regex.ERROR 
                             : new Regex.Alternation(e, new Regex.Empty());
  }

  @Override
  protected Regex.Expression visit(Repetition node, Void context) {
    Regex.Expression e = node.body().accept(this, context);
    return e == Regex.ERROR ? Regex.ERROR : new Regex.Repetition(e);
  }

  @Override
  protected Regex.Expression visit(RepetitionPlus node, Void context) {
    return new N_Sequence(
      node.pos(), 
      CollectionUtil.list(
        node.body(), 
        new Repetition(node.pos(), node.body())
      )
    ).accept(this, context);
  }

  @Override
  protected Regex.Expression visit(Rule node, Void context) {
    return unsupported("Rule");
  }

  @Override
  protected Regex.Expression visit(SemanticPredicate node, Void context) {
    return Regex.ERROR;
  }

  @Override
  protected Regex.Expression visit(SetValueAction node, Void context) {
    return node.body().accept(this, context);
  }

  @Override
  protected Regex.Expression visit(StringLiteral node, Void context) {
    char[] content = node.value().toCharArray();
    if (content.length == 0)
      return new Regex.Empty();
    Regex.Expression e = new Regex.Char(content[0]);
    for (int i = 1; i < content.length; i++) {
      e = new Regex.Sequence(e, new Regex.Char(content[i]));
    }
    return e;
  }

  @Override
  protected Regex.Expression visit(Wildcard node, Void context) {
    return new Regex.All();
  }

  private <T> T unsupported(String type) {
    throw new UnsupportedOperationException("Peg2DfaTranslator#visit(" + type
        + ", " + "Void");
  }
}
