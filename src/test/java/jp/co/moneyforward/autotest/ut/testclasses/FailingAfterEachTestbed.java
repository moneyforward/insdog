package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.exceptions.ActionException;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;

@AutotestExecution(defaultExecution = @AutotestExecution.Spec(
    value = {"login", "connect", "disconnect", "logout"},
    afterEach = "fail"))
public class FailingAfterEachTestbed extends TestbedBase {
  @Named
  public static Scene fail() {
    return new Scene.Builder("fail").add("out",
                                         (value, executionEnvironment) -> {
                                           throw new ActionException("Intentional Exception!");
                                         }).build();
  }
}
