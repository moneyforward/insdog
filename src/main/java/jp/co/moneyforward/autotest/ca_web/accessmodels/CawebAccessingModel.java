package jp.co.moneyforward.autotest.ca_web.accessmodels;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.core.Context.Impl;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.*;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Func;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Let;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.ClosedBy;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.github.valid8j.fluent.Expectations.value;
import static java.util.concurrent.TimeUnit.SECONDS;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.*;

/**
 * This test assumes that the user provided by `EXECUTION_PROFILE` has already been registered and associated with
 * a corporation to which they belong.
 *
 * A test that translated [ca_login_check.csv](https://github.com/moneyforward/ca_web_e2e_test_d/blob/main/action_files/scenarios/ca/login_check/ca_login_check.csv) in
 * [駄犬くん](https://github.com/moneyforward/ca_web_e2e_test_d).
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class CawebAccessingModel implements AutotestRunner {
  private final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(getContext(), new HashMap<>());
  
  /**
   * An execution profile, which hosts variables in test executions.
   *
   * The variables in a profile should not change their values in one test execution.
   */
  public static final ExecutionProfile EXECUTION_PROFILE = new ExecutionProfile();
  
  /**
   * Returns a scene that performs **open** operation.
   *
   * The returned scene should be closed by **close** operation.
   * This is a general action, that can be used for various applications because it internally does only create **Playwright**
   * instance, open a **Browser** object, and then create a new **Browser** context, all of which are necessary to almost all
   * web applications.
   *
   * @return A scene that performs **open** operation.
   */
  @Named
  @ClosedBy("close")
  @Export({"window", "browser", "page"})
  public static Scene open() {
    TimeUnit timeUnit = SECONDS;
    int time = 30;
    return new Scene.Builder("NONE")
        .add("window", new Let<>(Playwright.create()))
        .add("browser", new Func<>("Playwright::chromium",
                                   (Playwright p) -> p.chromium()
                                                      .launch(new BrowserType.LaunchOptions().setHeadless(EXECUTION_PROFILE.setHeadless()))),
             "window")
        .add("browserContext", new Func<>("Browser::newContext->setDefaultTimeout(" + time + timeUnit + ")", (Browser b) -> {
          BrowserContext c = ExecutionProfile.browserContextFrom(b, EXECUTION_PROFILE);
          c.setDefaultTimeout(timeUnit.toMillis(time));
          return c;
        }), "browser")
        .add("page", new Func<>("BrowserContext::newPage", BrowserContext::newPage), "browserContext")
        .build();
  }
  
  /**
   * Returns an action for logging in the **ca_web** application using the variables defined in the `EXECUTION_PROFILE`.
   *
   * @return A login action.
   * @see CawebAccessingModel#EXECUTION_PROFILE
   */
  @Named
  @ClosedBy("logout")
  @Export("page")
  @DependsOn("open")
  public static Scene login() {
    return new Scene.Builder("login")
        .add("page", new Navigate(EXECUTION_PROFILE.homeUrl()), "page")
        .add("page", new SendKey(PageFunctions.getByPlaceholder("example@moneyforward.com"), EXECUTION_PROFILE.userEmail()), "page")
        .add("page", new Click(getButtonByName("ログインする")), "page")
        .add("page", new SendKey(PageFunctions.getByLabel("パスワード"), EXECUTION_PROFILE.userPassword()), "page")
        .add("page", new Click("button[id='submitto']"), "page")
        .build();
  }
  
  /**
   * Returns a scene, which performs a "logout" action.
   *
   * @return A scene to perform a "logout" action.
   */
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene logout() {
    return new Scene.Builder("page")
        .add(new PageAct("""
                             Disable logout since BASIC-auth workaround shouldn't be used once self-hosted GitHub.
                             Check https://app.asana.com/0/1206402209253009/1207606779915586/f for more detail.
                             """) {
          @Override
          protected void action(Page page,
                                ExecutionEnvironment executionEnvironment) {
            assumeStatement(value(today()).toBe()
                                          .predicate(dateAfter(date(EXECUTION_PROFILE.plannedDateForSettingUpSelfhostedGitHubActions())))
                                          .$());
          }
        })
        .add(new Click(getLinkByName("スペシャルサンドボックス合同会社 (法人)", true)))
        .add(new Click(getLinkByName("ログアウト")))
        .build();
  }
  
  /**
   * Returns a scene object, which performs a screenshot.
   *
   * @return A scene object which performs a screenshot.
   * @see Screenshot
   */
  @Named
  @Export("page")
  @DependsOn("open")
  public static Scene screenshot() {
    return new Scene.Builder("page")
        .add("NONE", new Screenshot(), "page")
        .build();
  }
  
  /**
   * Returns a `Scene`, which closes the ongoing session.
   *
   * @return A `Scene`, which closes the ongoing session.
   */
  @Named
  @DependsOn("open")
  public static Scene close() {
    return new Scene.Builder("close")
        .add("NONE", new CloseBrowser(), "browser")
        .add("NONE", new CloseWindow(), "window")
        .build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
  
  private static Context getContext() {
    return new Impl() {
      @Override
      public Context createChild() {
        return this;
      }
    };
  }
}
