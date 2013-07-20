package com.github.kmizu.yapp

import com.github.kmizu.yapp.Automata.Nfa
import com.github.kmizu.yapp.Regex.All
import com.github.kmizu.yapp.Regex.Alternation
import com.github.kmizu.yapp.Regex.Char
import com.github.kmizu.yapp.Regex.CharClass
import com.github.kmizu.yapp.Regex.Empty
import com.github.kmizu.yapp.Regex.Repetition
import com.github.kmizu.yapp.Regex.Sequence

object Regex2Dfa {
  final val INSTANCE: Regex2Dfa = new Regex2Dfa
}

class Regex2Dfa extends Regex.Visitor[Automata.Nfa, Pair[Integer, Integer]] {
  private def this() {
    this()
  }

  def compile(expression: Regex.Expression): Automata.Dfa = {
    if (expression eq Regex.ERROR) return Automata.Dfa.ERROR
    val nfa: Automata.Nfa = compileToNfa(expression)
    return Automata.fromNfa2Dfa(nfa)
  }

  private def compileToNfa(expression: Regex.Expression): Automata.Nfa = {
    val nfa: Automata.Nfa = new Automata.Nfa
    val result: Pair[Integer, Integer] = expression.accept(this, nfa)
    nfa.startNum = result.fst
    nfa.finalNum = result.snd
    return nfa
  }

  protected override def visit(expression: Regex.All, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    {
      var i: Int = 0
      while (i < Automata.NUM_ALPHABETS) {
        {
          context.addTransition(startNum, i.asInstanceOf[Char], finalNum)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Alternation, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    val resultL: Pair[Integer, Integer] = expression.lhs.accept(this, context)
    val resultR: Pair[Integer, Integer] = expression.rhs.accept(this, context)
    context.addEpsilon(startNum, resultL.fst)
    context.addEpsilon(startNum, resultR.fst)
    context.addEpsilon(resultL.snd, finalNum)
    context.addEpsilon(resultR.snd, finalNum)
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Char, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    context.addTransition(startNum, expression.getChar, finalNum)
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.CharClass, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    {
      var i: Int = 0
      while (i < Automata.NUM_ALPHABETS) {
        {
          if (expression.isNot == expression.getChars.contains(i.asInstanceOf[Char])) continue //todo: continue is not supported
          context.addTransition(startNum, i.asInstanceOf[Char], finalNum)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Empty, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    context.addEpsilon(startNum, finalNum)
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Repetition, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum: Int = context.addState
    val finalNum: Int = context.addState
    val result: Pair[Integer, Integer] = expression.body.accept(this, context)
    context.addEpsilon(startNum, result.fst)
    context.addEpsilon(startNum, finalNum)
    context.addEpsilon(result.snd, result.fst)
    context.addEpsilon(result.snd, finalNum)
    return Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Sequence, context: Automata.Nfa): Pair[Integer, Integer] = {
    val resultL: Pair[Integer, Integer] = expression.lhs.accept(this, context)
    val resultR: Pair[Integer, Integer] = expression.rhs.accept(this, context)
    context.addEpsilon(resultL.snd, resultR.fst)
    return Pair.make(resultL.fst, resultR.snd)
  }
}
