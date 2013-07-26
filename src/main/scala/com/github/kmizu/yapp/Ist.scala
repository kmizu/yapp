/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import com.github.kmizu.yapp.util.SystemProperties.LINE_SEPARATOR
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set

/**
 * Represents Intermediate Syntax Tree(Ist).
 * Basically, this class has no member except class or interface.
 * @author Kota Mizushima
 *
 */
object Ist {

  abstract class Node {
    def pos: Position

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R
  }

  case class ParserUnit (pos: Position, name: Symbol, nameToCharSet: Map[Symbol, Set[Character]], startName: Symbol, startType: Symbol, rules: List[Ist.Function]) extends Node with Iterable[Ist.Function] {
    def getName: Symbol = {
      return name
    }

    def getNameToCharSet: Map[Symbol, Set[Character]] = {
      return nameToCharSet
    }

    def getStartName: Symbol = {
      return startName
    }

    def getStartType: Symbol = {
      return startType
    }

    def getRules: List[Ist.Function] = {
      return rules
    }

    def iterator: Iterator[Ist.Function] = {
      return rules.iterator
    }

    override def toString: String = {
      val builder: StringBuilder = new StringBuilder
      builder.append("parser ")
      builder.append(name)
      builder.append(LINE_SEPARATOR)
      builder.append(LINE_SEPARATOR)
      import scala.collection.JavaConversions._
      for (rule <- rules) {
        builder.append(rule)
        builder.append(LINE_SEPARATOR)
        builder.append(LINE_SEPARATOR)
      }
      return new String(builder.toString())
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Function(pos: Position, name: Symbol, `type`: Symbol, code: String, memoized: Boolean, statement: Ist.Statement) extends Node {
    def getName: Symbol = {
      return name
    }

    def getType: Symbol = {
      return `type`
    }

    def getCode: String = {
      return code
    }

    def isMemoized: Boolean = {
      return memoized
    }

    def getStatement: Ist.Statement = {
      return statement
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  abstract class Statement extends Node

  case class ActionStatement(pos: Position, code: String) extends Statement {

    def getCode: String = {
      return code
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class SetSemanticValue(pos: Position, code: String) extends Statement {
    def getCode: String = {
      return code
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Block(pos: Position, label: Symbol, statements: Ist.Statement*) extends Statement {
    def this(pos: Position, label: Symbol, statements: List[Ist.Statement]) {
      this(pos, label, statements:_*)
    }

    def getLabel: Symbol = {
      return label
    }

    def getStatements: Array[Ist.Statement] = {
      return statements.toArray
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class EscapeFrom(pos: Position, label: Symbol) extends Statement {
    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Fail(pos: Position) extends Statement {
    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class MatchString(pos: Position, `var`: Ist.Var, value: String, label: Symbol) extends Statement {
    def getVar: Ist.Var = {
      return `var`
    }

    def getValue: String = {
      return value
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Accept(pos: Position) extends Statement {
    override def toString: String = {
      return "accept;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class GenerateSuccess(pos: Position) extends Statement {
    override def toString: String = {
      return "generate_success;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class GenerateFailure(pos: Position, message: String) extends Statement {
    def expected: String = {
      return message
    }

    override def toString: String = {
      return "generate_failure;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class MatchAny(pos: Position, `var`: Ist.Var, label: Symbol) extends Statement {
    def getVar: Ist.Var = {
      return `var`
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class MatchCharClass(pos: Position, name: Symbol, `var`: Ist.Var, positive: Boolean, label: Symbol) extends Statement {
    def getName: Symbol = {
      return name
    }

    def getVar: Ist.Var = {
      return `var`
    }

    def isPositive: Boolean = {
      return positive
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class NewCursorVar(pos: Position, name: Symbol) extends Statement {
    def getName: Symbol = {
      return name
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Nop(pos: Position) extends Statement {
    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Var(name: Symbol, `type`: Symbol) {
    def getName: Symbol = {
      return name
    }

    def getType: Symbol = {
      return `type`
    }
  }

  case class MatchRule(pos: Position, `var`: Ist.Var, rule: Symbol, label: Symbol) extends Statement {
    def getVar: Ist.Var = {
      return `var`
    }

    def getRule: Symbol = {
      return rule
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class BackupCursor(pos: Position, `var`: Symbol) extends Statement {
    def getVar: Symbol = {
      return `var`
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class IncrDepth(pos: Position) extends Statement {
    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class DecrDepth(pos: Position) extends Statement {
    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class RewindCursor(pos: Position, `var`: Symbol) extends Statement {
    def getVar: Symbol = {
      return `var`
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  case class Loop(pos: Position, label: Symbol, statements: Ist.Statement*) extends Statement {
    def getLabel: Symbol = {
      return label
    }

    def getStatements: Array[Ist.Statement] = {
      return statements.toArray
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  abstract class Visitor[R >: Null, C] {
    def visit(node: Ist.Accept, context: C): R = null

    def visit(node: Ist.ActionStatement, context: C): R = null

    def visit(node: Ist.BackupCursor, context: C): R = null

    def visit(node: Ist.Block, context: C): R = null

    def visit(node: Ist.DecrDepth, context: C): R = null

    def visit(node: Ist.EscapeFrom, context: C): R = null

    def visit(node: Ist.Fail, context: C): R = null

    def visit(node: Ist.Function, context: C): R = null

    def visit(node: Ist.GenerateSuccess, context: C): R = null

    def visit(node: Ist.GenerateFailure, context: C): R = null

    def visit(node: Ist.IncrDepth, context: C): R = null

    def visit(node: Ist.Loop, context: C): R = null

    def visit(node: Ist.MatchAny, context: C): R = null

    def visit(node: Ist.MatchCharClass, context: C): R = null

    def visit(node: Ist.MatchRule, context: C): R = null

    def visit(node: Ist.MatchString, context: C): R = null

    def visit(node: Ist.NewCursorVar, context: C): R = null

    def visit(node: Ist.Nop, context: C): R = null

    def visit(node: Ist.ParserUnit, context: C): R = null

    def visit(node: Ist.RewindCursor, context: C): R = null

    def visit(node: Ist.SetSemanticValue, context: C): R = null
  }
}
