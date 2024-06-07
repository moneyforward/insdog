package jp.co.moneyforward.autotest.ut.misc;

import jp.co.moneyforward.autotest.ca_web.actions.gui.ConnectDataset;
import jp.co.moneyforward.autotest.ca_web.actions.gui.*;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.github.valid8j.fluent.Expectations.value;
import static com.github.valid8j.pcond.forms.Predicates.alwaysTrue;
import static com.github.valid8j.pcond.forms.Printables.predicate;


@ExtendWith(AutotestEngine.class)
public class AutotestExample {
  @BeforeAll
  public Scene login() {
    return new Scene.Builder().add("", new Navigate("http://ca.login.url"), "navigate")
                              .add("", null, "openLoginWindow")
                              .add("", new SendKey(), "email")
                              .add("", new SendKey(), "password")
                              .add("", new Click("text,eq,ログインする"), "clickLogin")
                              .add("", new WaitFor(), "waitForModalOpened")
                              .add("", new Click("a#btn-modal-close"), "clickCloseOnModal")
                              .add("", new WaitFor(), "waitForModalClosed")
                              .build();
  }
  
  @BeforeAll
  @DependsOn("login")
  public Scene connectDatasetAndRegisterAccounts() {
    return new Scene.Builder().add("", new ConnectDataset(), "connectDataset")
                              .add("", new WaitFor(), "waitForModalOpened")
                              .add("", new Click("a#btn-modal-close"), "clickCloseOnModal")
                              .add("", new WaitFor(), "waitForModalClosed")
                              .build();
  }
  
  @AutotestExecution
  public Scene registerBankAccountUnderApiPartnership_Rakuten() {
    return new Scene.Builder().add("", new Click("click,#js-navi-tab > li.active > a"), "clickBankLabel")
                              // "registerRakutenBank"
                              .add("",
                                   new Click("#tab1 > ul.account-list > li:nth-child(1) > a")
                                       .assertion(o -> value(o).satisfies()
                                                               .predicate(predicate("#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,,eq,【法人】楽天銀行",
                                                                                    alwaysTrue()))))
                              .add("", new SendKey(), "inputId1")
                              .add("", new SendKey(), "inputPw1")
                              .add("submit",
                                   new Click("#js-account-edit-form > div > input").assertion(
                                       o -> value(o).satisfies()
                                                    .predicate(predicate("#alert-success > p,,eq,金融機関を登録しました。",
                                                                         alwaysTrue()))))
                              .add("", new WaitFor(), "waitFor2seconds")
                              .build();
  }
  
  @AutotestExecution
  public Scene registerBankAccountUnderApiPartnership_Yuucho() {
    return new Scene.Builder().add("", new Click("click,#js-navi-tab > li.active > a"), "clickBankLabel")
                              .add("registerYuuchoBank",
                                   new Click("#tab1 > ul.account-list > li:nth-child(1) > a").assertion(
                                       o -> value(o).satisfies()
                                                    .predicate(predicate("#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,,eq,【法人】楽天銀行",
                                                                         alwaysTrue()))))
                              .add("", new SendKey(), "inputId1")
                              .add("", new SendKey(), "inputPw1")
                              .add("submit"
                                  , new Click("#js-account-edit-form > div > input").assertion(
                                      o -> value(o).satisfies()
                                                   .predicate(predicate("#alert-success > p,,eq,金融機関を登録しました。",
                                                                        alwaysTrue()))))
                              .add("", new WaitFor(), "waitFor2seconds")
                              .build();
  }
  
  @AutotestExecution
  public Scene registerBankAccountWithOnetimePassword_Hokuyo() {
    return new Scene.Builder().add("", new Get("$ca_accounts_service_list_url"), "")
                              //"#js-ca-main-contents > div.js-service-search.service-search > input"
                              //【法人】北洋銀行
                              .add("", new SendKey(), "enterCorporationInSearchBox")
                              .add("wait", new WaitFor().assertion(
                                  o -> value(o).satisfies()
                                               .predicate(predicate("/*#page-accounts > div.modal.fade.modal-accounts.js-mf-cloud-account-accounts-new-modal.in > div > div > div.modal-header > p,displayed?*/\n" +
                                                                        "                                                                           /*true*/", alwaysTrue()))))
                              .build();
  }
  
  @AutotestExecution
  @DependsOn("registerBankAccountWithOnetimePassword_Hokuyo")
  public Scene unregisterBankAccount_Hokuyo() {
    //    # ,【法人】北洋銀行の明細確認,,click,td:nth-child(8) > a,text,eq,閲覧
    //    #,,,sleep,,,,4
    //    #,assert,,assert_text,#js-acts-table-tbody > tr:nth-child(1) > td:nth-child(2),text,match,"2019/.*/01"
    //    #,登録済一覧を開く,,get,$ca_accounts_url,,,
    //    #,,,sleep,,,,2
    //    #,【法人】北洋銀行を削除,,click,input.ca-btn-delete-icon,,,,
    //    #,モーダル表示待ち,,sleep,,,,1
    //    #,ダイアログ消去,,alert_accept,,,,
    //    #,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
    //    #,assert,,assert_text,td:nth-child(1),,match,.*【法人】ゆうちょ銀行（ゆうちょダイレクト）.*
    //    #,【法人】ゆうちょ（ゆうちょダイレクト）銀行を削除,,click,input.ca-btn-delete-icon,,,,
    //    #,モーダル表示待ち,,sleep,,,,1
    //    #,ダイアログ消去,,alert_accept,,,,
    //    #,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
    //    #,assert,,assert_text,td:nth-child(1),,match,.*【法人】楽天銀行.*
    //    #,【法人】楽天銀行を削除,,click,input.ca-btn-delete-icon,,,,
    //    #,モーダル表示待ち,,sleep,,,,1
    //    #,ダイアログ消去,,alert_accept,,,,
    //    #,assert,,assert_text,#alert-success > p,,eq,金融機関を削除しました。
    return new Scene.Builder()
        // 【法人】北洋銀行の明細確認
        .add("", new Click("td:nth-child(8) > a,text,eq,閲覧"), "")
        // wait
        .add("", new WaitFor().assertion(
            o -> value(o).satisfies().predicate(predicate("#js-acts-table-tbody > tr:nth-child(1) > td:nth-child(2),text,match,\"2019/.*/01\"", alwaysTrue()))))
        .build();
  }
  
  @AutotestExecution
  @DependsOn("registerBankAccountUnderApiPartnership_Rakuten")
  public Scene unregisterBankAccount_Rakuten() {
    return new Scene.Builder()
        // 【法人】北洋銀行の明細確認
        .add("", new Click("td:nth-child(8) > a,text,eq,閲覧"), "")
        // wait
        .add("", new WaitFor().assertion(
            o -> value(o).satisfies().predicate(predicate("#js-acts-table-tbody > tr:nth-child(1) > td:nth-child(2),text,match,\"2019/.*/01\"", alwaysTrue()))))
        .build();
  }
  
  @AutotestExecution
  @DependsOn("registerBankAccountUnderApiPartnership_Yuucho")
  public Scene unregisterBankAccount_Yuucho() {
    return new Scene.Builder()
        .add("", new Click("td:nth-child(8) > a,text,eq,閲覧"), "【法人】北洋銀行の明細確認")
        .add("wait", new WaitFor().assertion(
            o -> value(o).satisfies().predicate(predicate("#js-acts-table-tbody > tr:nth-child(1) > td:nth-child(2),text,match,\"2019/.*/01\"", alwaysTrue()))))
        .build();
  }
  
  @AfterAll
  public Scene logout() {
    return new Scene.Builder().add("", new Click("a#dropdown-office"), "openMenuAtTop")
                              .add("", new Click("a,text,eq,ログアウト"), "clickLogout")
                              .build();
  }
  
  @Test
  public void test1() {}
}
