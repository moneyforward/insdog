package jp.co.moneyforward.autotest.ut.misc;

public enum ObjectUtils {
  ;
  
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
    return Atom.NULL_VALUE;
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
 
}
