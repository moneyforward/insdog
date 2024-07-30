package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.exceptions.ActionException;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"login"},
        value = {"connect", "disconnect"},
        afterAll = {"logout"}))
public class AllPassingWithBeforeAndAfterAllTestbed extends TestbedBase {
  @Named
  public static Scene fail() {
    return new Scene.Builder("fail").add("out", (LeafAct<Object, Object>) (value, executionEnvironment) -> {
      throw new ActionException("Intentional Exception!");
    }).build();
  }
}
