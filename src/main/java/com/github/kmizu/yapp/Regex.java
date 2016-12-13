/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp;

import java.util.Set;

import com.github.kmizu.yapp.Ast.CharClass.Char;
import com.github.kmizu.yapp.Ast.CharClass.Element;
import com.github.kmizu.yapp.Ast.CharClass.Range;

/**
 * This class represents the namespace which have AST(Abstract
 * Syntax Tree) classes of RE(Regular Expression).  Basically, 
 * this class doesn't have members except static classes or 
 * static interfaces or constants.
 * @author Kota Mizushima
 *
 */
public class Regex {
  public static abstract class Expression {
    public abstract <R, C> R accept(Visitor<C, R> visitor, C context);
  }
  
  public static abstract class BinaryExpression extends Expression {
    public final Expression lhs;
    public final Expression rhs;
    
    public BinaryExpression(Expression lhs, Expression rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
    }    
  }
  
  /**
   * An repetition expression (A*) of RE.
   * @author Kota Mizushima
   *
   */
  public static class Repetition extends Expression {
    public final Expression body;
    
    public Repetition(Expression body) {
      this.body = body;
    }
        
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "(" + body + "*)";
    }
  }
  
  /**
   * An alternation expression (A | B) of a RE.
   * @author Kota Mizushima
   *
   */
  public static class Alternation extends BinaryExpression {
    public Alternation(Expression lhs, Expression rhs) {
      super(lhs, rhs);
    }
    
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "(" + lhs + "|" + rhs + ")";
    }
  }
  
  /**
   * A sequence expression(A B) of a RE.
   * @author Kota Mizushima
   *
   */
  public static class Sequence extends BinaryExpression {
    public Sequence(Expression lhs, Expression rhs) {
      super(lhs, rhs);
    }
    
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return (lhs.toString() + rhs.toString());
    }
  }
  
  /**
   * A dot expression(.) of a RE.
   * @author Kota Mizushima
   *
   */  
  public static class All extends Expression {
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return ".";
    }
  }

  /**
   * An empty expression of a RE.
   * @author Kota Mizushima
   *
   */  
  public static class Empty extends Expression {
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "";
    }
  }
  
  /**
   * A character expression of a RE.
   * @author Kota Mizushima
   *
   */  
  public static class Char extends Expression {
    private final char chr;
    
    public Char(char chr) {
      this.chr = chr;
    }
    
    public char getChar() {
      return chr;
    }
    
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "" + chr;
    }
  }
  
  /**
   * A character set expression of a RE.
   * @author Kota Mizushima
   *
   */  
  public static class CharClass extends Expression {
    private final boolean not;
    private final Set<Character> chars;
    
    public CharClass(boolean not, Set<Character> chars) {
      this.not = not;
      this.chars = chars;
    }
    
    public boolean isNot() {
      return not;
    }
    
    public Set<Character> getChars() {
      return chars;
    }
    
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append('[');
      if(not) {
        buf.append('^');
      }
      for(char c:chars) {
        buf.append(c);
      }
      buf.append(']');
      return new String(buf);
    }
  }
  
  /**
   * This class represents an error.
   */
  public static class Error extends Expression {
    private Error() {}
    @Override
    public <R, C> R accept(Visitor<C, R> visitor, C context) {
      return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
      return "_|_";
    }
  }
  
  public static final Error ERROR = new Error();
  
  /**
   * A visitor to visit an AST of a RE.
   * @author Kota Mizushima
   *
   * @param <C>
   */
  public static abstract class Visitor<C, R> {
    protected R visit(All expression, C context){ return null; }
    protected R visit(Alternation expression, C context){ return null; }
    protected R visit(Char expression, C context){ return null; }
    protected R visit(CharClass expression, C context){ return null; }
    protected R visit(Empty expression, C context){ return null; }
    protected R visit(Repetition expression, C context){ return null; }
    protected R visit(Sequence expression, C context){ return null; }
    protected R visit(Error expression, C context) { return null; }
  }
}
