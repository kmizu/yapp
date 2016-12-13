package com.github.kmizu.yapp.translator;

import java.util.Map;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Automata;
import com.github.kmizu.yapp.DirectedGraph;
import com.github.kmizu.yapp.Regex;
import com.github.kmizu.yapp.Regex2Dfa;
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
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.Automata.Dfa;
import com.github.kmizu.yapp.util.CollectionUtil;

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
