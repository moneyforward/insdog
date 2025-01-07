package jp.co.moneyforward.autotest.ututils;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Act.Let;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;

public enum ActUtils {
  ;
  
  public static <T> Let<T> let(T value) {
    return new Let<>(value);
  }
  
  public static Act<String, String> helloAct() {
    return name("helloAct", (value, executionEnvironment) -> "HELLO:" + value);
  }
  
  public static Act<String, String> printlnAct() {
    return name("println", (value, executionEnvironment) -> {
      System.out.println(value);
      return value;
    });
  }
  
  public static Act<String, String> addToListAct(List<String> list) {
    return name("addTo:" + list, (value, executionEnvironment) -> {
      list.add(value);
      return value;
    });
  }
  
  private static <T, R> Act<T, R> name(String name, Act<T, R> act) {
    return new Act<>() {
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
