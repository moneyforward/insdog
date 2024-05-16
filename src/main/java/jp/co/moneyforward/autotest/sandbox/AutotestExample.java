package jp.co.moneyforward.autotest.sandbox;

import jp.co.moneyforward.autotest.ca_web.actions.gui.*;
import jp.co.moneyforward.autotest.framework.action.Play;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.ActionTest;

import static com.github.valid8j.fluent.Expectations.value;
import static com.github.valid8j.pcond.forms.Predicates.alwaysTrue;
import static com.github.valid8j.pcond.forms.Printables.predicate;


@ActionTest
public class AutotestExample {
  @ActionTest
  public Play example() {
    return new Play.Builder()
        .addMain(
            new Scene.Builder()
                .addScene("login",
                          new Scene.Builder().add("navigate", new Navigate("http://ca.login.url"))
                                             .add("openLoginWindow", null)
                                             .add("email", new SendKey())
                                             .add("password", new SendKey())
                                             .add("clickLogin", new Click("text,eq,ログインする"))
                                             .add("waitForModalOpened", new WaitFor())
                                             .add("clickCloseOnModal", new Click("a#btn-modal-close"))
                                             .add("waitForModalClosed", new WaitFor())
                                             .build())
                .addScene("connectDatasetAndRegisterAccounts",
                          new Scene.Builder().add("connectDataset", new ConnectDataset())
                                             .add("waitForModalOpened", new WaitFor())
                                             .add("clickCloseOnModal", new Click("a#btn-modal-close"))
                                             .add("waitForModalClosed", new WaitFor())
                                             .build())
                /* scripts/scenes/registerBankAccountUnderPartnershipi.yaml++:12 */
                .addScene("registerBankAccountUnderApiPartnership:【法人】楽天銀行",
                          new Scene.Builder().add("clickBankLabel", new Click("click,#js-navi-tab > li.active > a"))
                                             .addAct("registerRakutenBank",
                                                     new Click("#tab1 > ul.account-list > li:nth-child(1) > a"),
                                                     o -> value(o).satisfies()
                                                                  .predicate(predicate("#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,,eq,【法人】楽天銀行",
                                                                                       alwaysTrue())))
                                             .add("inputId1", new SendKey())
                                             .add("inputPw1", new SendKey())
                                             .addAct("submit",
                                                     new Click("#js-account-edit-form > div > input"),
                                                     o -> value(o).satisfies()
                                                                  .predicate(predicate("#alert-success > p,,eq,金融機関を登録しました。",
                                                                                       alwaysTrue())))
                                             .add("waitFor2seconds", new WaitFor())
                                             .build())
                .addScene("registerBankAccountUnderApiPartnership:YuuchoBank",
                          new Scene.Builder().add("clickBankLabel", new Click("click,#js-navi-tab > li.active > a"))
                                             .addAct("registerYuuchoBank",
                                                     new Click("#tab1 > ul.account-list > li:nth-child(1) > a"),
                                                     o -> value(o).satisfies()
                                                                  .predicate(predicate("#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,,eq,【法人】楽天銀行",
                                                                                       alwaysTrue())))
                                             .add("inputId1", new SendKey())
                                             .add("inputPw1", new SendKey())
                                             .addAct("submit",
                                                     new Click("#js-account-edit-form > div > input"),
                                                     o -> value(o).satisfies()
                                                                  .predicate(predicate("#alert-success > p,,eq,金融機関を登録しました。",
                                                                                       alwaysTrue())))
                                             .add("waitFor2seconds", new WaitFor())
                                             .build())
                .addScene("registerBankAccountWithOnetimePassword",
                          new Scene.Builder().add("", new Get("$ca_accounts_service_list_url"))
                                             //"#js-ca-main-contents > div.js-service-search.service-search > input"
                                             //【法人】北洋銀行
                                             .add("enterCorporationInSearchBox", new SendKey())
                                             .addAct("wait", new WaitFor(),
                                                     o -> value(o).satisfies()
                                                                  .predicate(predicate("/*#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,displayed?*/\n" +
                                                                                           "                                                                           /*true*/", alwaysTrue())))
                                             .build())
                .addScene("unregisterBankAccount",
                          new Scene.Builder()
                              .add("【法人】北洋銀行の明細確認", new Click("td:nth-child(8) > a,text,eq,閲覧"))
                              .addAct("wait", new WaitFor(),
                                      o -> value(o).satisfies().predicate(predicate("#js-acts-table-tbody > tr:nth-child(1) > td:nth-child(2),text,match,\"2019/.*/01\"", alwaysTrue())))
                              .build())
                .addScene("unregisterBankAccount",
                          new Scene.Builder().build())
                .addScene("unregisterBankAccount",
                          new Scene.Builder().build())
                .addScene("logout",
                          new Scene.Builder().add("openMenuAtTop", new Click("a#dropdown-office"))
                                             .add("clickLogout", new Click("a,text,eq,ログアウト"))
                                             .build())
                
                .build())
        .build();
  }
}
