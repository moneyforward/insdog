package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.exceptions.ActionException;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        value = {"login", "connect", "disconnect", "logout", "fail"},
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
public class AllPassingTestbed extends TestbedBase {
  @Named
  public static Scene fail() {
    return new Scene.Builder().add((Act<Object, Object>) (value, executionEnvironment) -> {
      throw new ActionException("Intentional Exception!");
    }).build();
  }
}
