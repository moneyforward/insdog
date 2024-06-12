package jp.co.moneyforward.autotest.ututils;

import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Let;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;

public enum ActUtils {
  ;
  
  @SuppressWarnings("ClassEscapesDefinedScope")
  public static <T> Let<T> let(T value) {
    return new Let<>(value);
  }
  
  @SuppressWarnings("ClassEscapesDefinedScope")
  public static LeafAct<String, String> helloAct() {
    return name("helloAct", (value, executionEnvironment) -> "HELLO:" + value);
  }
  
  public static LeafAct<String, String> exclamationAct() {
    return name("exclamationAct", (value, executionEnvironment) -> value + "!");
  }
  
  public static LeafAct<String, String> printlnAct() {
    return name("println", (value, executionEnvironment) -> {
      System.out.println(value);
      return value;
    });
  }
  
  public static LeafAct<String, String> addToListAct(List<String> list) {
    return name("addTo:" + list, (value, executionEnvironment) -> {
      list.add(value);
      return value;
    });
  }
  
  private static <T, R> LeafAct<T, R> name(String name, LeafAct<T, R> act) {
    return new LeafAct<>() {
      @Override
      public String name() {
        return name;
      }
      
      @Override
      public R perform(T value, ExecutionEnvironment executionEnvironment) {
        return act.perform(value, executionEnvironment);
      }
    };
  }
}
