package jp.co.moneyforward.autotest.ca_web.tests.pages;

import com.github.valid8j.pcond.core.fluent.builtins.ObjectChecker;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Click;
import jp.co.moneyforward.autotest.actions.web.Navigate;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import org.junit.jupiter.api.Tag;

import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.*;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;


/**
 * A smoke test that visits menu items in the home page of **ca_web**.
 * This test covers the sidebar menu items.
 */
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = DEPENDENCY_BASED,
        beforeEach = {
            "toHome", "screenshot"
        },
        value = {
            "自動で仕訳_連携サービスから入力",
            "自動で仕訳_請求書から入力",
            "自動で仕訳_経費・債務支払いから入力",
            "自動で仕訳_給与から入力",
            "手動で仕訳_振替伝票入力",
            "手動で仕訳_簡単入力",
            "手動で仕訳_仕訳帳入力",
            "手動で仕訳_取引から入力",
            "取引管理_債務管理",
            "会計帳簿_総勘定元帳_新形式(β)",
            "会計帳簿_総勘定元帳_旧形式",
            "会計帳簿_補助元帳_新形式(β)",
            "会計帳簿_残高試算表_貸借対照表",
            "会計帳簿_残高試算表_損益計算書",
            "会計帳簿_貸借対照表_推移表",
            "会計帳簿_帳簿管理",
            "レポート_費用レポート",
            "レポート_その他",
            "レポート_収益内訳",
            "レポート_キャッシュフロー",
            "決算・申告",
            "データ連携",
            "各種設定"
        },
        afterEach = {"screenshot"}))
public class VisitMenuItemsTest extends CawebAccessingModel {
  
  public static final String SELECTOR_FOR_BREADCRUMBS = "ul.ca-tab-large li.active a";
  public static final String SELECTOR_FOR_ACTIVE_SIMPLE_AUTOMATIC_JOURNAL_ENTRY_LINK = "ul.ca-tab li.active a[data-tab='simple']";
  public static final String SELECTOR_FOR_ACTIVE_COMPOUND_AUTOMATIC_JOURNAL_ENTRY_LINK = "ul.ca-tab li.active a[data-tab='compound']";
  
  @Named
  @DependsOn("login")
  public static Scene toHome() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .build();
  }
  
  @Named("自動で仕訳_連携サービスから入力")
  @DependsOn("login")
  public static Scene enterJournalAutomatically_fromLinkedService() {
    return new Scene.Builder("page")
        .with(
           /*
            # 1: visit.csv
            #,data_store読み込み,,,,,,
             ,自動で仕訳_連携サービスから入力_通常・カード他,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 連携サービスから入力"
             ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,通帳・カード他
            */
            b -> b.add(new Click(locatorByText("自動で仕訳")))
                  .add(new Click(linkLocatorByName("連携サービスから入力")))
                  .assertion((Page p) -> value(p).function(locatorBySelector(SELECTOR_FOR_BREADCRUMBS))
                                                 .function(textContent())
                                                 .toBe()
                                                 .equalTo("通帳・カード他")))
        .with(
           /*
            # 1-1
            ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_通常,,click,div[class='ca-table-header'] a[class='ca-caret-right'],text,eq,自動仕訳ルール
            ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_単一,,click,ul[class='ca-tab'] a[data-tab='simple'],text,eq,単一自動仕訳ルール
            ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='simple'],,eq,単一自動仕訳ルール
            */
            b -> b.add(new Click(locatorByText("自動仕訳ルール")))
                  .add(new Click(linkLocatorByText("単一自動仕訳ルール")))
                  .assertion((Page p) -> value(p).function(locatorBySelector(SELECTOR_FOR_ACTIVE_SIMPLE_AUTOMATIC_JOURNAL_ENTRY_LINK))
                                                 .function(textContent())
                                                 .toBe()
                                                 .equalTo("単一自動仕訳ルール")))
        .with(
            // 1-2
            /*
             # 1-2
              ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_複合,,click,ul[class='ca-tab'] a[data-tab='compound'],text,eq,複合自動仕訳ルール
              ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='compound'],,eq,複合自動仕訳ルール
             */
            b -> b.add(new Click(locatorByText("複合自動仕訳ルール")))
                  .assertion((Page p) -> value(p).function(locatorBySelector(SELECTOR_FOR_ACTIVE_COMPOUND_AUTOMATIC_JOURNAL_ENTRY_LINK))
                                                 .function(textContent())
                                                 .toBe()
                                                 .equalTo("複合自動仕訳ルール")))
        .with(
            /*
             # 1-3
             ,自動仕訳に戻る,,click,div[class='ca-table-header'] a[class='ca-caret-left'],text,eq,自動仕訳に戻る
             ,明示sleep,,sleep,,,,1.5
             ,自動で仕訳_連携サービスから入力_通常・カード他_登録済一覧,,click,div[class='ca-table-header'] a[class='ca-caret-right mf-ml10'],text,eq,登録済一覧
             ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
             */
            b -> b.add(new Click(locatorByText("自動仕訳に戻る")))
                  .add(new Click(linkLocatorByText(" 登録済一覧")))
                  .assertion((Page p) -> pageTitleIsEqualTo(p, "登録済一覧｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * # visit.csv
   * ,自動で仕訳_請求書から入力_仕訳候補,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 請求書から入力"
   * ,assert,,assert_title,,,eq,請求書から入力｜マネーフォワード クラウド会計
   *
   * @return A scene that executes the scenario described above.
   */
  @Named("自動で仕訳_請求書から入力")
  @DependsOn("login")
  public static Scene enterJournalAutomatically_fromInvoice() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("自動で仕訳")))
                    .add(new Click(linkLocatorByText("請求書から入力")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "請求書から入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * # visit.csv
   * ,自動で仕訳_経費・債務支払から入力_仕訳候補,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 経費・債務支払から入力"
   * ,assert,,assert_title,,,eq,経費・債務支払から入力｜マネーフォワード クラウド会計
   */
  @Named("自動で仕訳_経費・債務支払いから入力")
  @DependsOn("login")
  public static Scene enterJournalAutomatically_fromExpenseDebtPayment() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("自動で仕訳")))
                    .add(new Click(linkLocatorByText("経費・債務支払から入力")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "経費・債務支払から入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * # visit.csv
   * ,自動で仕訳_給与から入力,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 給与から入力"
   * ,assert,,assert_title,,,eq,給与から入力｜マネーフォワード クラウド会計
   */
  @Named("自動で仕訳_給与から入力")
  @DependsOn("login")
  public static Scene enterJournalAutomatically_fromSalaryPayment() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("自動で仕訳")))
                    .add(new Click(linkLocatorByText("給与から入力")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "給与から入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * ,手動で仕訳_振替伝票入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 振替伝票入力"
   * ,assert,,assert_title,,,eq,振替伝票入力｜マネーフォワード クラウド会計
   */
  @Named("手動で仕訳_振替伝票入力")
  @DependsOn("login")
  public static Scene enterJournalManually_fromTransferSlip() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("手動で仕訳")))
                    .add(new Click(locatorBySelector("#js-ca-main-contents").andThen(byText("振替伝票入力"))))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "振替伝票入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * ,手動で仕訳_簡単入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 簡単入力"
   * ,assert,,assert_title,,,eq,簡単入力｜マネーフォワード クラウド会計
   */
  @Named("手動で仕訳_簡単入力")
  @DependsOn("login")
  public static Scene enterJournalManually_withEasyInput() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("手動で仕訳")))
                    .add(new Click(linkLocatorByName("簡単入力")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "簡単入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * ,手動で仕訳_仕訳帳入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 仕訳帳入力"
   * ,assert,,assert_title,,,eq,仕訳帳入力｜マネーフォワード クラウド会計
   */
  @Named("手動で仕訳_仕訳帳入力")
  @DependsOn("login")
  public static Scene enterJournalManually_fromJournalBook() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("手動で仕訳")))
                    .add(new Click(linkLocatorByName("仕訳帳入力")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "仕訳帳入力｜マネーフォワード クラウド会計")))
        .build();
  }
  
  /**
   * 1.
   * ,手動で仕訳_取引から入力_収入,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 取引から入力"
   * ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収入
   *
   * 2.
   * ,手動で仕訳_取引から入力_収入_収入先の設定,,click,#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1),,,
   * ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a,,eq,収入先
   *
   * 3.
   * ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
   * ,手動で仕訳_取引から入力_収入_自動仕訳ルール_収入ルール,,click,a.ca-caret-right.mf-ml15,text,eq,自動仕訳ルール
   * ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.active > a,,eq,収入ルール
   *
   * 4.
   * ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
   * ,手動で仕訳_取引から入力_支出,,click,ul[class='ca-tab-large'] li a,text,eq,支出
   * ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,支出
   *
   * 5.
   * ,手動で仕訳_取引から入力_支出_支出先の設定,,click,#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1),,,
   * ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a,,eq,支出先
   * 　6.
   * ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
   * ,手動で仕訳_取引から入力_支出_自動仕訳ルール_支出ルール,,click,a.ca-caret-right.mf-ml15,text,eq,自動仕訳ルール
   * ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.active > a,,eq,支出ルール
   */
  @Named("手動で仕訳_取引から入力")
  @DependsOn("login")
  public static Scene enterJournalManually_fromTransaction_Income() {
    return new Scene.Builder("page")
        // 1. 手動で仕訳_取引から入力_収入
        .with(b -> b.add(new Click(locatorByText("手動で仕訳")))
                    .add(new Click(linkLocatorByName("取引から入力")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収入")))
        // 2. 手動で仕訳_取引から入力_収入_収入先の設定
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1)")))
                    .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a"))
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収入先")))
        // 3. 取引から入力に戻る -> 手動で仕訳_取引から入力_支出
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents > div.ca-table-header > div > a")))
                    .add(new Click(locatorBySelector("a.ca-caret-right.mf-ml15")))
                    .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container > ul > li.active > a"))
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収入ルール")))
        // 4. 取引から入力に戻る -> 手動で仕訳_取引から入力_支出
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents > div.ca-table-header > div > a")))
                    .add(new Click(locatorBySelector(selectorForLargeTab()).andThen(byText("支出"))))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent()).toBe().equalTo("支出")))
        // 5. 手動で仕訳_取引から入力_支出_支出先の設定
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1)")))
                    .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a"))
                                                   .function(textContent()).toBe().equalTo("支出先")))
        // 6. 取引から入力に戻る -> 手動で仕訳_取引から入力_支出_自動仕訳ルール_支出ルール
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents > div.ca-table-header > div > a")))
                    .add(new Click(locatorBySelector("a.ca-caret-right.mf-ml15")))
                    .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container > ul > li.active > a"))
                                                   .function(textContent()).toBe().equalTo("支出ルール")))
        .build();
  }
  
  private static Function<Page, Locator> locatorForActiveLargeTab() {
    return locatorBySelector(selectorForActiveLargeTab());
  }
  
  /**
   * ,取引管理_債務管理,,load_action_file,visit.csv,,,"menu: 取引管理, function: 債務管理"
   * ,,,sleep,,,,1
   * ,assert,,assert_text,#js-premium-modal-corporate-business > div > div > div.modal-body > div.text-center > p.js-premium-title.premium-title,,match,ビジネスプランへの.*
   */
  @Named("取引管理_債務管理")
  @DependsOn("login")
  public static Scene transactionManagement_debtManagement() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("取引管理")))
        .add(new Click(linkLocatorByText("債務管理")))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-premium-modal-corporate-business > div > div > div.modal-body > div.text-center"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("プラン"))
        .build();
  }
  
  @Named("会計帳簿_総勘定元帳_新形式(β)")
  @DependsOn("login")
  public static Scene whenOpenGeneralLedgersUrlAndClickNewFormatBeta_thenPageLoadedAndNewFormatActivated() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().generalLedgersUrl()))
        .assertion((Page p) -> value(p).function(toTitle())
                                       .asString()
                                       .toBe()
                                       .equalTo("総勘定元帳｜マネーフォワード クラウド会計"))
        .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                       .function(textContent())
                                       .toBe().equalTo("新形式(β)"))
        .build();
  }
  
  @Named("会計帳簿_総勘定元帳_旧形式")
  @DependsOn("login")
  public static Scene whenOpenGeneralLedgersUrlAndClickOldFormat_thenOldFormatActivated() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().generalLedgersUrl()))
        .add(new Click(locatorByText("旧形式")))
        .assertion((Page p) -> value(p).function(toTitle())
                                       .asString()
                                       .toBe()
                                       .equalTo("総勘定元帳｜マネーフォワード クラウド会計"))
        .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                       .function(textContent())
                                       .toBe().equalTo("旧形式"))
        .build();
  }
  
  @Named("会計帳簿_補助元帳_新形式(β)")
  @DependsOn("login")
  public static Scene whenOpenSubsidiaryLedgersUrlAndClickNewFormatBeta_thenPageLoadedAndNewFormatActivated() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().subsidiaryLedgersUrl()))
        .assertion((Page p) -> value(p).function(toTitle())
                                       .asString()
                                       .toBe()
                                       .equalTo("補助元帳｜マネーフォワード クラウド会計"))
        .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                       .function(textContent())
                                       .toBe().equalTo("新形式(β)"))
        .build();
  }
  
  @Named("会計帳簿_残高試算表_貸借対照表")
  @DependsOn("login")
  public static Scene accountingBooks_trialBalance_balanceSheet() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("会計帳簿")))
        .add(new Click(linkLocatorByText("残高試算表")))
        .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabforBalanceSheet()))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("貸借対照表"))
        .add(new Click(locatorBySelector(selectorForBookTab("pl"))))
        .assertion((Page p) -> pageTitleIsEqualTo(p, "残高試算表｜マネーフォワード クラウド会計"))
        .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabForProfitAndLoss()))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("損益計算書"))
        .build();
  }
  
  @Named("会計帳簿_残高試算表_損益計算書")
  @DependsOn("login")
  public static Scene accountingBooks_trialBalance_incomeStatementSheet() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("会計帳簿")))
        .add(new Click(linkLocatorByText("残高試算表")))
        .add(new Click(locatorBySelector(selectorForBookTab("pl"))))
        .assertion((Page p) -> pageTitleIsEqualTo(p, "残高試算表｜マネーフォワード クラウド会計"))
        .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabForProfitAndLoss()))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("損益計算書"))
        .build();
  }
  
  /*
        # ONGOING
        ,会計帳簿_推移表_月次_貸借対照表,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 推移表"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表

      #
        ,会計帳簿_推移表_月次_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_推移表_四半期_貸借対照表,,click,ul[class='ca-tab-large'] a,text,eq,四半期
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,四半期
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表

   */
  @Named("会計帳簿_貸借対照表_推移表")
  @DependsOn("login")
  public static Scene accountingBooks_transition() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("会計帳簿")))
                    .add(new Click(linkLocatorByText("推移表")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("月次"))
                    .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabforBalanceSheet()))
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("貸借対照表")))
        .with(b -> b.add(new Click(locatorByText("損益計算書")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("月次"))
                    .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabForProfitAndLoss()))
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("損益計算書")))
        .with(b -> b.add(new Click(locatorByText("四半期")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("四半期"))
                    .assertion((Page p) -> value(p).function(locatorBySelector(selectorForActiveBookTabforBalanceSheet()))
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("貸借対照表")))
        .build();
  }
  
  /*
        # DONE: $book_setting_url
        ,会計帳簿_帳簿管理,,get,$book_setting_url,,,
        ,会計帳簿_帳簿管理_仕訳一括削除,,click,ul[class='ca-tab-large'] li a,text,eq,仕訳一括削除
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,仕訳一括削除
   */
  @Named("会計帳簿_帳簿管理")
  @DependsOn("login")
  public static Scene accountingBooks_booksSetting() {
    return new Scene.Builder("page")
        .with(b -> b.add(new Click(locatorByText("会計帳簿")))
                    .add(new Click(linkLocatorByText("帳簿管理")))
                    .add(new Click(linkLocatorByText("仕訳一括削除")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("仕訳一括削除")))
        .build();
  }
  
  
  @Named("レポート_キャッシュフロー")
  @DependsOn("login")
  public static Scene visitReportItems_cashFlow() {
    return new Scene.Builder("page")
        /*
          ,レポート_キャッシュフローレポート,,load_action_file,visit.csv,,,"menu: レポート, function: キャッシュフローレポート"
          ,assert,,assert_title,,,eq,キャッシュフローレポート｜マネーフォワード クラウド会計
         */
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("キャッシュフローレポート")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "キャッシュフローレポート｜マネーフォワード クラウド会計")))
        .build();
  }
  
  @Named("レポート_収益内訳")
  @DependsOn("login")
  public static Scene visitReportItems_incomeBreakDown() {
    return new Scene.Builder("page")
        /*
         ,レポート_収益レポート_収益内訳,,load_action_file,visit.csv,,,"menu: レポート, function: 収益レポート"
         ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
         ,assert,,assert_title,,,eq,収益レポート｜マネーフォワード クラウド会計
         ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収益内訳
         */
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("収益レポート")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "収益レポート｜マネーフォワード クラウド会計"))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収益内訳")))
        /*
                ,レポート_収益レポート_月次推移,,click,ul[class='ca-tab-large'] li a,text,eq,月次推移
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,レポート_収益レポート_月次推移_勘定科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,勘定科目別
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0'],text,eq,勘定科目別

         */
        .with(b -> b.add(new Click(linkLocatorByText("月次推移")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("月次推移")))
        /*
            ,レポート_収益レポート_月次推移_補助科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,補助科目別
            ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
            ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
            ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='1'],text,eq,補助科目別
    
         */
        .with(b -> b.add(new Click(linkLocatorByText("収益詳細")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収益詳細")))
        .build();
  }
  
  @Named("レポート_費用レポート")
  @DependsOn("login")
  public static Scene visitReportItems_expenseReport() {
    return new Scene.Builder("page")
        // ,レポート_費用レポート_費用内訳,,load_action_file,visit.csv,,,"menu: レポート, function: 費用レポート"
        // ,assert,,assert_title,,,eq,費用レポート｜マネーフォワード クラウド会計
        // ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用内訳
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("費用レポート")))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("費用レポート｜マネーフォワード クラウド会計"))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("費用内訳")))
        //,レポート_費用レポート_月次推移,,click,ul[class='ca-tab-large'] li a,text,eq,月次推移
        //,20231222_random-fail発生のためsleep追加,,sleep,,,,9
        //,レポート_費用レポート_月次推移_勘定科目別,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,勘定科目別
        //,20231222_random-fail発生のためsleep追加,,sleep,,,,9
        //,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        //,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0'],text,eq,勘定科目別
        .with(b -> b.add(new Click(locatorBySelector(selectorForLargeTab()).andThen(byText("月次推移"))))
                    .add(new Click(locatorByText("勘定科目別")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("月次推移"))
                    .assertion((Page p) -> value(p).function(locatorBySelector("ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0']"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("勘定科目別")))
        // ,レポート_費用レポート_費用詳細,,click,ul[class='ca-tab-large'] li a,text,eq,費用詳細
        // ,レポート_費用レポート_費用詳細_実現済一覧,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,実現済一覧
        // ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用詳細
        // ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='0'],text,eq,実現済一覧
        .with(b -> b.add(new Click(locatorBySelector(selectorForLargeTab()).andThen(byText("費用詳細"))))
                    .add(new Click(locatorByText("実現済一覧")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("費用詳細"))
                    .assertion((Page p) -> value(p).function(locatorBySelector("ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0']"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("勘定科目別")))
        // ,レポート_費用レポート_費用詳細_未実現一覧,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,未実現一覧
        // ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用詳細
        // ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='1'],text,eq,未実現一覧
        .with(b -> b.add(new Click(locatorByText("未実現一覧")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("費用詳細"))
                    .assertion((Page p) -> value(p).function(locatorBySelector("ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='1']"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("未実現一覧")))
        .build();
  }
  
  @Named("レポート_その他")
  @DependsOn("login")
  public static Scene visitReportItems_misc() {
    return new Scene.Builder("page")
        /*
          ,レポート_収入先レポート,,load_action_file,visit.csv,,,"menu: レポート, function: 収入先レポート"
          ,assert,,assert_title,,,eq,収入先レポート｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("収入先レポート")))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("収入先レポート｜マネーフォワード クラウド会計")))
        /*
          ,レポート_支出先レポート,,load_action_file,visit.csv,,,"menu: レポート, function: 支出先レポート"
          ,assert,,assert_title,,,eq,支出先レポート｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("支出先レポート")))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("支出先レポート｜マネーフォワード クラウド会計")))
        /*
         ,レポート_財務指標(β)_グラフ,,load_action_file,visit.csv,,,"menu: レポート, function: 財務指標(β)"
         ,assert,,assert_title,,,eq,財務指標(β)｜マネーフォワード クラウド会計
         ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,グラフ
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("財務指標(β)")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "財務指標(β)｜マネーフォワード クラウド会計"))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("グラフ"))
        )
        /*
        ,レポート_財務指標(β)_実績値,,click,ul[class='ca-tab-large'] li a,text,eq,実績値
        ,assert,,assert_title,,,eq,財務指標(β)｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,実績値
         */
        .with(b -> b.add(new Click(locatorBySelector(selectorForLargeTab()).andThen(byText("実績値"))))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("財務指標(β)｜マネーフォワード クラウド会計")))
        .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .equalTo("実績値"))
        /*
          ,レポート_外部サービス,,load_action_file,visit.csv,,,"menu: レポート, function: 外部サービス"
          ,assert,,assert_title,,,eq,外部サービス｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("外部サービス")))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("外部サービス｜マネーフォワード クラウド会計")))
        .build();
  }
  
  
  @Named("レポート_収益詳細")
  @DependsOn("login")
  public static Scene visitReportItems_incomeDetail() {
    return new Scene.Builder("page")
        /*
          ,レポート_キャッシュフローレポート,,load_action_file,visit.csv,,,"menu: レポート, function: キャッシュフローレポート"
          ,assert,,assert_title,,,eq,キャッシュフローレポート｜マネーフォワード クラウド会計
          ,レポート_収益レポート_収益内訳,,load_action_file,visit.csv,,,"menu: レポート, function: 収益レポート"
          ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
          ,assert,,assert_title,,,eq,収益レポート｜マネーフォワード クラウド会計
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収益内訳
         */
        .with(b -> b.add(new Click(locatorByText("レポート")))
                    .add(new Click(linkLocatorByText("収益レポート")))
                    .add(new Click(linkLocatorByText("収益内訳")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収益内訳")))
        /*
          ,レポート_収益レポート_月次推移,,click,ul[class='ca-tab-large'] li a,text,eq,月次推移
          ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
          ,レポート_収益レポート_月次推移_勘定科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,勘定科目別
          ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
          ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0'],text,eq,勘定科目別
         */
        .with(b -> b.add(new Click(linkLocatorByText("月次推移")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("月次推移")))
        /*
            ,レポート_収益レポート_月次推移_補助科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,補助科目別
            ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
            ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
            ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='1'],text,eq,補助科目別
         */
        .with(b -> b.add(new Click(linkLocatorByText("収益詳細")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("収益詳細")))
        .build();
  }
  
  @Named("決算・申告")
  @DependsOn("login")
  public static Scene visitSettlementAndDeclaration() {
    return new Scene.Builder("page")
        /*
          ,決算・申告_固定資産台帳_固定資産の一覧,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 固定資産台帳"
          ,assert,,assert_title,,,eq,固定資産台帳｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("決算・申告")))
                    .add(new Click(linkLocatorByText("固定資産台帳")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "固定資産台帳｜マネーフォワード クラウド会計")))
        /*
          ,決算・申告_固定資産台帳_固定資産の追加,,click,ul[class='ca-tab-large'] li a,text,eq,固定資産の追加
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,固定資産の追加
         */
        .with(b -> b.add(new Click(linkLocatorByText("固定資産の追加")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("固定資産の追加")))
        /*
          ,決算・申告_消費税集計_勘定科目別税区分集計表,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 消費税集計"
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,勘定科目別税区分集計表
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("決算・申告")))
                    .add(new Click(linkLocatorByText("消費税集計")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("勘定科目別税区分集計表")))
        /*
          ,決算・申告_消費税集計_税区分集計表,,click,ul[class='ca-tab-large'] li a,text,eq,税区分集計表
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,税区分集計表
         */
        .with(b -> b.add(new Click(linkLocatorByExactText("税区分集計表")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("税区分集計表")))
        /*
          ,決算・申告_決算書_貸借対照表,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 決算書"
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,貸借対照表
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("決算・申告")))
                    .add(new Click(linkLocatorByText("決算書")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("貸借対照表")))
        /*
          ,決算・申告_決算書_損益計算書,,click,ul[class='ca-tab-large'] li a,text,eq,損益計算書
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,損益計算書
         */
        .with(b -> b.add(new Click(linkLocatorByText("損益計算書")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("損益計算書")))
        /*
          ,決算・申告_決算書_販管費内訳書,,click,ul[class='ca-tab-large'] li a,text,eq,販管費内訳書
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,販管費内訳書
         */
        .with(b -> b.add(new Click(linkLocatorByText("販管費内訳書")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("販管費内訳書")))
        /*
          ,決算・申告_決算書_株主資本等変動計算書,,click,ul[class='ca-tab-large'] li a,text,eq,株主資本等変動計算書
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,株主資本等変動計算書
         */
        .with(b -> b.add(new Click(linkLocatorByText("株主資本等変動計算書")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("株主資本等変動計算書")))
        /*
          ,決算・申告_決算書_個別注記表,,click,ul[class='ca-tab-large'] li a,text,eq,個別注記表
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,個別注記表
         */
        .with(b -> b.add(new Click(linkLocatorByText("個別注記表")))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .toBe()
                                                   .equalTo("個別注記表")))
        /*
          NOT FOUND ON ANY OF CA_WEB SCREEN, PERHAPS, REMOVED FEATURE?
          ,決算・申告_決算書_表紙,,click,ul[class='ca-tab-large'] li a,text,eq,表紙
          ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,表紙
         */
        /*
          ,決算・申告_達人シリーズ連携,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 達人シリーズ連携"
          ,assert,,assert_title,,,eq,達人シリーズ連携｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("決算・申告")))
                    .add(new Click(linkLocatorByText("達人シリーズ連携")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "達人シリーズ連携｜マネーフォワード クラウド会計")))
        /*
          ,決算・申告_次年度繰越,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 次年度繰越"
          ,assert,,assert_title,,,eq,次年度繰越｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(b -> b.add(new Click(locatorByText("決算・申告")))
                    .add(new Click(linkLocatorByText("次年度繰越")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "次年度繰越｜マネーフォワード クラウド会計")))
        .build();
  }
  
  @Named("データ連携")
  @DependsOn("login")
  public static Scene visitDataLinkItems() {
    return new Scene.Builder("page")
        /*
          ,データ連携_新規登録,,load_action_file,visit.csv,,,"menu: データ連携, function: 新規登録"
          ,モーダル表示待ち,,sleep,,,,1
          ,モーダル消去,,click,a#btn-modal-close,,,
          ,モーダル閉じ待ち,,sleep,,,,1.8
          ,assert,,assert_title,,,eq,新規登録｜マネーフォワード クラウド会計
         */
        .with(b -> b.add(new Click(locatorBySelector("#js-sidebar-opener").andThen(byText("データ連携"))))
                    .add(new Click(locatorBySelector("#js-ca-main-contents").andThen(byText("新規登録"))))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "新規登録｜マネーフォワード クラウド会計")))
        /*
          ,データ連携_登録済一覧,,load_action_file,visit.csv,,,"menu: データ連携, function: 登録済一覧"
          ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
          ,データ連携_登録済一覧_連携サービスの選択,,click,#js-ca-main-contents div > a,text,eq,連携サービスの選択
          ,assert,,assert_text,#js-ca-main-contents > div.ca-general-container.mf-mb10,,match,.*で使用する連携サービスを選択してください。.*
         */
        .with(b -> b.add(new Click(locatorBySelector("#js-sidebar-opener").andThen(byText("データ連携"))))
                    .add(new Click(linkLocatorByName("登録済一覧")))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "登録済一覧｜マネーフォワード クラウド会計")))
        /*
          ,データ連携_登録済一覧_連携サービスの選択,,click,#js-ca-main-contents div > a,text,eq,連携サービスの選択
          ,assert,,assert_text,#js-ca-main-contents > div.ca-general-container.mf-mb10,,match,.*で使用する連携サービスを選択してください。.*
        */
        .with(b -> b.add(new Click(locatorBySelector("#js-ca-main-contents div > a").andThen(byText("連携サービスの選択"))))
                    .assertion((Page p) -> value(p).function(toTitle())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("連携サービスの選択｜マネーフォワード クラウド会計")))
        .build();
  }
  
  @Named("各種設定")
  @DependsOn("login")
  public static Scene settingsItems() {
    return new Scene.Builder("page")
        /*
              ,各種設定_事業者,,load_action_file,visit.csv,,,"menu: 各種設定, function: 事業者"
              ,assert,,assert_title,,,eq,事業者｜マネーフォワード クラウド会計
         */
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "事業者")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "事業者｜マネーフォワード クラウド会計")))
        /*
              ,各種設定_開始残高,,load_action_file,visit.csv,,,"menu: 各種設定, function: 開始残高"
              ,assert,,assert_title,,,eq,開始残高｜マネーフォワード クラウド会計
              ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,開始残高
         */
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "開始残高")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "開始残高｜マネーフォワード クラウド会計"))
                .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                               .function(textContent())
                                               .asString()
                                               .toBe()
                                               .equalTo("開始残高")))
        /*
              ,各種設定_開始残高_部門別開始残高,,click,ul[class='ca-tab-large'] li a,text,eq,部門別開始残高
              ,assert,,assert_title,,,eq,開始残高｜マネーフォワード クラウド会計
              ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,部門別開始残高
         */
        .with(b -> b.add(new Click(locatorForLargeTab().andThen(byText("部門別開始残高"))))
                    .assertion((Page p) -> pageTitleIsEqualTo(p, "開始残高｜マネーフォワード クラウド会計"))
                    .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("部門別開始残高")))
        /*
              ,各種設定_勘定科目_通常入力用_貸借対照表,,load_action_file,visit.csv,,,"menu: 各種設定, function: 勘定科目"
              ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
        */
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "勘定科目")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "勘定科目｜マネーフォワード クラウド会計"))
                .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                               .function(textContent())
                                               .asString()
                                               .toBe()
                                               .equalTo("通常入力用")))
        /*
              ,各種設定_勘定科目_通常入力用_損益計算書,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,損益計算書
              ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
         */
        .with(b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-in-tab-large pull-left'] li a").andThen(byText("損益計算書"))))
                    .assertion((Page p) -> value(p).function(locatorBySelector("div[class='ca-navigation-container-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("通常入力用")))
        /*
              ,各種設定_勘定科目_通常入力用_製造原価報告書,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,製造原価報告書
              ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
         */
        /*
        .with(b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-in-tab-large pull-left'] li a").andThen(byText("製造原価報告書"))))
                    .assertion((Page p) -> value(p).function(locatorBySelector("div[class='ca-navigation-container-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("通常入力用")))
                                                   
         */
        /*
              ,貸借対照表に戻る,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,貸借対照表
              ,各種設定_勘定科目_簡単入力用_支出,,click,div[class='ca-navigation-container-large'] li a,text,eq,簡単入力用
              ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,簡単入力用
              ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a,,eq,支出
         */
        .with(b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-in-tab-large pull-left'] li a").andThen(byText("貸借対照表"))))
                    .add(new Click(locatorBySelector("div[class='ca-navigation-container-large'] li a").andThen(byText("簡単入力用"))))
                    .assertion((Page p) -> value(p).function(locatorBySelector("div[class='ca-navigation-container-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("簡単入力用"))
                    .assertion((Page p) -> value(p).function(locatorBySelector("ul[class='ca-tab-in-tab-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("支出"))
        )
        /*
              ,各種設定_勘定科目_簡単入力用_収入,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,収入
              ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,簡単入力用
              ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a,,eq,収入
         */
        .with(b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-in-tab-large'] li a").andThen(byText("収入"))))
                    .assertion((Page p) -> value(p).function(locatorBySelector("div[class='ca-navigation-container-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("簡単入力用"))
                    .assertion((Page p) -> value(p).function(locatorBySelector("ul[class='ca-tab-in-tab-large'] li[class='active'] a"))
                                                   .function(textContent())
                                                   .asString()
                                                   .toBe()
                                                   .equalTo("収入")
                    ))
        /*
              ,各種設定_税区分,,load_action_file,visit.csv,,,"menu: 各種設定, function: 税区分"
              ,assert,,assert_title,,,eq,税区分｜マネーフォワード クラウド会計
          */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "税区分")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "税区分｜マネーフォワード クラウド会計")))
        /*
         ,各種設定_部門,,load_action_file,visit.csv,,,"menu: 各種設定, function: 部門"
         ,assert,,assert_text,#js-ca-main-contents > div.ca-explanation.mf-mb10 > div,,eq,部門を設定することで、残高試算表や月次推移を部門別に分析することができます。部門を追加
         */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "部門")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "部門｜マネーフォワード クラウド会計"))
                .assertion((Page p) -> value(p).function(locatorForActiveLargeTab())
                                               .function(textContent())
                                               .asString()
                                               .toBe()
                                               .containing("部門の設定")))
        /*
          ,各種設定_部門_配賦基準の設定,,click,ul[class='ca-tab-large'] li a,text,eq,配賦基準の設定
          ,assert,,assert_text,#js-allocation-patterns > div > div,,eq,部門の設定をすると配賦基準の設定が利用可能になります
          */
        .with(
            b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-large'] li a").andThen(byText("配賦基準の設定"))))
                  .assertion((Page p) -> value(p).function(locatorBySelector("#js-allocation-patterns > div > div"))
                                                 .function(textContent())
                                                 .asString()
                                                 .toBe()
                                                 .equalTo("部門の設定をすると配賦基準の設定が利用可能になります")))
        /*
          ,各種設定_部門_配賦基準の設定,,click,ul[class='ca-tab-large'] li a,text,eq,配賦基準の設定
          ,assert,,assert_text,#js-ca-main-contents > div.ca-hint-container.mf-mb10 > div,,eq,配賦基準の設定をすると配賦基準の割当が利用可能になります
         */
        .with(
            b -> b.add(new Click(locatorBySelector("ul[class='ca-tab-large'] li a").andThen(byText("配賦基準の割当"))))
                  .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.ca-hint-container.mf-mb10 > div"))
                                                 .function(textContent())
                                                 .asString()
                                                 .toBe()
                                                 .equalTo("配賦基準の設定をすると配賦基準の割当が利用可能になります")))
        /*
          ,ホームに戻る,,click,div.sidebar-container a,text,eq,ホーム
          ,各種設定_タグ,,load_action_file,visit.csv,,,"menu: 各種設定, function: タグ"
          ,assert,,assert_title,,,eq,タグ｜マネーフォワード クラウド会計
         */
        .add(toHome())
        /*
        ,各種設定_摘要辞書,,load_action_file,visit.csv,,,"menu: 各種設定, function: 摘要辞書"
        ,assert,,assert_title,,,eq,摘要辞書｜マネーフォワード クラウド会計
         */
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "摘要辞書")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "摘要辞書｜マネーフォワード クラウド会計")))
        /*
          ,各種設定_仕訳辞書,,load_action_file,visit.csv,,,"menu: 各種設定, function: 仕訳辞書"
          ,assert,,assert_title,,,eq,仕訳辞書｜マネーフォワード クラウド会計
          */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "仕訳辞書")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "仕訳辞書｜マネーフォワード クラウド会計")))
        /*
        ,各種設定_仕訳辞書_新規作成,,click,a.ca-btn-default.ca-btn-size-xsmall.mf-ml10,text,eq,新規作成
        ,assert,,assert_title,,,eq,仕訳辞書の新規作成｜マネーフォワード クラウド会計
        */
        .with(
            b -> b.add(new Click(locatorBySelector("a.ca-btn-default.ca-btn-size-xsmall.mf-ml10").andThen(byText("新規作成"))))
                  .assertion((Page p) -> pageTitleIsEqualTo(p, "仕訳辞書の新規作成｜マネーフォワード クラウド会計")))
        /*
          ,各種設定_取引先_収入先,,load_action_file,visit.csv,,,"menu: 各種設定, function: 取引先"
          ,assert,,assert_title,,,eq,取引先一覧｜マネーフォワード クラウド会計
        */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "取引先")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "取引先一覧｜マネーフォワード クラウド会計")))
        /*
              ,各種設定_他社ソフトデータの移行,,load_action_file,visit.csv,,,"menu: 各種設定, function: 他社ソフトデータの移行"
              ,assert,,assert_text,#js-ca-main-contents > div.mf-text-large.mf-mb15,,eq,他社ソフトとのデータの移行ができます
         */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "各種設定", "他社ソフトデータの移行")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "他社ソフトデータの移行｜マネーフォワード クラウド会計"))
                .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.mf-text-large.mf-mb15"))
                                               .function(textContent())
                                               .asString()
                                               .toBe()
                                               .containing("他社ソフトとのデータの移行ができます")))
        /*
                  ,書類管理_ストレージ,,load_action_file,visit.csv,,,"menu: 書類管理, function: ストレージ"
                  ,assert,,assert_title,,,eq,ストレージ｜マネーフォワード クラウド会計
         */
        .add(toHome())
        .with(
            b -> clickMenuItemThenChild(b, "書類管理", "ストレージ")
                .assertion((Page p) -> pageTitleIsEqualTo(p, "ストレージ｜マネーフォワード クラウド会計")))
        .build();
  }
  
  private static Function<Page, Locator> locatorForLargeTab() {
    return locatorBySelector(selectorForLargeTab());
  }
  
  private static String selectorForLargeTab() {
    return "ul[class='ca-tab-large'] li a";
  }
  
  private static Scene.Builder clickMenuItemThenChild(Scene.Builder b, String sideMenuItem, String childItem) {
    return b.add(new Click(locatorBySelector("#js-sidebar-opener").andThen(byText(sideMenuItem))))
            .add(new Click(linkLocatorByName(childItem, false)));
  }
  
  private static ObjectChecker<Page, String> pageTitleIsEqualTo(Page p, String expectedPageTitle) {
    return value(p).function(toTitle())
                   .toBe()
                   .equalTo(expectedPageTitle);
  }
  
  
  private static String selectorForBookTab(String bookName) {
    return "ul[class*='js-book-tab-menu'] a[data-activate-tab-content='" + bookName + "']";
  }
  
  private static String selectorForActiveBookTabForProfitAndLoss() {
    return selectorForActiveBookTab("pl");
  }
  
  private static String selectorForActiveBookTabforBalanceSheet() {
    return selectorForActiveBookTab("bs");
  }
  
  private static String selectorForActiveBookTab(String bookName) {
    return "ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='" + bookName + "']";
  }
  
  private static String selectorForActiveLargeTab() {
    return "ul[class='ca-tab-large'] li[class='active'] a";
  }
}
