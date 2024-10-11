package jp.co.moneyforward.autotest.ut.framework.engine;

import jp.co.moneyforward.autotest.framework.testengine.AutotestEngineUtils;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutotestEngineUtilsTest {
  
  @Test
  public void whenNonSpecified_thenEmpty() {
    List<String> specified = new ArrayList<>();
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().empty());
  }
  
  @Test
  public void whenOneWithoutDependencySpecified_thenItIsReturned() {
    List<String> specified = List.of("open");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().equalTo(List.of("open")));
  }
  
  @Test
  public void whenOneWithDependencySpecified_thenItIsReturnedWithDependency() {
    List<String> specified = List.of("login");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().equalTo(List.of("open", "login")));
  }
  
  @Test
  public void whenOneWithIndirectDependencySpecified_thenItIsReturnedWithDependency() {
    List<String> specified = List.of("connect");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().equalTo(List.of("open", "login", "connect")));
  }
  
  @Test
  public void whenOnesSpecifiedIncludingAllDependencies_thenOrderIsKept() {
    List<String> specified = List.of("open", "login", "connect");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().equalTo(List.of("open", "login", "connect")));
  }
  
  @Test
  public void whenOnesSpecifiedNotInOrderOfDependencies_thenDependencyIsRespectedOverSpecifiedOrder() {
    List<String> specified = List.of("connect", "login", "open");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    List<String> out = AutotestEngineUtils.topologicalSort(specified, graph);
    
    assertStatement(value(out).toBe().equalTo(List.of("open", "login", "connect")));
  }
  
  
  @Test
  public void whenOneNotInGraph_thenNosuchElementIsThrown() {
    List<String> specified = List.of("hello!");
    Map<String, List<String>> graph = prepareInitialGraph();
    
    assertThrows(NoSuchElementException.class, () -> AutotestEngineUtils.topologicalSort(specified, graph));
  }
  
  
  @SafeVarargs
  private static <K, V> Map<K, V> linkedHashMapOf(Entry<K, V>... entries) {
    LinkedHashMap<K, V> ret = new LinkedHashMap<>();
    for (Entry<K, V> entry : entries) {
      ret.put(entry.key(), entry.value());
    }
    return ret;
  }
  
  
  private static Map<String, List<String>> prepareInitialGraph() {
    return linkedHashMapOf(
        entry("open", List.of()),
        entry("login", List.of("open")),
        entry("connect", List.of("login")),
        entry("disconnect", List.of("login")),
        entry("logout", List.of("login")),
        entry("close", List.of("open"))
    );
  }
  
  private static <K, V> Entry<K, V> entry(K key, V value) {
    return new Entry<>(key, value);
  }
  
  private record Entry<K, V>(K key, V value) {
  }
}
