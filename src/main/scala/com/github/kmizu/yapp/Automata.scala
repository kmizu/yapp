/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp

import scala.util.control.Breaks._
import com.github.kmizu.yapp.util.CollectionUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Collection
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set
import com.github.kmizu.yapp.util.CollectionUtil._


/**
 * Represents the Automata(Nfa and DFA) namespace.
 * This class has no member except static class or interface or constant.
 * @author Kota Mizushima
 *
 */
object Automata {
  final val NUM_ALPHABETS: Int = Character.MAX_VALUE + 1
  def fromNfa2Dfa(nfa: Automata.Nfa): Automata.Dfa = {
    val tables: List[Array[Int]] = CollectionUtil.list[Array[Int]]()
    val nfa2dfa: Map[Set[Integer], Integer] = CollectionUtil.map[Set[Integer], Integer]()
    val unmarked: List[Set[Integer]] = CollectionUtil.list[Set[Integer]]()
    val finals: Set[Integer] = CollectionUtil.set[Integer]()
    val dstart: Set[Integer] = nfa.eclosure(nfa.startNum)
    unmarked.add(dstart)
    nfa2dfa.put(dstart, 0)
    tables.add(new Array[Int](NUM_ALPHABETS))
    if (dstart.contains(nfa.finalNum)) finals.add(0)
    breakable(while (true) {
      if (unmarked.isEmpty) break()
      val t = unmarked.remove(unmarked.size - 1)
      val t2 = nfa2dfa.get(t)
      var sym = 0
      while (sym < NUM_ALPHABETS) {
        val u = nfa.eclosure(nfa.move(t, sym.asInstanceOf[Char]))
        var u2 = nfa2dfa.get(u)
        if (u2 == null) {
          u2 = nfa2dfa.size
          unmarked.add(u)
          tables.add(new Array[Int](NUM_ALPHABETS))
          nfa2dfa.put(u, u2)
          if (u.contains(nfa.finalNum)) finals.add(u2)
        }
        tables.get(t2)(sym) = u2
        sym += 1
      }
    })
    val newTables = new Array[Array[Int]](nfa2dfa.size)
    val start = nfa2dfa.get(dstart)
    import scala.collection.JavaConversions._
    for (stateNum <- nfa2dfa.values) {
      newTables(stateNum) = tables.get(stateNum)
    }
    new Automata.Dfa(newTables, start, finals)
  }

  class Nfa {
    final val states: List[Automata.NfaState] = CollectionUtil.list[Automata.NfaState]()
    var startNum: Int = -1
    var finalNum: Int = -1

    def addState: Int = {
      val state: Automata.NfaState = new Automata.NfaState
      states.add(state)
      return states.size - 1
    }

    def addTransition(current: Int, input: Char, next: Int) {
      states.get(current).addTransition(input, next)
    }

    def addEpsilon(current: Int, next: Int) {
      states.get(current).addEpsilon(next)
    }

    def eclosure(stateNum: Int): Set[Integer] = {
      val result = set[Integer](stateNum)
      while (true) {
        val copy: Set[Integer] = setFrom[Integer](result)
        import scala.collection.JavaConversions._
        for (next <- copy) {
          val state: Automata.NfaState = states.get(next)
          result.addAll(state.etrans)
        }
        if (result.size == copy.size) break //todo: break is not supported
      }
      result
    }

    def eclosure(stateNums: Set[Integer]): Set[Integer] = {
      val result: Set[Integer] = CollectionUtil.set[Integer]()
      import scala.collection.JavaConversions._
      for (stateNum <- stateNums) result.addAll(eclosure(stateNum))
      result
    }

    def move(stateNums: Set[Integer], input: Char): Set[Integer] = {
      val result: Set[Integer] = CollectionUtil.set[Integer]()
      import scala.collection.JavaConversions._
      for (stateNum <- stateNums) {
        val nexts: Set[Integer] = states.get(stateNum).strans.get(input)
        if (nexts != null) result.addAll(nexts)
      }
      result
    }

  }

  class NfaState {
    def addTransition(input: Char, next: Int) {
      var set: Set[Integer] = strans.get(input)
      if (set == null) {
        set = set
        strans.put(input, set)
      }
      set.add(next)
    }

    def addEpsilon(next: Int) {
      etrans.add(next)
    }

    final val etrans: Set[Integer] = CollectionUtil.set[Integer]()
    final val strans: Map[Character, Set[Integer]] = CollectionUtil.map[Character, Set[Integer]]()
  }

  object Dfa {
    private def spacing(w: PrintWriter, n: Int): Unit = {
      var i: Int = 0
      while (i < n) {
        w.print(" ")
        i += 1
      }
    }

    final val ERROR: Automata.Dfa = new Automata.Dfa(null, -1, null)
  }

  class Dfa(val table: Array[Array[Int]], val start: Int, val finals: Set[Integer]) {
    import Dfa._
    import Automata._
    def and(rhs: Automata.Dfa): Automata.Dfa = {
      val newTable = Array.ofDim[Int](table.length * rhs.table.length, NUM_ALPHABETS)
      val newStart: Int = rhs.table.length * start + rhs.start
      val newFinals: Set[Integer] = CollectionUtil.set[Integer]()
      import scala.collection.JavaConversions._
      for (a <- finals) {
        import scala.collection.JavaConversions._
        for (b <- rhs.finals) {
          newFinals.add(rhs.table.length * a + b)
        }
      }
      {
        var a: Int = 0
        while (a < table.length) {
          {
            {
              var b: Int = 0
              while (b < rhs.table.length) {
                {
                  {
                    var input: Int = 0
                    while (input < NUM_ALPHABETS) {
                      {
                        val nextA: Int = table(a)(input)
                        val nextB: Int = rhs.table(b)(input)
                        if (nextA == -1 || nextB == -1) {
                          newTable(rhs.table.length * a + b)(input) = -1
                        }
                        else {
                          newTable(rhs.table.length * a + b)(input) = rhs.table.length * nextA + nextB
                        }
                      }
                      ({
                        input += 1; input - 1
                      })
                    }
                  }
                }
                ({
                  b += 1; b - 1
                })
              }
            }
          }
          ({
            a += 1; a - 1
          })
        }
      }
      return new Automata.Dfa(newTable, newStart, newFinals)
    }

    def disjoint(dfa: Automata.Dfa): Boolean = {
      return this.and(dfa).isEmpty
    }

    def isEmpty: Boolean = {
      val reachable: Set[Integer] = CollectionUtil.set[Integer]()
      mark(reachable, start)
      reachable.retainAll(finals)
      return reachable.isEmpty
    }

    override def toString: String = {
      var maxDigit: Int = String.valueOf(table.length).length
      if (maxDigit % 2 == 0) maxDigit += 1
      val ASCII_PRINTABLE_START = 32
      val ASCII_PRINTABLE_FINAL = 126
      val buff: StringWriter = new StringWriter
      val w: PrintWriter = new PrintWriter(buff)
      w.printf("start: %d%n", new Integer(start))
      w.printf("final: ")
      import scala.collection.JavaConversions._
      for (f <- finals) {
        w.printf("%0" + maxDigit + "d ", f)
      }
      w.println
      spacing(w, maxDigit + 1)

      {
        var i: Int = ASCII_PRINTABLE_START
        while (i <= ASCII_PRINTABLE_FINAL) {
          spacing(w, maxDigit / 2)
          w.printf("%c", new Character(i.asInstanceOf[scala.Char]))
          spacing(w, maxDigit / 2 + 1)
          i += 1
        }
      }
      w.println

      var i: Int = 0
      while (i < table.length) {
        w.printf("%0" + maxDigit + "d ", new Integer(i))
        var j: Int = ASCII_PRINTABLE_START
        while (j <= ASCII_PRINTABLE_FINAL) {
          if (table(i)(j) != -1) {
            w.printf("%0" + maxDigit + "d ", new Integer(table(i)(j)))
          } else {
            spacing(w, maxDigit / 2)
            w.print("X")
            spacing(w, maxDigit / 2 + 1)
          }
          j += 1
          w.println
        }
        i += 1
      }
      w.flush
      return new String(buff.getBuffer)
    }

    private def mark(reachable: Set[Integer], stateNum: Int): Unit = {
      if (reachable.contains(stateNum)) return
      reachable.add(stateNum)
      var input = 0
      while (input < NUM_ALPHABETS) {
        val next = table(stateNum)(input)
        if (next != -1) mark(reachable, next)
        input += 1
      }
    }
  }

}
