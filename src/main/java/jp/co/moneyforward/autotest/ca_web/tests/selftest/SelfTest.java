package jp.co.moneyforward.autotest.ca_web.tests.selftest;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;

import java.util.HashMap;

/**
 * A test to check if the framework works as designed.
 * This test is designed not to access the **caweb** application.
 */
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        value = {"connect", "disconnect"}
    ))
public class SelfTest implements AutotestRunner {
  private final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(Context.create(), new HashMap<>());
  
  @Named
  @Export("page")
  @ClosedBy("close")
  public static Scene open() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("OPEN"))
        .build();
  }
  
  @Named
  @ClosedBy("logout")
  @Export("page")
  @DependsOn("open")
  public static Scene login() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("LOGIN"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene connect() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("CONNECT"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene disconnect() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("DISCONNECT"))
        .build();
  }
  
  @Named
  @DependsOn("login")
  public static Scene logout() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("LOGOUT"))
        .build();
  }
  
  @Named
  @DependsOn("open")
  public static Scene close() {
    return new Scene.Builder("page")
        .add(new LeafAct.Let<>("CLOSE"))
        .build();
  }

  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
}