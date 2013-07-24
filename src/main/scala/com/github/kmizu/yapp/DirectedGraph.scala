package com.github.kmizu.yapp

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set

class DirectedGraph[N, ExtraInfo >: Null <: AnyRef](defaultInfo: ExtraInfo) extends Iterable[N] {
  private[this] val nodes = new HashSet[N]
  private[this] val edges = new HashMap[N, Set[N]]
  private[this] val infoMap = new HashMap[N, ExtraInfo]

  def this() {
    this(null)
  }

  def add(node: N) {
    if (!nodes.contains(node)) {
      edges.put(node, new HashSet[N])
    }

    nodes.add(node)
    infoMap.put(node, defaultInfo)
  }

  def setInfo(node: N, info: ExtraInfo): Unit = infoMap.put(node, info)

  def getInfo(node: N): ExtraInfo = infoMap.get(node)

  def contains(node: N): Boolean = nodes.contains(node)

  def addEdge(from: N, to: N): Unit = edges.get(from).add(to)

  def neighbors(from: N): Set[N] = Collections.unmodifiableSet(edges.get(from))

  def iterator: Iterator[N] = nodes.iterator

  def hasCyclicity(start: N): Boolean = {
    def cyclic(start: N, current: N, visit: Set[N]): Boolean = {
      if (visit.contains(current))  return true

      try {
        visit.add(current)
        import scala.collection.JavaConversions._
        for (s <- neighbors(current)) {
          if (cyclic(start, s, visit)) return true
        }
        false
      } finally {
        visit.remove(current)
      }
    }

    cyclic(start, start, new HashSet[N])
  }

  override def toString: String = {
    val buf = new StringBuffer
    import scala.collection.JavaConversions._
    for (n <- nodes) {
      buf.append(n)
      buf.append(" -> ")
      buf.append(edges.get(n))
      buf.append(System.getProperty("line.separator"))
    }
    new String(buf)
  }
}
