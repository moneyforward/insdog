package jp.co.moneyforward.autotest.it.t4t;

import jp.co.moneyforward.autotest.ca_web.tests.selftest.SelfTest;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.annotations.When;

import java.util.function.Supplier;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = DEPENDENCY_BASED,
        value = {"pass"}
    ))
public class FailInWhenClause extends SelfTest {
  @Named
  @Export("page")
  public static Scene pass() {
    return new Scene.Builder("page").add(createAct("passing", () -> "Hello, I'm passing!")).build();
  }
  
  @Named
  @When("pass")
  public static Scene thenFail() {
    return new Scene.Builder("page").add(
        createAct("intentionalFailure", () -> {
          throw new RuntimeException("FAIL!");
        })).build();
  }
  
  private static Act.Source<Object> createAct(final String name, final Supplier<String> action) {
    return new Act.Source<>() {
      @Override
      protected Object value() {
        return action.get();
      }
      
      @Override
      public String name() {
        return name;
      }
    };
  }
}
