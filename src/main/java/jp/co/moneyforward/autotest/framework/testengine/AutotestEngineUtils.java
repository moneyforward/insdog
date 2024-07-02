package jp.co.moneyforward.autotest.framework.testengine;

import java.util.*;

public enum AutotestEngineUtils {
  ;
  
  public static <T> List<T> topologicalSort(List<T> specified, Map<T, List<T>> graph) {
    Set<T> visited = new LinkedHashSet<>();
    specified.forEach(each -> traverseDependencies(each, graph, visited));
    return new LinkedList<>(visited);
  }
  
  private static <T> void traverseDependencies(T node, Map<T, List<T>> graph, Set<T> visited) {
    if (visited.contains(node))
      return;
    if (!graph.containsKey(node)) {
      throw new NoSuchElementException("Unknown node:<" + node + "> was given. Known nodes are: " + graph.keySet());
    }
    graph.get(node).forEach(each -> traverseDependencies(each, graph, visited));
    visited.add(node);
  }
  
  static <T> List<T> mergeListsByAppendingMissedOnes(List<T> list1, List<T> list2) {
    List<T> ret = new ArrayList<>(list1);
    for (T item : list2) {
      if (!ret.contains(item)) {
        ret.add(item);
      }
    }
    return ret;
  }
  
  static <T> List<T> mergeListsByInsertingMissedOnes(List<T> list1, List<T> list2) {
    List<T> ret = new ArrayList<>(list2.size() + list1.size());
    for (T item : list2) {
      if (!list1.contains(item)) {
        ret.add(item);
      }
    }
    ret.addAll(list1);
    return ret;
  }
}
