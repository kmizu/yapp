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
    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R
  }

  abstract class BinaryExpression extends Expression {
    val lhs: Regex.Expression
    val rhs: Regex.Expression
  }

  /**
   * An repetition expression (A*) of RE.
   * @author Kota Mizushima
   *
   */
  case class Repetition(body: Regex.Expression) extends Expression {

    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String = "(" + body + "*)"
  }

  /**
   * An alternation expression (A | B) of a RE.
   * @author Kota Mizushima
   *
   */
  case class Alternation(lhs: Regex.Expression, rhs: Regex.Expression) extends BinaryExpression {
    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String =  "(" + lhs + "|" + rhs + ")"
  }

  /**
   * A sequence expression(A B) of a RE.
   * @author Kota Mizushima
   *
   */
  case class Sequence(lhs: Regex.Expression, rhs: Regex.Expression) extends BinaryExpression {

    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String = (lhs.toString + rhs.toString)
  }

  /**
   * A dot expression(.) of a RE.
   * @author Kota Mizushima
   *
   */
  class All extends Expression {
    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String = "."
  }

  /**
   * An empty expression of a RE.
   * @author Kota Mizushima
   *
   */
  class Empty extends Expression {
    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String = ""
  }

  /**
   * A character expression of a RE.
   * @author Kota Mizushima
   *
   */
  case class Char(chr: scala.Char) extends Expression {

    def getChar: scala.Char = chr

    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String =  "" + chr

  }

  /**
   * A character set expression of a RE.
   * @author Kota Mizushima
   *
   */
  case class CharClass(not: Boolean, chars: Set[Character]) extends Expression {
    def isNot: Boolean = not

    def getChars: Set[Character] = chars

    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String = {
      val buf = new StringBuffer
      buf.append('[')
      if (not) {
        buf.append('^')
      }
      import scala.collection.JavaConversions._
      for (c <- chars) {
        buf.append(c)
      }
      buf.append(']')
      new String(buf)
    }
  }

  /**
   * This class represents an error.
   */
  class Error extends Expression {
    private def this() {
      this()
    }

    def accept[C, R](visitor: Regex.Visitor[C, R], context: C): R = visitor.visit(this, context)

    override def toString: String =  "_|_"
  }

  /**
   * A visitor to visit an AST of a RE.
   * @author Kota Mizushima
   *
   * @tparam C context type of Visitor
   * @tparam R argument type of Visitor's visit methods.
   */
  abstract class Visitor[C, R >: Null] {
    protected[yapp] def visit(expression: Regex.All, context: C): R = null

    protected[yapp] def visit(expression: Regex.Alternation, context: C): R = null

    protected[yapp] def visit(expression: Regex.Char, context: C): R = null

    protected[yapp] def visit(expression: Regex.CharClass, context: C): R = null

    protected[yapp] def visit(expression: Regex.Empty, context: C): R = null

    protected[yapp] def visit(expression: Regex.Repetition, context: C): R = null

    protected[yapp] def visit(expression: Regex.Sequence, context: C): R = null

    protected[yapp] def visit(expression: Regex.Error, context: C): R = null
  }

}
