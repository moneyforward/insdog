package jp.co.moneyforward.autotest.examples;

import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(value = {"login", "connect", "disconnect", "logout"},
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
public class ProgrammingModelExample implements AutotestRunner {
  @Named
  public static Scene login() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene connect() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene disconnect() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene logout() {
    return new Scene.Builder().build();
  }
}
