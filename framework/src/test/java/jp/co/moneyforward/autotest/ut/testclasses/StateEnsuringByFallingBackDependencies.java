package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static jp.co.moneyforward.autotest.actions.web.PageAct.pageAct;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;

@AutotestExecution(
    defaultExecution = @Spec(
        value = {"connect", "disconnect"},
        planExecutionWith = DEPENDENCY_BASED
    ))
public class StateEnsuringByFallingBackDependencies implements AutotestRunner {
  
  @Named
  @Export
  @ClosedBy("closeExecutionSession")
  public Scene openExecutionSession() {
    return Scene.create("openPageSession",
                        act("openWindow"),
                        act("openBrowser"));
  }
  
  @Named
  public Scene closeExecutionSession() {
    return Scene.create("closeExecutionSession",
                        act("closeBrowser"),
                        act("closeWindow"));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene toHomeScreen() {
    return Scene.create("toHome", act("goToHomeScreenByDirectlyEnteringUrl"));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene loadLoginSession() {
    return Scene.create("loadLoginSession",
                        act("loadLoginSessionFromFile"));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene saveLoginSession() {
    return Scene.create("saveLoginSession",
                        act("saveLoginSessionToFile"));
  }
  
  /**
   * Let's not specify "logout" for login.
   *
   * A test for log-in and log-out to be performed as expected should be a separate and independent test class.
   *
   * @return A scene that performs "login"
   */
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene login() {
    return Scene.create("login",
                        act("enterUsername"),
                        act("enterPassword"),
                        act("clickLogin"),
                        act("enterTOTP"),
                        act("submit"));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  @PreparedBy({"toHomeScreen"})
  @PreparedBy({"loadLoginSession", "toHomeScreen"})
  @PreparedBy({"login", "saveLoginSession"})
  public Scene isLoggedIn() {
    return Scene.create("isLoggedIn", act("checkIfIamOnHomeScreen"));
  }
  
  @Named
  @DependsOn("isLoggedIn")
  public Scene connect() {
    return Scene.create("connect", act("connectBank"));
  }
  
  @Named
  @DependsOn("isLoggedIn")
  public Scene disconnect() {
    return Scene.create("disconnect", act("disconnectBank"));
  }
  
  @Named
  public Scene logout() {
    return Scene.create("logout", act("loggingOut"));
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return ReportingActionPerformer.create();
  }
  
  private static Act<Object, Object> act(String description) {
    return Act.create(description, emptyFunction(description));
  }
  
  private static Function<Object, Object> emptyFunction(String description) {
    return x -> {
      System.out.println(description);
      return x;
    };
  }
}
