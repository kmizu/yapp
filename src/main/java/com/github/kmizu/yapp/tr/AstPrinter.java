package com.github.kmizu.yapp.tr;

import java.util.List;
import java.util.Map;

import com.github.kmizu.yapp.Ast;
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
import com.github.kmizu.yapp.Ast.Node;
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
import com.github.kmizu.yapp.Pair;
import com.github.kmizu.yapp.util.CollectionUtil;

import static com.github.kmizu.yapp.util.CollectionUtil.map;
import static com.github.kmizu.yapp.util.CollectionUtil.t;

public class AstPrinter extends Ast.Visitor<Void, Ast.Node> implements Translator<Ast.Grammar, Ast.Grammar> {
  private static <T> Class<?> c(Class<T> clazz) {
    return clazz;
  }
  private static int prec(Node n) {
    return n == null ? Integer.MAX_VALUE : precedences.get(n.getClass());
  }
  private static Map<Class, Integer> precedences = CollectionUtil.map(
     t(c(N_Alternation.class), 5),
     t(c(N_Sequence.class), 4),
     t(c(Action.class), 3),
     t(c(SetValueAction.class), 3),
     t(c(AndPredicate.class), 2),
     t(c(NotPredicate.class), 2),
     t(c(Optional.class), 1),
     t(c(Repetition.class), 1),
     t(c(RepetitionPlus.class), 1),
     t(c(SemanticPredicate.class), 0),
     t(c(NonTerminal.class), 0),
     t(c(CharClass.class), 0),
     t(c(Empty.class), 0),
     t(c(Cut.class), 0),
     t(c(Fail.class), 0),
     t(c(Wildcard.class), 0),
     t(c(StringLiteral.class), 0)
  );
  public Grammar translate(Grammar from) {
    accept(from, null);
    return from;
  }

  @Override
  public Void visit(Action node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    accept(node.body(), node);
    p(" ");
    p("<[ ");
    p("%s", node.code());
    p(" ]>");
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(AndPredicate node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    p("&");
    accept(node.body(), node);
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(CharClass node, Node context) {
    p("%s", node.toString());
    return null;
  }

  @Override
  public Void visit(Cut node, Node context) {
    p("^");
    return null;
  }

  @Override
  public Void visit(Empty node, Node context) {
    p("\"\"");
    return null;
  }

  @Override
  public Void visit(Fail node, Node context) {
    p("fail");
    return null;
  }

  @Override
  public Void visit(Grammar node, Node context) {
    n("//nrules = %d", node.getRules().size());
    n("grammar %s;", node.name());
    for(Rule r:node) accept(r, null);
    return null;
  }

  @Override
  public Void visit(N_Alternation node, Node context) {
    List<Expression> body = node.body();
    if(prec(node) >= prec(context)) p("(");
    accept(body.get(0), node);
    for(Expression e:body.subList(1, body.size())) {
      p(" / ");
      accept(e, node);
    }
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(N_Sequence node, Node context) {
    List<Expression> body = node.body();
    if(prec(node) >= prec(context)) p("(");
    accept(body.get(0), node);
    for(Expression e:body.subList(1, body.size())) {
      p(" ");
      accept(e, node);
    }
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(NonTerminal node, Node context) {
    if(node.variable() != null) p("%s:", node.variable());
    p("%s", node.name());
    return null;
  }

  @Override
  public Void visit(NotPredicate node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    p("!");
    accept(node.body(), node);
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(Optional node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    accept(node.body(), node);
    p("?");
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(Repetition node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    accept(node.body(), node);
    p("*");
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(RepetitionPlus node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    accept(node.body(), node);
    p("+");
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(Rule node, Node context) {
    if(node.vtype() != null) p("%s ", node.vtype());
    p("%s = ", node.name());
    accept(node.body(), null);
    n(";");
    return null;
  }

  @Override
  public Void visit(SemanticPredicate node, Node context) {
    p("&{%s}", node.predicate());
    return null;
  }

  @Override
  public Void visit(SetValueAction node, Node context) {
    if(prec(node) >= prec(context)) p("(");
    accept(node.body(), node);
    p(" %%{");
    p("%s", node.code());
    p("}");
    if(prec(node) >= prec(context)) p(")");
    return null;
  }

  @Override
  public Void visit(StringLiteral node, Node context) {
    p("\"%s\"", node.value());
    return null;
  }

  @Override
  public Void visit(Wildcard node, Node context) {
    p(".");
    return null;
  }

  private void p(String format, Object... args) {
    System.out.printf(format, args);
  }

  private void n(String format, Object... args) {
    p(format, args);
    System.out.println();
  }
}
