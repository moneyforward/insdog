package jp.co.moneyforward.autotest.framework.selftest;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Act.Let;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;

import java.util.HashMap;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.createContext;

/**
 * A test to check if the framework works as designed.
 * This test is designed not to access the **caweb** application.
 */
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"snapshot"},
        value = {"connect", "disconnect"},
        afterEach = {"snapshot"}
    ))
public class SelfTest implements AutotestRunner {
  private static boolean enableAssertion = false;
  public static final String OVERRIDING_DOMAIN_NAME = "overriding.domain.name.co.jp";
  private final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(createContext(), new HashMap<>());
  
  @Named
  public Scene snapshot() {
    return Scene.begin()
                .act(new Act.Func<>(p -> p))
                .end();
  }
  
  @Named
  @Export("page")
  @ClosedBy("close")
  public static Scene open() {
    return Scene.begin()
                .act(new Let<>("OPEN"))
                .end();
  }
  
  @Named
  @ClosedBy("logout")
  @Export("page")
  @DependsOn("open")
  public static Scene login() {
    return Scene.begin()
                .act(new Let<>("LOGIN"))
                .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene connect() {
    return Scene.begin()
                .act(new Let<>("CONNECT"))
                .act(new Act.Func<>(p -> p))
                .end();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene disconnect() {
    return new Scene.Builder("page")
        .act(new Let<>("DISCONNECT"))
        .build();
  }
  
  @Named
  @DependsOn("login")
  public static Scene logout() {
    return new Scene.Builder("page")
        .act(new Let<>("LOGOUT"))
        .build();
  }
  
  @Named
  @DependsOn("open")
  public static Scene close() {
    return new Scene.Builder("page")
        .act(new Let<>("CLOSE"))
        .build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
  
  public static void enableAssertion() {
    enableAssertion = true;
  }
  
  public static void disableAssertion() {
    enableAssertion = false;
  }
}