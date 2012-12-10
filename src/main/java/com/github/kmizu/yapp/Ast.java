/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.kmizu.yapp.util.SystemProperties;

/**
 * This class holds node classes of AST of Yapp.
 * This class is used only for namespace.
 * @author Kota Mizushima
 */
public class Ast {
  public static class Action extends Expression {
    private Expression body;
    private String  code;

    public Action(Position pos, Expression body, String code) {
      super(pos);
      this.body = body;
      this.code = code;
    }

    public Expression body() {
      return body;
    }
    
    public String code() {
      return code;
    }

    @Override
    public String toString() {
      return body + " <[" + code + "]>";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class BoundedExpression extends Expression {
    private Expression body;
    public BoundedExpression(Expression body) {
      super(body.pos());
      this.body = body;
    }
    
    public Expression body() {
      return body;
    }
    
    @Override
    public String toString() {
      return "bounded{" + body + "}";
    }
    
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }

  public static class SetValueAction extends Expression {
    private Expression body;
    private String code;

    public SetValueAction(Position pos, Expression body, String code) {
      super(pos);
      this.body = body;
      this.code = code;
    }

    public Expression body() {
      return body;
    }

    public String code() {
      return code;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return body + " %{" + code + "}";
    }
  }


  public static class N_Alternation extends VarArgExpression {
    public N_Alternation(Position pos, List<Expression> expressions){
      super(pos, expressions);
    }


    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("(");
      buf.append(body.get(0).toString());
      for(Expression e : body.subList(1,  body.size())){
        buf.append(" / ");
        buf.append(e.toString());
      }
      buf.append(")");
      return new String(buf);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static abstract class BinaryExpression extends Expression {
    private Expression lhs;
    private Expression rhs;

    public BinaryExpression(Position pos, Expression lhs, Expression rhs){
      super(pos);
      this.lhs = lhs;
      this.rhs = rhs;
    }

    public Expression lhs() {
      return lhs;
    }

    public Expression rhs() {
      return rhs;
    }
  }
  
  public static class Cut extends Expression {
    public Cut(Position pos){
      super(pos);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "^";
    }
  }

  public static class Fail extends Expression {
    public Fail(Position pos){
      super(pos);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }

    @Override
    public String toString() {
      return "fail";
    }
  }

  public static class Empty extends Terminal {
    public Empty(Position pos){
      super(pos);
    }

    public String toString() {
      return "()";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static abstract class Expression extends Node {
    public Expression(Position pos){
      super(pos);
    }
  }
  
  public static abstract class Terminal extends Expression {
    public Terminal(Position pos){
      super(pos);
    }
  }

  public static class Grammar extends Node implements Iterable<Rule> {
    private Symbol name;
    private final List<MacroDefinition> macros;
    private final List<Rule> rules;

    public Grammar(Position pos, Symbol name, List<MacroDefinition> macros, List<Rule> rules){
      super(pos);
      this.name = name;
      this.macros = macros;
      this.rules = rules;
    }

    public Iterator<Rule> iterator() {
      return rules.iterator();
    }

    public Symbol name() {
      return name;
    }
    
    public void setName(Symbol name) {
      this.name = name;
    }
    
    public List<MacroDefinition> macros() {
      return macros;
    }

    public List<Rule> getRules() {
      return rules;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("grammar " + name + ";");
      buf.append(SystemProperties.LINE_SEPARATOR);
      buf.append(SystemProperties.LINE_SEPARATOR);
      for(Rule r : rules) {
        buf.append(r);
        buf.append(SystemProperties.LINE_SEPARATOR);
        buf.append(SystemProperties.LINE_SEPARATOR);
      }
      return new String(buf);
    }
  }

  public static abstract class Node {
    private Position pos;

    public Node(Position pos) {
      this.pos = pos;
    }

    public Position pos() {
      return pos;
    }

    public abstract <R, C> R accept(Visitor<R, C> visitor, C context);
  }

  public static class NonTerminal extends Expression {
    private Symbol name;
    private Symbol var;

    public NonTerminal(Position pos, Symbol name, Symbol var) {
      super(pos);
      this.name = name;
      this.var  = var;
    }

    public NonTerminal(Position pos, Symbol name){
      this(pos, name, null);
    }

    public Symbol name() {
      return name;
    }

    public Symbol var() {
      return var;
    }

    @Override
    public String toString() {
      return var != null ? var + ":" + name.toString() : name.toString();
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class MacroVariable extends Expression {
    private final Symbol name;
    private final Symbol var;
    
    public MacroVariable(Position pos, Symbol name, Symbol var) {
      super(pos);
      this.name = name;
      this.var = var;
    }

    public Symbol name() {
      return name;
    }
    
    public Symbol var() {
      return var;
    }
    
    @Override
    public String toString() {
      return var != null ? var + ":" + name.toString() : name.toString();
    }
    
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class MacroCall extends Expression {
    private final Symbol name;
    private final List<Expression> params;
    
    public MacroCall(Position pos, Symbol name, List<Expression> params) {
      super(pos);
      this.name = name;
      this.params = params;
    }

    public Symbol name() {
      return name;
    }
    
    public List<Expression> params() {
      return params;
    }
    
    @Override
    public String toString() {
      return name + "(" + params + ")";
    }
    
    @Override
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class NotPredicate extends Expression {
    private Expression body;
    
    public NotPredicate(Position pos, Expression body){
      super(pos);
      this.body = body;
    }
    
    public Expression body() {
      return body;
    }

    @Override
    public String toString() {
      return "!(" + body + ")";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class AndPredicate extends Expression {
    private Expression body;
    public AndPredicate(Position pos, Expression expr){
      super(pos);
      this.body = expr;
    }
    public Expression body() {
      return body;
    }
    @Override
    public String toString() {
      return "&(" + body + ")";
    }
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class SemanticPredicate extends Expression {
    private String predicate;
    
    public SemanticPredicate(Position pos, String expression){
      super(pos);
      this.predicate = expression;
    }
    public String predicate() {
      return predicate;
    }

    @Override
    public String toString() {
      return "&{" + predicate + "}";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class Repetition extends Expression {
    private Expression body;
    public Repetition(Position pos, Expression expr){
      super(pos);
      this.body = expr;
    }
    public Expression body() {
      return body;
    }

    @Override
    public String toString() {
      return "(" + body + ")*";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class RepetitionPlus extends Expression {
    private Expression body;
    
    public RepetitionPlus(Position pos, Expression body){
      super(pos);
      this.body = body;
    }
    
    public Expression body() {
      return body;
    }

    @Override
    public String toString() {
      return "(" + body + ")+";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class Optional extends Expression {
    private Expression body;
    
    public Optional(Position pos, Expression expr){
      super(pos);
      this.body = expr;
    }
    
    public Expression body() {
      return body;
    }

    @Override
    public String toString() {
      return "(" + body + ")?";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class MacroDefinition extends Node {
    private final Symbol name;
    private final List<Symbol> formalParams;
    private final Expression body;
    public MacroDefinition(Position pos, Symbol name,
      List<Symbol> formalParams, Expression body) {
      super(pos);
      this.name = name;
      this.formalParams = formalParams;
      this.body = body;
    }
    public Symbol name() { 
      return name; 
    }
    public List<Symbol> formalParams() {
      return formalParams;
    }
    public Expression body() {
      return body;
    }
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      if(formalParams.size() > 0) {
        builder.append(formalParams.get(0));
        for(Symbol formalParam:formalParams.subList(1, formalParams.size())) {
          builder.append(", " + formalParam);
        }
      }
      return String.format(
        "macro %s(%s) = %s", name, new String(builder), body
      );
    }
    @Override
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class Rule extends Node {
    public static final int BOUNDED = 1;
    private int flags;
    private Symbol name;
    private Symbol type;
    private Expression expression;
    private String code;

    public Rule(Position pos, int flags, Symbol name, Symbol type, Expression expression, String code){
      super(pos);
      this.flags = flags;
      this.name = name;
      this.type = type;
      this.expression = expression;
      this.code = code;
    }
    
    public int flags() {
      return flags;
    }

    public Symbol name() {
      return name;
    }

    public Symbol type() {
      return type;
    }

    public Expression body() {
      return expression;
    }

    public String code() {
      return code;
    }

    @Override
    public String toString() {
      return ((flags & BOUNDED) != 0 ? "bounded " : "") + 
              name + " = " +  expression +  " ;";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class N_Sequence extends VarArgExpression {
    public N_Sequence(Position pos, List<Expression> expressions) {
      super(pos, expressions);
    }

    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("(");
      buf.append(body.get(0).toString());
      for(Expression e : body.subList(1,  body.size())){
        buf.append(" ");
        buf.append(e.toString());
      }
      buf.append(")");
      return new String(buf);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class StringLiteral extends Terminal {
    private String value;
    private Symbol var;

    public StringLiteral(Position pos, String value, Symbol var){
      super(pos);
      this.value = value;
      this.var = var;
    }

    public StringLiteral(Position pos, String value){
      this(pos, value, null);
    }

    public String value() {
      return value;
    }

    public Symbol var() {
      return var;
    }

    @Override
    public String toString() {
      return "\"" + value + "\"";
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Wildcard extends Terminal {
    private final Symbol var;
    
    public Wildcard(Position pos, Symbol var) {
      super(pos);
      this.var = var;
    }
    
    public Symbol var() {
      return var;
    }
    
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return ".";
    }
  }

  public static class CharClass extends Terminal {
    public abstract static class Element {}
    public static class Char extends Element {
      public final char value;
      public Char(char value) {
        this.value = value;
      }
    }
    public static class Range extends Element {
      public final char start;
      public final char end;
      public Range(char start, char end) {
        this.start = start;
        this.end = end;
      }
    }
    private Symbol var;
    public final boolean positive;
    public final List<Element> elements;

    public CharClass(Position pos, boolean positive, List<Element> elements){
      this(pos, positive, elements, null);
    }

    public CharClass(
      Position pos, boolean positive, List<Element> elements, Symbol var
    ){
      super(pos);
      this.positive = positive;
      this.elements = Collections.unmodifiableList(elements);
      this.var = var;
    }

    public Symbol var() {
      return var;
    }

    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      if(var != null) {
        buf.append(var);
        buf.append(':');
      }
      buf.append('[');
      if(!positive) {
        buf.append('^');
      }
      for(Element e:elements) {
        if(e instanceof Char) {          
          switch(((Char)e).value) {
          case '\t': buf.append("\\t"); break;
          case '\f': buf.append("\\f"); break;
          case '\r': buf.append("\\r"); break;
          case '\n': buf.append("\\n"); break;
          default: buf.append(((Char)e).value); break;
          }
        }else {
          buf.append(((Range)e).start);
          buf.append('-');
          buf.append(((Range)e).end);
        }
      }
      buf.append(']');
      return new String(buf);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }


  public static abstract class VarArgExpression extends Expression implements Iterable<Expression> {
    protected List<Expression> body;

    public VarArgExpression(Position pos, List<Expression> expressions){
      super(pos);
      this.body = expressions;
    }

    public List<Expression> body() {
      return body;
    }

    public Iterator<Expression> iterator() {
      return body.iterator();
    }

    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(body.get(0).toString());
      for(Expression e : body.subList(1, body.size())){
        buf.append(" | ");
        buf.append(e.toString());
      }
      return new String(buf);
    }
  }

  public static class Visitor<R, C> {
    protected R visit(Empty node, C context){
      return null;
    }
    protected R visit(Action node, C context){ 
      return null;
    }
    protected R visit(SetValueAction node, C context){ 
      return null;
    }
    protected R visit(N_Alternation node, C context){ 
      return null;
    }
    protected R visit(Cut node, C context){ 
      return null;
    }
    protected R visit(Fail node, C context){ 
      return null;
    }
    protected R visit(SemanticPredicate node, C context) { 
      return null;
    }
    protected R visit(AndPredicate node, C context){ 
      return null;
    }
    protected R visit(NotPredicate node, C context){ 
      return null;
    }
    protected R visit(Grammar node, C context){ 
      return null;
    }
    protected R visit(NonTerminal node, C context){ 
      return null;
    }
    protected R visit(MacroCall node, C context){
      return null;
    }
    protected R visit(MacroVariable node, C context){
      return null;
    }
    protected R visit(Repetition node, C context){ 
      return null;
    }
    protected R visit(RepetitionPlus node, C context){ 
      return null;
    }
    protected R visit(Optional node, C context){ 
      return null;
    }
    protected R visit(Rule node, C context){ 
      return null;
    }
    protected R visit(MacroDefinition node, C context) {
      return null;
    }
    protected R visit(N_Sequence node, C context){ 
      return null;
    }
    protected R visit(StringLiteral node, C context){ 
      return null;
    }
    protected R visit(CharClass node, C context){ 
      return null;
    }
    protected R visit(Wildcard node, C context){ 
      return null;
    }
    protected R visit(BoundedExpression node, C context){ 
      return node.body().accept(this, context);
    }
    
    /**
     * using this method, node.accept(this, context) can be written to
     * accept(node, context).
     * @param node
     * @param context
     * @return
     */
    protected final R accept(Node node, C context) {
      return node.accept(this, context);
    }
  }
  
  public static class DepthFirstVisitor<C> extends Visitor<Void, C> {
    @Override
    protected Void visit(Action node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(AndPredicate node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(CharClass node, C context) {
      return null;
    }

    @Override
    protected Void visit(Cut node, C context) {
      return null;
    }

    @Override
    protected Void visit(Empty node, C context) {
      return null;
    }

    @Override
    protected Void visit(Fail node, C context) {
      return null;
    }

    @Override
    protected Void visit(Grammar node, C context) {
      for(Rule r:node){
        r.body().accept(this, context);
      }
      return null;
    }

    @Override
    protected Void visit(N_Alternation node, C context) {
      for(Expression e:node){
        e.accept(this, context);
      }
      return null;
    }

    @Override
    protected Void visit(N_Sequence node, C context) {
      for(Expression e:node){
        e.accept(this, context);
      }
      return null;
    }

    @Override
    protected Void visit(NonTerminal node, C context) {
      return null;
    }
    
    @Override
    protected Void visit(MacroVariable node, C context) {
      return null;
    }
    
    @Override
    protected Void visit(MacroCall node, C context) {
      for(Expression param:node.params) param.accept(this, context);
      return null;
    }

    @Override
    protected Void visit(NotPredicate node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(Optional node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(Repetition node, C context) {
      return node.body.accept(this, context);
    }

    @Override
    protected Void visit(RepetitionPlus node, C context) {
      return node.body.accept(this, context);
    }
    
    protected Void visit(BoundedExpression node, C context) {
      return node.body.accept(this, context);
    }

    @Override
    protected Void visit(Rule node, C context) {
      return node.body().accept(this, context);
    }
    
    protected Void visit(MacroDefinition node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(SemanticPredicate node, C context) {
      return null;
    }

    @Override
    protected Void visit(SetValueAction node, C context) {
      return node.body().accept(this, context);
    }

    @Override
    protected Void visit(StringLiteral node, C context) {
      return null;
    }

    @Override
    protected Void visit(Wildcard node, C context) {
      return null;
    }    
  }
}
