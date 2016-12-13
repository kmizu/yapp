/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.kmizu.yapp.util.CollectionUtil.*;

/**
 * Represents the Automata(Nfa and DFA) namespace.
 * This class has no member except static class or interface or constant.
 * @author Kota Mizushima
 *
 */
public class Automata {
  public static final int NUM_ALPHABETS = Character.MAX_VALUE + 1;
  public static class Nfa {
    public final List<NfaState> states = list();
    public int startNum = -1;
    public int finalNum = -1;
    
    public int addState() {
      NfaState state = new NfaState();
      states.add(state);
      return states.size() - 1;
    }
    
    public void addTransition(int current, char input, int next) {
      states.get(current).addTransition(input, next);
    }
    
    public void addEpsilon(int current, int next) {
      states.get(current).addEpsilon(next);
    }
    
    public Set<Integer> eclosure(int stateNum) {
      Set<Integer> result = set(stateNum);
      while(true) {
        Set<Integer> copy = setFrom(result);
        for(int next : copy) {
          NfaState state = states.get(next);
          result.addAll(state.etrans);
        }
        if(result.size() == copy.size()) break;
      }
      return result;
    }
    
    public Set<Integer> eclosure(Set<Integer> stateNums) {
      Set<Integer> result = set();
      for(int stateNum : stateNums) result.addAll(eclosure(stateNum));
      return result;
    }
    
    public Set<Integer> move(Set<Integer> stateNums, char input) {
      Set<Integer> result = set();
      for(int stateNum : stateNums) {
        Set<Integer> nexts = states.get(stateNum).strans.get(input);
        if(nexts != null) result.addAll(nexts);
      }
      return result;
    }    
  }
  
  public static class NfaState {
    public final Set<Integer> etrans = set();
    public final Map<Character, Set<Integer>> strans = map();
    
    public void addTransition(char input, int next) {
      Set<Integer> set = strans.get(input);
      if(set == null) {
        set = set();
        strans.put(input, set);
      }
      set.add(next);
    }
    
    public void addEpsilon(int next) {
      etrans.add(next);
    }
  }
  
  public static class Dfa {
    public static final Dfa ERROR = new Dfa(null, -1, null);
    public final int[][] table;
    public final int start;
    public final Set<Integer> finals;
    public Dfa(int[][] table, int start, Set<Integer> finals){
      this.table = table;
      this.start = start;
      this.finals = finals;
    }
    
    public Dfa and(Dfa rhs) {
      int[][] newTable = new int[table.length * rhs.table.length][NUM_ALPHABETS];
      int newStart = rhs.table.length * start + rhs.start;
      Set<Integer> newFinals = set();
      for(int a : finals) {
        for(int b : rhs.finals) {
          newFinals.add(rhs.table.length * a + b);
        }
      }
      for(int a = 0; a < table.length; a++) {
        for(int b = 0; b < rhs.table.length; b++) {
          for(int input = 0; input < NUM_ALPHABETS; input++) {
            int nextA = table[a][input];
            int nextB = rhs.table[b][input];
            if(nextA == -1 || nextB == -1) {
              newTable[rhs.table.length * a + b][input] = -1;
            }else {
              newTable[rhs.table.length * a + b][input] = rhs.table.length * nextA + nextB;
            }
          }
        }
      }
      return new Dfa(newTable, newStart, newFinals);
    }
          
    public boolean disjoint(Dfa dfa) {
      return this.and(dfa).isEmpty();
    }
    
    public boolean isEmpty() {
      Set<Integer> reachable = set();
      mark(reachable, start);
      reachable.retainAll(finals);
      return reachable.isEmpty();      
    }
    
    public String toString() {
      int maxDigit = String.valueOf(table.length).length();
      if(maxDigit % 2 == 0) maxDigit++;
      final int ASCII_PRINTABLE_START = 32;
      final int ASCII_PRINTABLE_FINAL = 126;
      StringWriter buff = new StringWriter();
      PrintWriter w = new PrintWriter(buff);
      w.printf("start: %d%n", start);
      w.printf("final: ");
      for(int f:finals) {
        w.printf("%0" + maxDigit + "d ", f);
      }
      w.println();
      spacing(w, maxDigit + 1);
      for(int i = ASCII_PRINTABLE_START; i <= ASCII_PRINTABLE_FINAL; i++) {
        spacing(w, maxDigit / 2);
        w.printf("%c", (char)i);
        spacing(w, maxDigit / 2 + 1);
      }
      w.println();
      for(int i = 0; i < table.length; i++) {
        w.printf("%0" + maxDigit + "d ", i);
        for(int j = ASCII_PRINTABLE_START; j <= ASCII_PRINTABLE_FINAL; j++) {
          if(table[i][j] != -1) {
            w.printf("%0" + maxDigit + "d ", table[i][j]);
          }else {
            spacing(w, maxDigit / 2);
            w.print("X");
            spacing(w, maxDigit / 2 + 1);
          }
        }
        w.println();
      }
      w.flush();
      return new String(buff.getBuffer());
    }
    
    private void mark(Set<Integer> reachable, int stateNum) {
      if(reachable.contains(stateNum)) return;
      reachable.add(stateNum);
      for(int input = 0; input < NUM_ALPHABETS; input++){
        int next = table[stateNum][input];
        if(next != -1) mark(reachable, next);
      }
    }
    
    private static void spacing(PrintWriter w, int n) {
      for(int i = 0; i < n; i++) {
        w.print(" ");
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public static Dfa fromNfa2Dfa(Nfa nfa) {
    List<int[]>                                     tables  = list();
    Map<Set<Integer>, Integer>                      nfa2dfa = map();
    List<Set<Integer>>                              unmarked = list();
    Set<Integer>                                    finals  = set();
    Set<Integer>                                    dstart   = nfa.eclosure(nfa.startNum);
    unmarked.add(dstart);
    nfa2dfa.put(dstart, 0);
    tables.add(new int[NUM_ALPHABETS]);
    if(dstart.contains(nfa.finalNum)) finals.add(0);
    while(true) {
      if(unmarked.isEmpty()) break;
      Set<Integer> t = unmarked.remove(unmarked.size() - 1);
      Integer t2 = nfa2dfa.get(t);
      for(int sym = 0; sym < NUM_ALPHABETS; sym++){
        Set<Integer> u = nfa.eclosure(nfa.move(t, (char)sym));
        Integer u2 = nfa2dfa.get(u);
        if(u2 == null){
          u2 = nfa2dfa.size();
          unmarked.add(u);
          tables.add(new int[NUM_ALPHABETS]);
          nfa2dfa.put(u, u2);
          if(u.contains(nfa.finalNum)) finals.add(u2);
        }
        tables.get(t2)[sym] = u2;
      }
    }
        
    int[][] newTables = new int[nfa2dfa.size()][];
    int start = nfa2dfa.get(dstart);
    for(int stateNum : nfa2dfa.values()) {
      newTables[stateNum] = tables.get(stateNum);
    }
    return new Dfa(newTables, start, finals);
  }
}
