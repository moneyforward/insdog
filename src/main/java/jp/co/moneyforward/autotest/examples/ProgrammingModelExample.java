package jp.co.moneyforward.autotest.examples;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.core.Context.Impl;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.ca_web.ExecutionProfile;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Func;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Let;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn.Parameter;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;

import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.*;

/**
 * // @formatter:off
 * This test assumes that the user provided by `EXECUTION_PROFILE` has already been registered and associated with
 * a corporation to which they belong.
 *
 * A test that translated link:https://github.com/moneyforward/ca_web_e2e_test_d/blob/main/action_files/scenarios/ca/login_check/ca_login_check.csv[ca_login_check.csv] in
 * link:https://github.com/moneyforward/ca_web_e2e_test_d[駄犬くん].
 *
 * // @formatter:on
 *
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Tag("ProgrammingModel")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connect", "disconnect", "logout"},
        afterEach = {"snapshot"},
        afterAll = {"close"},
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProgrammingModelExample implements AutotestRunner {
  private final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(getContext(), new HashMap<>());
  
  private static final ExecutionProfile EXECUTION_PROFILE = new ExecutionProfile();
  
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
        .add("window", new Let<>(Playwright.create()))
        .add("browser", new Func<>("Playwright::chromium", (Playwright p) -> p.chromium().launch()), "window")
        .add("page", new Func<>("Browser::newPage", (Browser b) -> b.newPage()), "browser")
        .build();
  }
  
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene login() {
    return new Scene.Builder()
        .add("page", new Navigate(EXECUTION_PROFILE.homeUrl()), "page")
        .add("page", new SendKey(LocatorFunctions.getByPlaceholder("example@moneyforward.com"), EXECUTION_PROFILE.userEmail()), "page")
        .add("page", new Click(getButtonByName("ログインする")), "page")
        .add("page", new SendKey(LocatorFunctions.getByLabel("パスワード"), EXECUTION_PROFILE.userPassword()), "page")
        .add("page", new Click("button[id='submitto']"), "page")
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene connect() {
    /*
,銀行ラベルを押す,,click,#js-navi-tab > li.active > a,,,
,【法人】楽天銀行を登録,,click,#tab1 > ul.account-list > li:nth-child(1) > a,,,
,,,wait,#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,displayed?,,true
,assert,,assert_text,#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,,eq,【法人】楽天銀行
,ID1入力,,send_key,#account_service_form_ID1,,,$account_service_form_id1
,PW1入力,,send_key,#account_service_form_PW1,,,$account_service_form_pw1
,submit,,click,#js-account-edit-form > div > input,,,
,assert,,assert_text,#alert-success > p,,eq,金融機関を登録しました。
    
     */
    /*
      await page.locator('#account_service_form_ID1').click();
  await page.locator('#account_service_form_ID1').fill('asdf');
  await page.locator('#account_service_form_PW1').click();
  await page.locator('#account_service_form_PW1').fill('asdf');
  await page.getByRole('button', { name: '連携登録' }).click();
     */
    return new Scene.Builder()
        .add("page", new Click(getByText("データ連携")), "page")
        .add("page", new Click(getLinkByName("新規登録")), "page")
        .add("page", new Click(getByText("銀行 (", true)), "page")
        .add("page", new Click(getByText("【個人】ゆうちょ銀行（投資信託）")), "page")
        .add("page", new Click("#account_service_form_ID1"), "page")
        .add("page", new SendKey("#account_service_form_ID1", EXECUTION_PROFILE.accountServiceId()), "page")
        .add("page", new Click("#account_service_form_PW1"), "page")
        .add("page", new SendKey("#account_service_form_PW1", EXECUTION_PROFILE.accountServicePassword()), "page")
        .add("page", new Click(getButtonByName("連携登録")), "page")
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene disconnect() {
    return new Scene.Builder()
        .add("page", new Click(p -> p.locator("#js-sidebar-opener").getByText("データ連携")), "page")
        .add("page", new Click((Page p) -> p.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("登録済一覧")).nth(1)), "page")
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene logout() {
    return new Scene.Builder()
        .add("page", new Click(getLinkByName("スペシャルサンドボックス合同会社 (法人)", true)),"page")
        .add("page", new Click(getLinkByName("ログアウト")), "page")
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene snapshot() {
    return new Scene.Builder()
        .add("NONE", new Screenshot("target/screenshot.png"), "page")
        .build();
  }
  
  @Named
  @DependsOn({
      @Parameter(name = "browser", sourceSceneName = "open", fieldNameInSourceScene = "browser"),
      @Parameter(name = "window", sourceSceneName = "open", fieldNameInSourceScene = "window"),
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page")}
  )
  public static Scene close() {
    return new Scene.Builder()
        .add("NONE", new Screenshot("target/screenshot.png"), "page")
        .add("NONE", new CloseBrowser(), "browser")
        .add("NONE", new CloseWindow(), "window")
        .build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
}
