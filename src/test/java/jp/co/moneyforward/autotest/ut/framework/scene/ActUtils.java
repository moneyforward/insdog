package jp.co.moneyforward.autotest.ut.framework.scene;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Act.Let;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public enum ActUtils {
  ;
  
  @SuppressWarnings("ClassEscapesDefinedScope")
  public static <T> Let<T> let(T value) {
    return new Let<>(value);
  }
  
  @SuppressWarnings("ClassEscapesDefinedScope")
  public static Act<String, String> helloAct() {
    return name("helloAct", (value, executionEnvironment) -> "HELLO:" + value);
  }
  
  static Act<String, String> exclamationAct() {
    return name("exclamationAct", (value, executionEnvironment) -> value + "!");
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
