package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

import static com.github.dakusui.actionunit.core.ActionSupport.attempt;
import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.action.Scene.create;
import static jp.co.moneyforward.autotest.actions.web.PageAct.pageAct;

@AutotestExecution(
    defaultExecution = @Spec(
        value = {"connect", "disconnect"},
        planExecutionWith = DEPENDENCY_BASED
    ))
public class PropertyEnsuring implements AutotestRunner {
  
  @Named
  @Export
  @ClosedBy("closePageSession")
  public Scene openPageSession() {
    return create("openPageSession");
  }
  
  @Named
  public Scene closePageSession() {
    return create("closeExecutionSession");
  }
  
  @Named
  @Export
  @DependsOn("openPageSession")
  public Scene loadLoginSession() {
    return create("loadLoginSession");
  }
  
  @Named
  @Export
  @DependsOn("openPageSession")
  public Scene login() {
    return create("login",
                  pageAct("enterUsername", (p, e) -> {
                  }),
                  pageAct("enterPassword", (p, e) -> {
                  }),
                  pageAct("clickLogin", (p, e) -> {
                  }),
                  pageAct("enterTOTP", (p, e) -> {
                  }),
                  pageAct("submit", (p, e) -> {
                  }));
  }
  
  @Named
  @Export
  public Scene isLoggedIn() {
    class Done extends RuntimeException {
    }
    Action done = ActionSupport.leaf(c -> {
      throw new Done();
    });
    attempt(sequential(done))
        .recover(Done.class, (c) -> {
          throw (RuntimeException) c;
        });
    return create("isLoggedIn");
  }
  
  @Named
  @Export
  @DependsOn({"loadLoginSession|login", "isLoggedIn!"})
  //@DependsOn({"loadLoginSession", "isLoggedIn!", "login", "isLoggedIn!"})
  public Scene ensureLoggedIn() {
    return new Scene.Builder("ensureLoggedIn").build();
  }
  
  @Named
  @DependsOn("ensureLoggedIn")
  public Scene connect() {
    return new Scene.Builder("connect").build();
  }
  
  @Named
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
}
