package jp.co.moneyforward.autotest.ut.misc;

import com.github.valid8j.pcond.forms.Printables;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;

import static com.github.valid8j.fluent.Expectations.requireArgument;
import static com.github.valid8j.fluent.Expectations.value;

public class Dictionary implements Element {
  private final SortedMap<String, Object> map;
  
  public Dictionary(Entry... entries) {
    this.map = new TreeMap<>();
    for (Entry e : entries)
      this.map.put(e.key(), e.value());
  }
  
  @SuppressWarnings("unchecked")
  public <E extends Element> E valueFor(String key) {
    return (E) this.map.get(requireArgument(value(key).toBe().notNull().predicate(containedBy(this.map.keySet()))));
  }
  
  public boolean hasKey(String key) {
    return this.map.containsKey(key);
  }
  
  public Set<String> keys() {
    return this.map.keySet();
  }
  
  private static <T> Predicate<T> containedBy(Collection<T> objects) {
    requireArgument(value(objects).toBe().notNull());
    return Printables.predicate("containedBy[" + objects + "]", objects::contains);
  }
}
