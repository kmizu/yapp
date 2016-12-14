package com.github.kmizu.yapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DirectedGraph<N, ExtraInfo> implements Iterable<N> {
  private final Set<N> nodes = new HashSet<N>();
  private final Map<N, Set<N>> edges = new HashMap<N, Set<N>>();
  private final Map<N, ExtraInfo> infoMap = new HashMap<N, ExtraInfo>();
  private ExtraInfo defaultInfo;
  
  public DirectedGraph(ExtraInfo defaultInfo) {
    this.defaultInfo = defaultInfo;
  }
  
  public DirectedGraph() {    
  }
  
  public void add(N node) {
    if(!nodes.contains(node)) {
      edges.put(node, new HashSet<N>());
    }
    nodes.add(node);
    infoMap.put(node, defaultInfo);
  }
  
  public void setInfo(N node, ExtraInfo info) {
    infoMap.put(node, info);
  }
  
  public ExtraInfo getInfo(N node) {
    return infoMap.get(node);
  }
  
  public boolean contains(N node) {
    return nodes.contains(node);
  }
  
  public void addEdge(N from, N to) {
    edges.get(from).add(to);
  }
  
  public Set<N> neighbors(N from) {
    return Collections.unmodifiableSet(edges.get(from));
  }
  
  public Iterator<N> iterator() {
    return nodes.iterator();
  }
  
  public boolean hasCyclicity(N start) {
    return hasCyclicity(start, start, new HashSet<N>());
  }
  
  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    for(N n:nodes) {
      buf.append(n);
      buf.append(" -> ");
      buf.append(edges.get(n));
      buf.append(System.getProperty("line.separator"));
    }
    return new String(buf);
  }
  
  private boolean hasCyclicity(N start, N current, Set<N> visit) {
    if(visit.contains(current)) {
      return true;
    }
    try {
      visit.add(current);
      for(N s:neighbors(current)){
        if(hasCyclicity(start, s, visit)) return true;
      }
      return false;
    }finally{
      visit.remove(current);
    }
  }
}
