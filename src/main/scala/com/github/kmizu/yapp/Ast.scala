/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import java.util.Collections
import java.util.Iterator
import java.util.List

/**
 * This class holds node classes of AST of Yapp.
 * This class is used only for namespace.
 * @author Kota Mizushima
 */
object Ast {

  case class Action(override val pos: Position, body: Ast.Expression, code: String) extends Expression(pos) {

    override def toString: String =  body + " <[" + code + "]>"

  }

  case class BoundedExpression(body: Ast.Expression) extends Expression(body.pos) {

    override def toString: String = {
      "bounded{" + body + "}"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = visitor.visit(this, context)

  }

  class SetValueAction extends Expression {
    def this(pos: Position, body: Ast.Expression, code: String) {
      this()
      `super`(pos)
      this.body = body
      this.code = code
    }

    def body: Ast.Expression = {
      return body
    }

    def code: String = {
      return code
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return body + " %{" + code + "}"
    }

    private var body: Ast.Expression = null
    private var code: String = null
  }

  class N_Alternation extends VarArgExpression {
    def this(pos: Position, expressions: List[Ast.Expression]) {
      this()
      `super`(pos, expressions)
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      buf.append("(")
      buf.append(body.get(0).toString)
      import scala.collection.JavaConversions._
      for (e <- body.subList(1, body.size)) {
        buf.append(" / ")
        buf.append(e.toString)
      }
      buf.append(")")
      return new String(buf)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  abstract class BinaryExpression extends Expression {
    def this(pos: Position, lhs: Ast.Expression, rhs: Ast.Expression) {
      this()
      `super`(pos)
      this.lhs = lhs
      this.rhs = rhs
    }

    def lhs: Ast.Expression = {
      return lhs
    }

    def rhs: Ast.Expression = {
      return rhs
    }

    private var lhs: Ast.Expression = null
    private var rhs: Ast.Expression = null
  }

  class Cut extends Expression {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "^"
    }
  }

  class Fail extends Expression {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "fail"
    }
  }

  class Empty extends Terminal {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    override def toString: String = {
      return "()"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  abstract class Expression extends Node {
    def this(pos: Position) {
      this()
      `super`(pos)
    }
  }

  abstract class Terminal extends Expression {
    def this(pos: Position) {
      this()
      `super`(pos)
    }
  }

  class Grammar extends Node with Iterable[Ast.Rule] {
    def this(pos: Position, name: Symbol, macros: List[Ast.MacroDefinition], rules: List[Ast.Rule]) {
      this()
      `super`(pos)
      this.name = name
      this.macros = macros
      this.rules = rules
    }

    def iterator: Iterator[Ast.Rule] = {
      return rules.iterator
    }

    def name: Symbol = {
      return name
    }

    def setName(name: Symbol) {
      this.name = name
    }

    def macros: List[Ast.MacroDefinition] = {
      return macros
    }

    def getRules: List[Ast.Rule] = {
      return rules
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      buf.append("grammar " + name + ";")
      buf.append(SystemProperties.LINE_SEPARATOR)
      buf.append(SystemProperties.LINE_SEPARATOR)
      import scala.collection.JavaConversions._
      for (r <- rules) {
        buf.append(r)
        buf.append(SystemProperties.LINE_SEPARATOR)
        buf.append(SystemProperties.LINE_SEPARATOR)
      }
      return new String(buf)
    }

    private var name: Symbol = null
    private final val macros: List[Ast.MacroDefinition] = null
    private final val rules: List[Ast.Rule] = null
  }

  abstract class Node(val pos: Position)

  case class NonTerminal(override val pos: Position, name: Symbol, `var`: Symbol) extends Expression(pos) {

    def this(pos: Position, name: Symbol) {
      this(pos, name, null)
    }

    override def toString: String = {
      if (`var` != null) `var` + ":" + name.toString else name.toString
    }

  }

  case class MacroVariable(override val pos: Position, name: Symbol, `var`: Symbol) extends Expression(pos) {

    override def toString: String = {
      return if (`var` != null) `var` + ":" + name.toString else name.toString
    }

  }

  case class MacroCall (override val pos: Position, name: Symbol, params: List[Ast.Expression]) extends Expression(pos) {

    override def toString: String = {
      return name + "(" + params + ")"
    }

  }

  case class NotPredicate(override val pos: Position, body: Ast.Expression) extends Expression(pos) {

    override def toString: String = {
      return "!(" + body + ")"
    }

  }

  case class AndPredicate (override val pos: Position, expr: Ast.Expression) extends Expression(pos) {

    override def toString: String = {
      return "&(" + body + ")"
    }

  }

  class SemanticPredicate extends Expression {
    def this(pos: Position, expression: String) {
      this()
      `super`(pos)
      this.predicate = expression
    }

    def predicate: String = {
      return predicate
    }

    override def toString: String = {
      return "&{" + predicate + "}"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var predicate: String = null
  }

  class Repetition extends Expression {
    def this(pos: Position, expr: Ast.Expression) {
      this()
      `super`(pos)
      this.body = expr
    }

    def body: Ast.Expression = {
      return body
    }

    override def toString: String = {
      return "(" + body + ")*"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var body: Ast.Expression = null
  }

  class RepetitionPlus extends Expression {
    def this(pos: Position, body: Ast.Expression) {
      this()
      `super`(pos)
      this.body = body
    }

    def body: Ast.Expression = {
      return body
    }

    override def toString: String = {
      return "(" + body + ")+"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var body: Ast.Expression = null
  }

  class Optional extends Expression {
    def this(pos: Position, expr: Ast.Expression) {
      this()
      `super`(pos)
      this.body = expr
    }

    def body: Ast.Expression = {
      return body
    }

    override def toString: String = {
      return "(" + body + ")?"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var body: Ast.Expression = null
  }

  class MacroDefinition extends Node {
    def this(pos: Position, name: Symbol, formalParams: List[Symbol], body: Ast.Expression) {
      this()
      `super`(pos)
      this.name = name
      this.formalParams = formalParams
      this.body = body
    }

    def name: Symbol = {
      return name
    }

    def formalParams: List[Symbol] = {
      return formalParams
    }

    def body: Ast.Expression = {
      return body
    }

    override def toString: String = {
      val builder: StringBuilder = new StringBuilder
      if (formalParams.size > 0) {
        builder.append(formalParams.get(0))
        import scala.collection.JavaConversions._
        for (formalParam <- formalParams.subList(1, formalParams.size)) {
          builder.append(", " + formalParam)
        }
      }
      return String.format("macro %s(%s) = %s", name, new String(builder), body)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val name: Symbol = null
    private final val formalParams: List[Symbol] = null
    private final val body: Ast.Expression = null
  }

  object Rule {
    final val BOUNDED: Int = 1
  }

  class Rule extends Node {
    def this(pos: Position, flags: Int, name: Symbol, `type`: Symbol, expression: Ast.Expression, code: String) {
      this()
      `super`(pos)
      this.flags = flags
      this.name = name
      this.`type` = `type`
      this.expression = expression
      this.code = code
    }

    def flags: Int = {
      return flags
    }

    def name: Symbol = {
      return name
    }

    def `type`: Symbol = {
      return `type`
    }

    def body: Ast.Expression = {
      return expression
    }

    def code: String = {
      return code
    }

    override def toString: String = {
      return (if ((flags & BOUNDED) != 0) "bounded " else "") + name + " = " + expression + " ;"
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var flags: Int = 0
    private var name: Symbol = null
    private var `type`: Symbol = null
    private var expression: Ast.Expression = null
    private var code: String = null
  }

  class N_Sequence extends VarArgExpression {
    def this(pos: Position, expressions: List[Ast.Expression]) {
      this()
      `super`(pos, expressions)
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      buf.append("(")
      buf.append(body.get(0).toString)
      import scala.collection.JavaConversions._
      for (e <- body.subList(1, body.size)) {
        buf.append(" ")
        buf.append(e.toString)
      }
      buf.append(")")
      return new String(buf)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class StringLiteral extends Terminal {
    def this(pos: Position, value: String, `var`: Symbol) {
      this()
      `super`(pos)
      this.value = value
      this.`var` = `var`
    }

    def this(pos: Position, value: String) {
      this()
      `this`(pos, value, null)
    }

    def value: String = {
      return value
    }

    def `var`: Symbol = {
      return `var`
    }

    override def toString: String = {
      return "\"" + value + "\""
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var value: String = null
    private var `var`: Symbol = null
  }

  class Wildcard extends Terminal {
    def this(pos: Position, `var`: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
    }

    def `var`: Symbol = {
      return `var`
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    override def toString: String = {
      return "."
    }

    private final val `var`: Symbol = null
  }

  object CharClass {

    abstract class Element {
    }

    class Char extends Element {
      def this(value: Char) {
        this()
        this.value = value
      }

      final val value: Char = 0
    }

    class Range extends Element {
      def this(start: Char, end: Char) {
        this()
        this.start = start
        this.end = end
      }

      final val start: Char = 0
      final val end: Char = 0
    }

  }

  class CharClass extends Terminal {
    def this(pos: Position, positive: Boolean, elements: List[Ast.CharClass#Element]) {
      this()
      `this`(pos, positive, elements, null)
    }

    def this(pos: Position, positive: Boolean, elements: List[Ast.CharClass#Element], `var`: Symbol) {
      this()
      `super`(pos)
      this.positive = positive
      this.elements = Collections.unmodifiableList(elements)
      this.`var` = `var`
    }

    def `var`: Symbol = {
      return `var`
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      if (`var` != null) {
        buf.append(`var`)
        buf.append(':')
      }
      buf.append('[')
      if (!positive) {
        buf.append('^')
      }
      import scala.collection.JavaConversions._
      for (e <- elements) {
        if (e.isInstanceOf[Ast.CharClass#Char]) {
          (e.asInstanceOf[Ast.CharClass#Char]).value match {
            case '\t' =>
              buf.append("\\t")
              break //todo: break is not supported
            case '\f' =>
              buf.append("\\f")
              break //todo: break is not supported
            case '\r' =>
              buf.append("\\r")
              break //todo: break is not supported
            case '\n' =>
              buf.append("\\n")
              break //todo: break is not supported
            case _ =>
              buf.append((e.asInstanceOf[Ast.CharClass#Char]).value)
              break //todo: break is not supported
          }
        }
        else {
          buf.append((e.asInstanceOf[Ast.CharClass#Range]).start)
          buf.append('-')
          buf.append((e.asInstanceOf[Ast.CharClass#Range]).end)
        }
      }
      buf.append(']')
      return new String(buf)
    }

    def accept(visitor: Ast.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var `var`: Symbol = null
    final val positive: Boolean = false
    final val elements: List[Ast.CharClass#Element] = null
  }

  abstract class VarArgExpression extends Expression with Iterable[Ast.Expression] {
    def this(pos: Position, expressions: List[Ast.Expression]) {
      this()
      `super`(pos)
      this.body = expressions
    }

    def body: List[Ast.Expression] = {
      return body
    }

    def iterator: Iterator[Ast.Expression] = {
      return body.iterator
    }

    override def toString: String = {
      val buf: StringBuffer = new StringBuffer
      buf.append(body.get(0).toString)
      import scala.collection.JavaConversions._
      for (e <- body.subList(1, body.size)) {
        buf.append(" | ")
        buf.append(e.toString)
      }
      return new String(buf)
    }

    protected var body: List[Ast.Expression] = null
  }

}