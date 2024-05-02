package jp.co.moneyforward.autotest.framework.utils;

import com.github.valid8j.pcond.forms.Printables;

import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;

import static com.github.valid8j.fluent.Expectations.*;

public enum ObjectUtils {
  ;
  
  
  public static final Atom NULL_VALUE = new Atom(null);
  
  public static Atom atom(Object value) {
    return new Atom(value);
  }
  
  public static Atom $(Object value) {
    return atom(value);
  }
  public static Array $(Element... values) {
    return array(values);
  }
  
  public static Dictionary $(Entry... entries) {
    return map(entries);
  }
  
  public static Atom nullValue() {
    return NULL_VALUE;
  }
  
  public static Array array(Element... elements) {
    return new Array(elements);
  }

  public static Entry entry(String key, Element atom) {
    return new Entry(key, atom);
  }
  
  public static Dictionary map(Entry... entries) {
    return new Dictionary(entries);
  }
  
  public record Atom(Object value) implements Element {
    public Atom(Object value) {
      this.value = requireArgument(that(value).satisfies().anyOf().instanceOf(String.class).instanceOf(Number.class).nullValue());
    }
  }
  
  public interface Element {
  }
  
  public record Array(Element... elements) implements Element {
  }
  public record Entry(String key, Element value) {
    public Entry(String key, Element value) {
      this.key = requireArgument(that(key).satisfies().notNull());
      this.value = requireArgument(that(value).satisfies().notNull());
    }
  }
  
  public static class Dictionary implements Element {
    private final SortedMap<String, Object> map;
    
    public Dictionary(Entry... entries) {
      this.map = new TreeMap<>();
      for (Entry e : entries)
        this.map.put(e.key, e.value);
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
  
}
