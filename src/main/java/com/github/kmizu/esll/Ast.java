package com.github.kmizu.esll;

import java.util.List;

/**
 * This class represent's ASTs of ESLL.
 * This class is used only for namespace.
 * @author Kota Mizushima
 */
public class Ast {
  public static class Pos {
    public final int line;
    public final int column;
    public Pos(int line, int column) {
      this.line = line;
      this.column = column;
    }    
  }
  public static abstract class Node {
    public final Pos pos;
    public Node(Pos pos) {
      this.pos = pos;
    }
    public abstract <R, C> R accept(Visitor<R, C> visitor, C context);
  }
  public enum Operator {PLUS, MINUS, MULT, DIV, MOD, ASSIGN}
  public static abstract class Expression extends Node {
    public Expression(Pos pos) { super(pos); }
  }
  public static class BinaryExpression extends Expression {
    public final Operator op;
    public final Expression lhs, rhs;
    public BinaryExpression(Pos pos, Operator op, Expression lhs, Expression rhs) {
      super(pos);
      this.op = op;
      this.lhs = lhs;
      this.rhs = rhs;
    }
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class FunctionCall extends Expression {
    public final String name;
    public final List<Expression> params;    
    public FunctionCall(Pos pos, String name, List<Expression> params) {
      super(pos);
      this.name = name;
      this.params = params;
    }    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  
  public static class StringLiteral extends Expression {
    public final String value;

    public StringLiteral(Pos pos, String value) {
      super(pos);
      this.value = value;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  public static class NumberLiteral extends Expression {
    public final int value;

    public NumberLiteral(Pos pos, int value) {
      super(pos);
      this.value = value;
    }    
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  public static class Identifier extends Expression {
    public final String name;

    public Identifier(Pos pos, String name) {
      super(pos);
      this.name = name;
    }

    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Program extends Node {
    public final List<Declaration> declarations;
    public Program(Pos pos, List<Declaration> declarations) {
      super(pos);
      this.declarations = declarations;
    }
    
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public abstract static class Declaration extends Node {
    public Declaration(Pos pos) {
      super(pos);
    }
  }
  
  public static class GlobalVariableDeclaration extends Declaration {
    public final String name;

    public GlobalVariableDeclaration(Pos pos, String name) {
      super(pos);
      this.name = name;
    }
    
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  
  public static class FunctionDeclaration extends Declaration {
    public final String name;
    public final List<String> params;
    public final Statement body;
    public FunctionDeclaration(Pos pos, String name, List<String> params, Statement body) {
      super(pos);
      this.name = name;
      this.params = params;
      this.body = body;
    }
    
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return visitor.visit(this, context);
    }        
  }
  
  public abstract static class Statement extends Node {
    public Statement(Pos pos) {
      super(pos);
    }   
  }
  public static class PrintStatement extends Statement {
    public final Expression arg;

    public PrintStatement(Pos pos, Expression arg) {
      super(pos);
      this.arg = arg;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  public static class ExpressionStatement extends Statement {
    public final Expression exp;

    public ExpressionStatement(Pos pos, Expression exp) {
      super(pos);
      this.exp = exp;
    }    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  public static class BlockStatement extends Statement {
    public final List<Statement> elements;

    public BlockStatement(Pos pos, List<Statement> elements) {
      super(pos);
      this.elements = elements;
    }        
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  public static class IfStatement extends Statement {
    public final Expression condition;
    public final Statement thenBody;
    public final Statement elseBody;/* nullable */
    public IfStatement(Pos pos, Expression condition, Statement thenBody,
      Statement elseBody) {
      super(pos);
      this.condition = condition;
      this.thenBody = thenBody;
      this.elseBody = elseBody;
    } 
    
    public <R, C> R accept(Ast.Visitor<R,C> visitor, C context) {
      return null;
    }
  }
  
  public static class Visitor<R, C> {
    public R visit(Program program, C context) { return null; }
    public R visit(GlobalVariableDeclaration declaration, C context) { return null; }
    public R visit(FunctionDeclaration declaration, C context) { return null; }
    public R visit(BinaryExpression exp, C context) { return null; }
    public R visit(FunctionCall exp, C context) { return null; }
    public R visit(StringLiteral exp, C context) { return null; }
    public R visit(NumberLiteral exp, C context) { return null; }
    public R visit(Identifier exp, C context) { return null; }
    public R visit(PrintStatement stmt, C context) { return null; }
    public R visit(ExpressionStatement stmt, C context) { return null; }
    public R visit(BlockStatement stmt, C context) { return null; }
    public R visit(IfStatement stmt, C context) { return null; }
  }
}
