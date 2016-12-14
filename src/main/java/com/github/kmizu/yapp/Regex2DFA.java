package com.github.kmizu.yapp;

import com.github.kmizu.yapp.Automata.NFA;
import com.github.kmizu.yapp.Regex.All;
import com.github.kmizu.yapp.Regex.Alternation;
import com.github.kmizu.yapp.Regex.Char;
import com.github.kmizu.yapp.Regex.CharClass;
import com.github.kmizu.yapp.Regex.Empty;
import com.github.kmizu.yapp.Regex.Repetition;
import com.github.kmizu.yapp.Regex.Sequence;

public class Regex2DFA extends Regex.Visitor<NFA, Pair<Integer, Integer>> {
  public static final Regex2DFA INSTANCE = new Regex2DFA();
  private Regex2DFA() {
  }
  
  public Automata.DFA compile(Regex.Expression expression) {
    if(expression == Regex.ERROR) return Automata.DFA.ERROR;
    NFA nfa = compileToNfa(expression);
    return Automata.fromNFA2DFA(nfa);
  }
  
  private NFA compileToNfa(Regex.Expression expression) {
    NFA nfa = new NFA();
    Pair<Integer, Integer> result = expression.accept(this, nfa);
    nfa.startNum = result.fst();
    nfa.finalNum = result.snd();
    return nfa;
  }

  @Override
  protected Pair<Integer, Integer> visit(All expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    for(int i = 0; i < Automata.NUM_ALPHABETS; i++) {
      context.addTransition(startNum, (char)i, finalNum);
    }
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(Alternation expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    Pair<Integer, Integer> resultL = expression.lhs.accept(this, context);
    Pair<Integer, Integer> resultR = expression.rhs.accept(this, context);
    context.addEpsilon(startNum, resultL.fst());
    context.addEpsilon(startNum, resultR.fst());
    context.addEpsilon(resultL.snd(), finalNum);
    context.addEpsilon(resultR.snd(), finalNum);
    
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(Char expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    context.addTransition(startNum, expression.getChar(), finalNum);
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(CharClass expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    for(int i = 0; i < Automata.NUM_ALPHABETS; i++) {
      if(expression.isNot() == expression.getChars().contains((char)i)) continue;
      context.addTransition(startNum, (char)i, finalNum);
    }
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(Empty expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    context.addEpsilon(startNum, finalNum);
    
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(Repetition expression, NFA context) {
    int startNum = context.addState();
    int finalNum = context.addState();
    Pair<Integer, Integer> result = expression.body.accept(this, context);
    context.addEpsilon(startNum, result.fst());
    context.addEpsilon(startNum, finalNum);
    context.addEpsilon(result.snd(), result.fst());
    context.addEpsilon(result.snd(), finalNum);
    
    return Pair.make(startNum, finalNum);
  }

  @Override
  protected Pair<Integer, Integer> visit(Sequence expression, NFA context) {
    Pair<Integer, Integer> resultL = expression.lhs.accept(this, context);
    Pair<Integer, Integer> resultR = expression.rhs.accept(this, context);
    context.addEpsilon(resultL.snd(), resultR.fst());
    
    return Pair.make(resultL.fst(), resultR.snd());
  }
}
