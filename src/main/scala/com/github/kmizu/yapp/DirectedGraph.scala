package com.github.kmizu.yapp

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set

class DirectedGraph extends Iterable[N] {
  def this(defaultInfo: ExtraInfo) {
    this()
    this.defaultInfo = defaultInfo
  }

  def this() {
    this()
  }

  def add(node: N) {
    if (!nodes.contains(node)) {
      edges.put(node, new HashSet[N])
    }
    nodes.add(node)
    infoMap.put(node, defaultInfo)
  }

  def setInfo(node: N, info: ExtraInfo) {
    infoMap.put(node, info)
  }

  def getInfo(node: N): ExtraInfo = {
    return infoMap.get(node)
  }

  def contains(node: N): Boolean = {
    return nodes.contains(node)
  }

  def addEdge(from: N, to: N) {
    edges.get(from).add(to)
  }

  def neighbors(from: N): Set[N] = {
    return Collections.unmodifiableSet(edges.get(from))
  }

  def iterator: Iterator[N] = {
    return nodes.iterator
  }

  def hasCyclicity(start: N): Boolean = {
    return hasCyclicity(start, start, new HashSet[N])
  }

  override def toString: String = {
    val buf: StringBuffer = new StringBuffer
    import scala.collection.JavaConversions._
    for (n <- nodes) {
      buf.append(n)
      buf.append(" -> ")
      buf.append(edges.get(n))
      buf.append(System.getProperty("line.separator"))
    }
    return new String(buf)
  }

  private def hasCyclicity(start: N, current: N, visit: Set[N]): Boolean = {
    if (visit.contains(current)) {
      return true
    }
    try {
      visit.add(current)
      import scala.collection.JavaConversions._
      for (s <- neighbors(current)) {
        if (hasCyclicity(start, s, visit)) return true
      }
      return false
    }
    finally {
      visit.remove(current)
    }
  }

  private var nodes: Set[N] = new HashSet[N]
  private var edges: Map[N, Set[N]] = new HashMap[N, Set[N]]
  private var infoMap: Map[N, ExtraInfo] = new HashMap[N, ExtraInfo]
  private var defaultInfo: ExtraInfo = null
}
