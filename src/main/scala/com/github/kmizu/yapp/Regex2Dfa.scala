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

class Regex2Dfa private() extends Regex.Visitor[Automata.Nfa, Pair[Integer, Integer]] {

  def compile(expression: Regex.Expression): Automata.Dfa = {
    if (expression eq Regex.ERROR) {
      Automata.Dfa.ERROR
    } else {
      Automata.fromNfa2Dfa(compileToNfa(expression))
    }
  }

  private def compileToNfa(expression: Regex.Expression): Automata.Nfa = {
    val nfa = new Automata.Nfa
    val result = expression.accept(this, nfa)
    nfa.startNum = result.fst
    nfa.finalNum = result.snd
    nfa
  }

  protected override def visit(expression: Regex.All, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum = context.addState

    for(i <- 0 until Automata.NUM_ALPHABETS) {
      context.addTransition(startNum, i.asInstanceOf[scala.Char], finalNum)
    }
    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Alternation, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum = context.addState
    val resultL = expression.lhs.accept(this, context)
    val resultR = expression.rhs.accept(this, context)

    context.addEpsilon(startNum, resultL.fst)
    context.addEpsilon(startNum, resultR.fst)
    context.addEpsilon(resultL.snd, finalNum)
    context.addEpsilon(resultR.snd, finalNum)
    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Char, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum= context.addState

    context.addTransition(startNum, expression.getChar, finalNum)
    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.CharClass, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum = context.addState

    for(i <- 0 until Automata.NUM_ALPHABETS) {
      if (expression.isNot != expression.getChars.contains(i.asInstanceOf[scala.Char])) {
        context.addTransition(startNum, i.asInstanceOf[scala.Char], finalNum)
      }
    }

    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Empty, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum = context.addState

    context.addEpsilon(startNum, finalNum)
    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Repetition, context: Automata.Nfa): Pair[Integer, Integer] = {
    val startNum = context.addState
    val finalNum = context.addState
    val result = expression.body.accept(this, context)

    context.addEpsilon(startNum, result.fst)
    context.addEpsilon(startNum, finalNum)
    context.addEpsilon(result.snd, result.fst)
    context.addEpsilon(result.snd, finalNum)
    Pair.make(startNum, finalNum)
  }

  protected override def visit(expression: Regex.Sequence, context: Automata.Nfa): Pair[Integer, Integer] = {
    val resultL = expression.lhs.accept(this, context)
    val resultR = expression.rhs.accept(this, context)
    context.addEpsilon(resultL.snd, resultR.fst)
    Pair.make(resultL.fst, resultR.snd)
  }
}
