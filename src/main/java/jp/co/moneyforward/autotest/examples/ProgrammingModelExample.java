package jp.co.moneyforward.autotest.examples;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.core.Context.Impl;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
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

import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.byText;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;

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
        afterEach = {"screenshot"},
        afterAll = {"close"}))
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
    return new Scene.Builder("session")
        .add("window", new Let<>(Playwright.create()))
        .add("browser", new Func<>("Playwright::chromium", (Playwright p) -> p.chromium().launch()), "window")
        .add("page", new Func<>("Browser::newPage", (Browser b) -> b.newPage()), "browser")
        .build();
  }
  
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene login() {
    return new Scene.Builder("page")
        .add(new Navigate(EXECUTION_PROFILE.homeUrl()))
        .add(new SendKey(PageFunctions.getByPlaceholder("example@moneyforward.com"), EXECUTION_PROFILE.userEmail()))
        .add(new Click(getButtonByName("ログインする")))
        .add(new SendKey(PageFunctions.getByLabel("パスワード"), EXECUTION_PROFILE.userPassword()))
        .add(new Click("button[id='submitto']"))
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
    return new Scene.Builder("page")
        .add(new Click(getByText("データ連携")))
        .add(new Click(getLinkByName("新規登録")))
        .add(new Click(getByText("銀行 (", true)))
        .add(new Click(getByText("【個人】ゆうちょ銀行（投資信託）")))
        .add(new Click("#account_service_form_ID1"))
        .add(new SendKey("#account_service_form_ID1", EXECUTION_PROFILE.accountServiceId()))
        .add(new Click("#account_service_form_PW1"))
        .add(new SendKey("#account_service_form_PW1", EXECUTION_PROFILE.accountServicePassword()))
        .add(new Click(getButtonByName("連携登録")))
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene disconnect() {
    return new Scene.Builder("page")
        .add(new Click(getBySelector("#js-sidebar-opener").andThen(byText("データ連携"))))
        .add(new Click(getLinkByName("登録済一覧").andThen(LocatorFunctions.nth(1))))
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene logout() {
    return new Scene.Builder("page")
        .add(new Click(getLinkByName("スペシャルサンドボックス合同会社 (法人)", true)))
        .add(new Click(getLinkByName("ログアウト")))
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene screenshot() {
    return new Scene.Builder("page").add(new Screenshot()).build();
  }
  
  @Named
  @DependsOn({
      @Parameter(name = "browser", sourceSceneName = "open", fieldNameInSourceScene = "browser"),
      @Parameter(name = "window", sourceSceneName = "open", fieldNameInSourceScene = "window"),
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page")}
  )
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
}
