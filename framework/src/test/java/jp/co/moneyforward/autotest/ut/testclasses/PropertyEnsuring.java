package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.BiConsumer;

import static com.github.dakusui.actionunit.core.ActionSupport.attempt;
import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static java.util.function.Function.identity;
import static jp.co.moneyforward.autotest.actions.web.PageAct.pageAct;
import static jp.co.moneyforward.autotest.framework.action.Scene.create;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;

@AutotestExecution(
    defaultExecution = @Spec(
        value = {"connect", "disconnect"},
        planExecutionWith = DEPENDENCY_BASED
    ))
public class PropertyEnsuring implements AutotestRunner {
  
  @Named
  @Export
  @ClosedBy("closeExecutionSession")
  public Scene openExecutionSession() {
    return Scene.create("openPageSession",
                        Act.create("openWindow", identity()),
                        Act.create("openBrowser", identity()));
  }
  
  @Named
  public Scene closeExecutionSession() {
    return Scene.create("closeExecutionSession",
                        Act.create("closeBrowser", identity()),
                        Act.create("closeWindow", identity()));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene loadLoginSession() {
    return Scene.create("loadLoginSession",
                        Act.create("loadLoginSessionFromFile", identity()));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene saveLoginSession() {
    return Scene.create("saveLoginSession",
                        Act.create("saveLoginSessionToFile", identity()));
  }
  
  @Named
  @Export
  @DependsOn("openExecutionSession")
  public Scene login() {
    return Scene.create("login",
                        pageAct("enterUsername", emptyAction()),
                        pageAct("enterPassword", emptyAction()),
                        pageAct("clickLogin", emptyAction()),
                        pageAct("enterTOTP", emptyAction()),
                        pageAct("submit", emptyAction()));
  }
  
  @Named
  @Export
  @PreparedBy("loadLoginSession")
  @PreparedBy({"login", "saveLoginSession"})
  public Scene isLoggedIn() {
    return Scene.create("isLoggedIn");
  }
  
  @Named
  @DependsOn("isLoggedIn")
  public Scene connect() {
    return new Scene.Builder("connect").build();
  }
  
  @Named
  @DependsOn("isLoggedIn")
  public Scene disconnect() {
    return new Scene.Builder("disconnect").build();
  }
  
  @Named
  public Scene logout() {
    return new Scene.Builder("logout").build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return ReportingActionPerformer.create();
  }
  
  private static BiConsumer<Page, ExecutionEnvironment> emptyAction() {
    return (p, e) -> {
    };
  }
}
