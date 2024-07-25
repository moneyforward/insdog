package jp.co.moneyforward.autotest.ca_web.tests.bankaccount;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.action.Wait;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import static com.github.valid8j.fluent.Expectations.value;
import static java.util.concurrent.TimeUnit.SECONDS;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;

/**
 * This is a test class for "Data Linkage" feature of **ca_web**.
 *
 * ## Preconditions
 *
 * This test assumes the account returned by the profile is "clean".
 * That is:
 *
 * - it can log in to the SUT with its password
 * - the account specified by `ExecutionProfile#userEmail` should be belonging to a company named "スペシャルサンドボックス合同会社 (法人)".
 *
 *
 * ## Fixing a broken test
 *
 * This test breaks in a staging environment, when a partnership with a financial institution, which this test uses, terminates.
 * On such an occasion, you should follow the following steps.
 *
 * Visit `ホーム > データ > 連携新規登録` (Home > Data > New Linkage Registration) screen, then click `銀行（...）` (banks(...)).
 *
 * In this *first* page, find out an institution which pops up a small screen to have you enter connecting information such as login ID and password.
 * A list of institutions we have partnership with can be found out in a page [1].
 * In case, you don't have a permission for the page, check [2] and ask a help.
 *
 * - [1] [developer_tools/demo_aggregates](https://accounting-aweb-stg1.ebisubook.com/section9/developer_tools/demo_aggregates)
 * - [2] [会計専用管理画面(ca_aweb)の運用の手引 #ja](https://moneyforward.kibe.la/notes/161219)
 *
 * @see ExecutionProfile#userEmail()
 * @see ExecutionProfile#userPassword()
 * @see ExecutionProfile#accountServiceId()
 * @see CawebAccessingModel
 *
 */
@Tag("bank")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {"connectBank", "disconnectBank"},
        afterEach = {"screenshot"}
    ))
public class BankLinkageTest extends CawebAccessingModel {
  /**
   * Returns a scenario to connect a back with a user account.
   *
   * @return A scenario to be performed.
   */
  @Named
  @Export("page")
  @DependsOn("login")
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
        .add(new Click(getByText("【個人】紀陽銀行（インターネット支店）")))
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
  
  /**
   * Performs an action to disconnect a bank from an account.
   *
   * @return A scenario to be performed.
   */
  @Named
  @Export("page")
  @DependsOn("login")
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
}
