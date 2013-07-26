/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import com.github.kmizu.yapp.util.SystemProperties
import java.util.Collections
import java.util.Iterator
import java.util.List
import scala.beans.BeanProperty

/**
 * This class holds node classes of AST of Yapp.
 * This class is used only for namespace.
 * @author Kota Mizushima
 */
object Ast {

  case class Action(override val pos: Position, body: Ast.Expression, code: String) extends Expression {
    override def toString: String =  body + " <[" + code + "]>"

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class BoundedExpression(body: Ast.Expression) extends Expression {
    val pos: Position = body.pos

    override def toString: String = {
      "bounded{" + body + "}"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class SetValueAction(pos: Position, body: Ast.Expression, code: String) extends Expression {
    override def toString: String = {
      return body + " %{" + code + "}"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class N_Alternation(pos: Position, body: List[Ast.Expression])  extends VarArgExpression {
    override def toString: String = {
      val buf = new StringBuffer
      buf.append("(")
      buf.append(body.get(0).toString)
      import scala.collection.JavaConversions._
      for (e <- body.subList(1, body.size)) {
        buf.append(" / ")
        buf.append(e.toString)
      }
      buf.append(")")

      new String(buf)
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  abstract class BinaryExpression extends Expression {
    def lhs: Ast.Expression
    def rhs: Ast.Expression
  }

  case class Cut(pos: Position) extends Expression {
    override def toString: String = {
      return "^"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class Fail(pos: Position) extends Expression {
    override def toString: String = {
      return "fail"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class Empty(pos: Position) extends Terminal {
    override def toString: String = {
      return "()"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  abstract class Expression extends Node

  abstract class Terminal extends Expression

  case class Grammar(pos: Position, @BeanProperty var name: Symbol, macros: List[Ast.MacroDefinition], rules: List[Ast.Rule]) extends Node with java.lang.Iterable[Ast.Rule] {
    def iterator: Iterator[Ast.Rule] = rules.iterator

    def setName(name: Symbol): Unit = {
      this.name = name
    }

    def getRules: List[Ast.Rule] = rules

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
      new String(buf)
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  abstract class Node {
    def pos: Position
    def accept[E, T](visitor: Visitor[E, T], context: T): E
  }

  case class NonTerminal(pos: Position, name: Symbol, `var`: Symbol) extends Expression {
    def this(pos: Position, name: Symbol) {
      this(pos, name, null)
    }

    override def toString: String = {
      if (`var` != null) `var` + ":" + name.toString else name.toString
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class MacroVariable(pos: Position, name: Symbol, `var`: Symbol) extends Expression {
    override def toString: String = {
      if (`var` != null) `var` + ":" + name.toString else name.toString
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class MacroCall(pos: Position, name: Symbol, params: List[Ast.Expression]) extends Expression {
    override def toString: String = {
      return name + "(" + params + ")"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class NotPredicate(pos: Position, body: Ast.Expression) extends Expression {
    override def toString: String = {
      return "!(" + body + ")"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class AndPredicate (pos: Position, body: Ast.Expression) extends Expression {
    override def toString: String = {
      return "&(" + body + ")"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class SemanticPredicate(pos: Position, predicate: String) extends Expression {
    override def toString: String = {
      return "&{" + predicate + "}"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class Repetition(pos: Position, body: Ast.Expression) extends Expression {
    override def toString: String = {
      return "(" + body + ")*"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class RepetitionPlus(pos: Position, body: Ast.Expression) extends Expression {
    override def toString: String = {
      return "(" + body + ")+"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class Optional(pos: Position, body: Ast.Expression) extends Expression {
    override def toString: String = {
      return "(" + body + ")?"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class MacroDefinition(pos: Position, name: Symbol, formalParams: List[Symbol], body: Ast.Expression) extends Node {
    override def toString: String = {
      val builder = new StringBuilder

      if (formalParams.size > 0) {
        builder.append(formalParams.get(0))
        import scala.collection.JavaConversions._
        for (formalParam <- formalParams.subList(1, formalParams.size)) {
          builder.append(", " + formalParam)
        }
      }

      String.format("macro %s(%s) = %s", name, builder.toString(), body)
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  object Rule {
    final val BOUNDED: Int = 1
  }

  case class Rule(pos: Position, flags: Int, name: Symbol, `type`: Symbol, body: Ast.Expression, code: String) extends Node {
    override def toString: String = {
      return (if ((flags & Rule.BOUNDED) != 0) "bounded " else "") + name + " = " + body+ " ;"
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class N_Sequence(pos: Position, body: List[Ast.Expression]) extends VarArgExpression {
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
      new String(buf)
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class StringLiteral(pos: Position, value: String, `var`: Symbol) extends Terminal {
    def this(pos: Position, value: String) {
      this(pos, value, null)
    }

    override def toString: String = {
      return "\"" + value + "\""
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class Wildcard(pos: Position, `var`: Symbol) extends Terminal {
    override def toString: String = {
      return "."
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  abstract class CharClassNode
  case class CharacterElement(value: Char) extends CharClassNode {
    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }
  case class Range(start: Char, end: Char) extends CharClassNode {
    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  case class CharClass(pos: Position, positive: Boolean, elements: List[Ast.CharClassNode], `var`: Symbol) extends Terminal {
    def this(pos: Position, positive: Boolean, elements: List[Ast.CharClassNode]) {
      this(pos, positive, Collections.unmodifiableList(elements), null)
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
        if (e.isInstanceOf[Ast.CharacterElement]) {
          (e.asInstanceOf[Ast.CharacterElement]).value match {
            case '\t' =>
              buf.append("\\t")
            case '\f' =>
              buf.append("\\f")
            case '\r' =>
              buf.append("\\r")
            case '\n' =>
              buf.append("\\n")
            case _ =>
              buf.append((e.asInstanceOf[Ast.CharacterElement]).value)
          }
        }
        else {
          buf.append((e.asInstanceOf[Ast.Range]).start)
          buf.append('-')
          buf.append((e.asInstanceOf[Ast.Range]).end)
        }
      }
      buf.append(']')
      new String(buf)
    }

    def accept[E, T](visitor: Visitor[E, T], context: T): E = visitor.visit(this, context)
  }

  abstract class VarArgExpression extends Expression with Iterable[Ast.Expression] {
    def body: List[Ast.Expression]

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
  }

  abstract class Visitor[E >: Null, T] {
    // Definitions
    def visit(node: Grammar, context: T): E = null
    def visit(node: MacroDefinition, context: T): E = null
    def visit(node: Rule, context: T): E = null

    // Expressions
    def visit(node: Action, context: T): E = null
    def visit(node: BoundedExpression, context: T): E = null
    def visit(node: SetValueAction, context: T): E = null
    def visit(node: N_Alternation, context: T): E = null
    def visit(node: N_Sequence, context: T): E = null
    def visit(node: NonTerminal, context: T): E = null
    def visit(node: MacroVariable, context: T): E = null
    def visit(node: MacroCall, context: T): E = null
    def visit(node: AndPredicate, context: T): E = null
    def visit(node: NotPredicate, context: T): E = null
    def visit(node: SemanticPredicate, context: T): E = null
    def visit(node: Repetition, context: T): E = null
    def visit(node: RepetitionPlus, context: T): E = null
    def visit(node: Optional, context: T): E = null
    def visit(node: StringLiteral, context: T): E = null
    def visit(node: Wildcard, context: T): E = null
    def visit(node:CharClass, context: T): E = null
    def visit(node: Cut, context: T): E = null
    def visit(node: Fail, context: T): E = null
    def visit(node: Empty, context: T): E = null

    // Nodes in character classes
    def visit(node: CharacterElement, context: T): E = null
    def visit(node: Range, context: T): E = null
  }
}
