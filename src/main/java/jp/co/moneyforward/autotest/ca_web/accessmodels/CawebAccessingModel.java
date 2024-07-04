package jp.co.moneyforward.autotest.ca_web.accessmodels;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.core.Context.Impl;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.ElementState;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Func;
import jp.co.moneyforward.autotest.framework.action.LeafAct.Let;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.action.Wait;
import jp.co.moneyforward.autotest.framework.annotations.ClosedBy;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn.Parameter;
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
   *
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
  public static Scene open() {
    TimeUnit timeUnit = SECONDS;
    int time = 30;
    return new Scene.Builder("NONE")
        .add("window", new Let<>(Playwright.create()))
        .add("browser", new Func<>("Playwright::chromium", (Playwright p) -> p.chromium().launch()), "window")
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
  @DependsOn(@Parameter(name = "page", sourceSceneName = "open"))
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
   * Returns a scenario to connect a back with a user account.
   *
   * @return A scenario to be performed.
   */
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "login"))
  public static Scene connectBank() {
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
        .assertion((Page p) -> value(p)
            .function(PageFunctions.getBySelector("#alert-success > p"))
            .function(LocatorFunctions.textContent())
            .toBe()
            .equalTo("金融機関を登録しました。"))
        // Could not come up with any better way than this...
        .add(new Wait<>(10, SECONDS, "no good way to make sure back registration is finished"))
        .build();
  }

  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene accessSimpleJournals() {
    return new Scene.Builder("page")
    .add(new PageAct("Access simple journals") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.getByText("手動で仕訳").hover();
        //assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("簡単入力@"))).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("簡単入力")).click();
        assertThat(page.locator("#js-ca-main-container").getByText("簡単入力", new Locator.GetByTextOptions().setExact(true))).isVisible();
      }
    })
    .build();
  }

  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "accessSimpleJournals", fieldNameInSourceScene = "page"))
  public static Scene createJournalInSimpleJournals() {
    return new Scene.Builder("page")
    .add(new PageAct("Create a journal with easy input") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator("#journal_recognized_at").click();
        page.locator("#journal_recognized_at").fill("05/15");
        page.locator("#journal_value").click();
        page.locator("#journal_value").fill("1111");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("登録")).click();
        
        // Let's make this a shared function.
        ElementHandle loader = page.querySelector(".ca-saving-cover");
        loader.waitForElementState(ElementState.HIDDEN);
        
        assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-recognized-at")).containsText( "05/15");
        assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-value")).containsText( "+1,111");
        assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-item")).containsText("現金 が増加して 現金 が減少した");

        page.locator(".ca-tr-emphasis").locator("a").click();
        page.onceDialog(dialog -> {
          System.out.println(String.format("Dialog message: %s", dialog.message()));
          dialog.dismiss();
        });
        page.getByText("削除", new Page.GetByTextOptions().setExact(true)).click();

        loader.waitForElementState(ElementState.HIDDEN);
        assertThat(page.locator("ca-tr-emphasis")).not().isAttached();
      }
    })
    .build();
  }
  
  /**
   * Performs an action to disconnect a bank from an account.
   *
   * @return A scenario to be performed.
   */
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "login"))
  public static Scene disconnectBank() {
   /*
      ,登録済一覧を開く,,get,$ca_accounts_url,,,
    ,,,sleep,,,,2
    ,【法人】北洋銀行を削除,,click,input.ca-btn-delete-icon,,,,
    ,モーダル表示待ち,,sleep,,,,1
    ,ダイアログ消去,,alert_accept,,,,
    ,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
    ,assert,,assert_text,td:nth-child(1),,match,.*【法人】ゆうちょ銀行（ゆうちょダイレクト）.*
    ,【法人】ゆうちょ（ゆうちょダイレクト）銀行を削除,,click,input.ca-btn-delete-icon,,,,
    ,モーダル表示待ち,,sleep,,,,1
    ,ダイアログ消去,,alert_accept,,,,
    ,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
    ,assert,,assert_text,td:nth-child(1),,match,.*【法人】楽天銀行.*
    ,【法人】楽天銀行を削除,,click,input.ca-btn-delete-icon,,,,
    ,モーダル表示待ち,,sleep,,,,1
    ,ダイアログ消去,,alert_accept,,,,
    ,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
     */
    return new Scene.Builder("page")
        .add(new Navigate(EXECUTION_PROFILE.accountsUrl()))
        .add(new PageAct("金融機関を削除する") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("\uF142")).locator("a").click();
            page.onceDialog(Dialog::accept);
            page.getByTestId("ca-client-test-account-dropdown-menu-delete-button")
                .click();
          }
        })
        .build();
  }
  
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "login"))
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
  
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "open"))
  public static Scene screenshot() {
    return new Scene.Builder("screenshot")
        .add("NONE", new Screenshot(), "page")
        .build();
  }
  
  @Named
  @DependsOn({
      @Parameter(name = "browser", sourceSceneName = "open"),
      @Parameter(name = "window", sourceSceneName = "open"),
      @Parameter(name = "page", sourceSceneName = "open")}
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
  
  private static Context getContext() {
    return new Impl() {
      @Override
      public Context createChild() {
        return this;
      }
    };
  }
}
