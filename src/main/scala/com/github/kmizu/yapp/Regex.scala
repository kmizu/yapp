/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import java.util.Set

/**
 * This class represents the namespace which have AST(Abstract
 * Syntax Tree) classes of RE(Regular Expression).  Basically,
 * this class doesn't have members except static classes or
 * static interfaces or constants.
 * @author Kota Mizushima
 *
 */
object Regex {
  final val ERROR: Regex.Error = new Regex.Error

  abstract class Expression {
    def accept(visitor: Regex.Visitor[C, R], context: C): R
  }

  abstract class BinaryExpression extends Expression {
    def this(lhs: Regex.Expression, rhs: Regex.Expression) {
      this()
      this.lhs = lhs
      this.rhs = rhs
    }

    final val lhs: Regex.Expression = null
    final val rhs: Regex.Expression = null
  }

  /**
   * An repetition expression (A*) of RE.
   * @author Kota Mizushima
   *
   */
  class Repetition extends Expression {
    def this(body: Regex.Expression) {
      this()
      this.body = body
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "(" + body + "*)"
    }

    final val body: Regex.Expression = null
  }

  /**
   * An alternation expression (A | B) of a RE.
   * @author Kota Mizushima
   *
   */
  class Alternation extends BinaryExpression {
    def this(lhs: Regex.Expression, rhs: Regex.Expression) {
      this()
      `super`(lhs, rhs)
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "(" + lhs + "|" + rhs + ")"
    }
  }

  /**
   * A sequence expression(A B) of a RE.
   * @author Kota Mizushima
   *
   */
  class Sequence extends BinaryExpression {
    def this(lhs: Regex.Expression, rhs: Regex.Expression) {
      this()
      `super`(lhs, rhs)
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return (lhs.toString + rhs.toString)
    }
  }

  /**
   * A dot expression(.) of a RE.
   * @author Kota Mizushima
   *
   */
  class All extends Expression {
    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "."
    }
  }

  /**
   * An empty expression of a RE.
   * @author Kota Mizushima
   *
   */
  class Empty extends Expression {
    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return ""
    }
  }

  /**
   * A character expression of a RE.
   * @author Kota Mizushima
   *
   */
  class Char extends Expression {
    def this(chr: Char) {
      this()
      this.chr = chr
    }

    def getChar: Char = {
      return chr
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "" + chr
    }

    private final val chr: Char = 0
  }

  /**
   * A character set expression of a RE.
   * @author Kota Mizushima
   *
   */
  class CharClass extends Expression {
    def this(not: Boolean, chars: Set[Character]) {
      this()
      this.not = not
      this.chars = chars
    }

    def isNot: Boolean = {
      return not
    }

    def getChars: Set[Character] = {
      return chars
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      buf.append('[')
      if (not) {
        buf.append('^')
      }
      import scala.collection.JavaConversions._
      for (c <- chars) {
        buf.append(c)
      }
      buf.append(']')
      return new String(buf)
    }

    private final val not: Boolean = false
    private final val chars: Set[Character] = null
  }

  /**
   * This class represents an error.
   */
  class Error extends Expression {
    private def this() {
      this()
    }

    def accept(visitor: Regex.Visitor[C, R], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "_|_"
    }
  }

  /**
   * A visitor to visit an AST of a RE.
   * @author Kota Mizushima
   *
   * @param <C>
   */
  abstract class Visitor {
    protected def visit(expression: Regex.All, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Alternation, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Char, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.CharClass, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Empty, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Repetition, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Sequence, context: C): R = {
      return null
    }

    protected def visit(expression: Regex.Error, context: C): R = {
      return null
    }
  }

}
