package jp.co.moneyforward.autotest.examples;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.core.Context.Impl;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.ca_web.actions.gui.Click;
import jp.co.moneyforward.autotest.ca_web.actions.gui.Navigate;
import jp.co.moneyforward.autotest.ca_web.actions.gui.SendKey;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.Act.Func;
import jp.co.moneyforward.autotest.framework.action.Act.Let;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;

@Tag("ProgrammingModel")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connect", "disconnect", "logout"},
        afterEach = {},
        afterAll = {"close"},
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProgrammingModelExample implements AutotestRunner {
  private static final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(getContext(), new HashMap<>());
  
  private static Context getContext() {
    return new Impl() {
      @Override
      public Context createChild() {
        return this;
      }
    };
  }
  
  @Named
  public static Scene open() {
    return new Scene.Builder()
        .add("WINDOW", new Let<>(Playwright.create()))
        .add("BROWSER", new Func<>("Playwright::chromium", (Playwright p) -> p.chromium().launch()), "WINDOW")
        .add("PAGE", new Func<>("Browser::newPage", (Browser b) -> b.newPage()), "BROWSER")
        .build();
  }
  
  
  @Named
  public static Scene login() {
    return new Scene.Builder()
        .assign("PAGE")
        .add(new Navigate("https://ca-web-ca-app-ai-ocr-bulk-upload.idev.test.musubu.co.in"), "PAGE")
        .add(new Click("text=ログインはこちらから"))
        .add(new SendKey("input[name='mfid_user[email]']", "ukai.hiroshi@moneyforward.co.jp"))
        .add(new SendKey("input[name='mfid_user[password]']", "!QAZ@WSX"))
        .add(new Click("button[id='submitto']"))
        .add(new Click("text=スキップする"))
        .build();
  }
  
  @Named
  public static Scene connect() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene disconnect() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene logout() {
    return new Scene.Builder().build();
  }
  
  @Named
  public static Scene close() {
    return new Scene.Builder()
        .assign("BROWSER")
        .add(new CloseBrowser(), "BROWSER")
        .add(new CloseWindow(), "WINDOW")
        .build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
}
