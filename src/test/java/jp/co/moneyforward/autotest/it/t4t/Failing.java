package jp.co.moneyforward.autotest.it.t4t;


import jp.co.moneyforward.autotest.ca_web.tests.selftest.SelfTest;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        value = {"fail"}
    ))
public class Failing extends SelfTest {
  @Named
  public static Scene fail() {
    return new Scene.Builder("page").add(new LeafAct.Source<>() {
      @Override
      protected Object value() {
        throw new RuntimeException("FAIL!");
      }
      
      @Override
      public String name() {
        return "intentionalFailure";
      }
    }).build();
  }
}
