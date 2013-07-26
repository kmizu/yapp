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
    def this(pos: Position) {
      this()
      this.pos = pos
    }

    def pos: Position = {
      return pos
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R

    private final val pos: Position = null
  }

  class ParserUnit extends Node with Iterable[Ist.Function] {
    def this(pos: Position, name: Symbol, nameToCharSet: Map[Symbol, Set[Character]], startName: Symbol, startType: Symbol, rules: List[Ist.Function]) {
      this()
      `super`(pos)
      this.name = name
      this.nameToCharSet = nameToCharSet
      this.startName = startName
      this.startType = startType
      this.rules = rules
    }

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

    private final val name: Symbol = null
    private final val startName: Symbol = null
    private final val startType: Symbol = null
    private final val rules: List[Ist.Function] = null
    private final val nameToCharSet: Map[Symbol, Set[Character]] = null
  }

  class Function extends Node {
    def this(pos: Position, name: Symbol, `type`: Symbol, code: String, memoized: Boolean, statement: Ist.Statement) {
      this()
      `super`(pos)
      this.name = name
      this.`type` = `type`
      this.code = code
      this.memoized = memoized
      this.statement = statement
    }

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

    private final val name: Symbol = null
    private final val `type`: Symbol = null
    private final val code: String = null
    private final val memoized: Boolean = false
    private final val statement: Ist.Statement = null
  }

  abstract class Statement extends Node {
    def this(pos: Position) {
      this()
      `super`(pos)
    }
  }

  class ActionStatement extends Statement {
    def this(pos: Position, code: String) {
      this()
      `super`(pos)
      this.code = code
    }

    def getCode: String = {
      return code
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var code: String = null
  }

  class SetSemanticValue extends Statement {
    def this(pos: Position, code: String) {
      this()
      `super`(pos)
      this.code = code
    }

    def getCode: String = {
      return code
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var code: String = null
  }

  class Block extends Statement {
    def this(pos: Position, label: Symbol, statements: Ist.Statement*) {
      this()
      `super`(pos)
      this.label = label
      this.statements = statements
    }

    def this(pos: Position, label: Symbol, statements: List[Ist.Statement]) {
      this()
      `super`(pos)
      this.label = label
      this.statements = statements.toArray(new Array[Ist.Statement](0))
    }

    def getLabel: Symbol = {
      return label
    }

    def getStatements: Array[Ist.Statement] = {
      return statements
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val label: Symbol = null
    private final val statements: Array[Ist.Statement] = null
  }

  class EscapeFrom extends Statement {
    def this(pos: Position, label: Symbol) {
      this()
      `super`(pos)
      this.label = label
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val label: Symbol = null
  }

  class Fail extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class MatchString extends Statement {
    def this(pos: Position, `var`: Ist.Var, value: String, label: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
      this.value = value
      this.label = label
    }

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

    private final val `var`: Ist.Var = null
    private final val value: String = null
    private final val label: Symbol = null
  }

  class Accept extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    override def toString: String = {
      return "accept;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class GenerateSuccess extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    override def toString: String = {
      return "generate_success;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class GenerateFailure extends Statement {
    def this(pos: Position, message: String) {
      this()
      `super`(pos)
      this.expected = message
    }

    def expected: String = {
      return expected
    }

    override def toString: String = {
      return "generate_failure;"
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private var expected: String = null
  }

  class MatchAny extends Statement {
    def this(pos: Position, `var`: Ist.Var, label: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
      this.label = label
    }

    def getVar: Ist.Var = {
      return `var`
    }

    def getLabel: Symbol = {
      return label
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val `var`: Ist.Var = null
    private final val label: Symbol = null
  }

  class MatchCharClass extends Statement {
    def this(pos: Position, name: Symbol, `var`: Ist.Var, positive: Boolean, label: Symbol) {
      this()
      `super`(pos)
      this.name = name
      this.`var` = `var`
      this.positive = positive
      this.label = label
    }

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

    private final val name: Symbol = null
    private final val `var`: Ist.Var = null
    private final val positive: Boolean = false
    private final val label: Symbol = null
  }

  class NewCursorVar extends Statement {
    def this(pos: Position, name: Symbol) {
      this()
      `super`(pos)
      this.name = name
    }

    def getName: Symbol = {
      return name
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val name: Symbol = null
  }

  class Nop extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class Var {
    def this(name: Symbol, `type`: Symbol) {
      this()
      this.name = name
      this.`type` = `type`
    }

    def getName: Symbol = {
      return name
    }

    def getType: Symbol = {
      return `type`
    }

    private final val name: Symbol = null
    private final val `type`: Symbol = null
  }

  class MatchRule extends Statement {
    def this(pos: Position, `var`: Ist.Var, rule: Symbol, label: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
      this.rule = rule
      this.label = label
    }

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

    private final val `var`: Ist.Var = null
    private final val rule: Symbol = null
    private final val label: Symbol = null
  }

  class BackupCursor extends Statement {
    def this(pos: Position, `var`: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
    }

    def getVar: Symbol = {
      return `var`
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val `var`: Symbol = null
  }

  class IncrDepth extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class DecrDepth extends Statement {
    def this(pos: Position) {
      this()
      `super`(pos)
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }
  }

  class RewindCursor extends Statement {
    def this(pos: Position, `var`: Symbol) {
      this()
      `super`(pos)
      this.`var` = `var`
    }

    def getVar: Symbol = {
      return `var`
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val `var`: Symbol = null
  }

  class Loop extends Statement {
    def this(pos: Position, label: Symbol, statements: Ist.Statement*) {
      this()
      `super`(pos)
      this.label = label
      this.statements = statements
    }

    def getLabel: Symbol = {
      return label
    }

    def getStatements: Array[Ist.Statement] = {
      return statements
    }

    def accept[R, C](visitor: Ist.Visitor[R, C], context: C): R = {
      return visitor.visit(this, context)
    }

    private final val label: Symbol = null
    private final val statements: Array[Ist.Statement] = null
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
