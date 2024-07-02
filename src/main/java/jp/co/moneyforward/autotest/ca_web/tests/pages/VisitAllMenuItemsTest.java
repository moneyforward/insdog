package jp.co.moneyforward.autotest.ca_web.tests.pages;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Click;
import jp.co.moneyforward.autotest.actions.web.LocatorFunctions;
import jp.co.moneyforward.autotest.actions.web.PageFunctions;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import org.junit.jupiter.api.Tag;

import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.byText;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;


@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "自動で仕訳_連携サービスから入力", "logout"},
        afterEach = {"screenshot"},
        afterAll = {"close"}))
public class VisitAllMenuItemsTest extends CawebAccessingModel {
  @Named("自動で仕訳_連携サービスから入力")
  @DependsOn(
      @DependsOn.Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene visitPagesUnderAutomaticJournalEntry() {
    return new Scene.Builder("page")
        /*
         # 1: visit.csv
         #,data_store読み込み,,,,,,
         ,自動で仕訳_連携サービスから入力_通常・カード他,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 連携サービスから入力"
         ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,通帳・カード他
          */
        .add(new Click(getByText("自動で仕訳")))
        .add(new Click(getLinkByName("連携サービスから入力")))
        //.add(new Click(getLinkByName("この説明をスキップ")))
        .assertion((Page p) -> value(p).function(PageFunctions.getBySelector("ul.ca-tab-large li.active a"))
                                       .function(LocatorFunctions.textContent())
                                       .toBe()
                                       .equalTo("通帳・カード他"))
        /*
         # 1-1
         ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_通常,,click,div[class='ca-table-header'] a[class='ca-caret-right'],text,eq,自動仕訳ルール
         ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_単一,,click,ul[class='ca-tab'] a[data-tab='simple'],text,eq,単一自動仕訳ルール
         ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='simple'],,eq,単一自動仕訳ルール
         */
        .add(new Click(getByText("自動仕訳ルール")))
        .add(new Click(getByText("単一自動仕訳ルール")))
        .assertion((Page p) -> value(p).function(PageFunctions.getBySelector("ul.ca-tab li.active a[data-tab='simple']"))
                                       .function(LocatorFunctions.textContent())
                                       .toBe()
                                       .equalTo("単一自動仕訳ルール"))
        // 1-2
        /*
         # 1-2
          ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_複合,,click,ul[class='ca-tab'] a[data-tab='compound'],text,eq,複合自動仕訳ルール
          ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='compound'],,eq,複合自動仕訳ルール
         */
        .add(new Click(getByText("複合自動仕訳ルール")))
        .assertion((Page p) -> value(p).function(PageFunctions.getBySelector("ul.ca-tab li.active a[data-tab='compound']"))
                                       .function(LocatorFunctions.textContent())
                                       .toBe()
                                       .equalTo("複合自動仕訳ルール"))
        /*
          # 1-3
          ,自動仕訳に戻る,,click,div[class='ca-table-header'] a[class='ca-caret-left'],text,eq,自動仕訳に戻る
          ,明示sleep,,sleep,,,,1.5
          ,自動で仕訳_連携サービスから入力_通常・カード他_登録済一覧,,click,div[class='ca-table-header'] a[class='ca-caret-right mf-ml10'],text,eq,登録済一覧
          ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
         */
        .add(new Click(getByText("自動仕訳に戻る")))
        //.add(new Click(getLinkByName("この説明をスキップ")))
        .add(new Click(getBySelector("div.ca-table-header a.ca-caret-right.mf-ml10").andThen(byText("登録済一覧"))))
        .assertion((Page p) -> value(p).function(PageFunctions.getTitle())
                                       .toBe()
                                       .equalTo("登録済一覧｜マネーフォワード クラウド会計"))
        .build();
  }
  
  @SuppressWarnings("unused")
  private static final String ORIGINAL_DAKENKUN_SCRIPT = """
      # comment1,comment2,comment3,*action,*what,attribute,matcher,value
      # content of 'visit.csv'
        # comment1,comment2,comment3,*action,*what,attribute,matcher,value
        メニュー,,メニュークリック,click,span,text,eq,$menu
        ,,,sleep,,,,1.5
        ,,機能名称クリック,click,a,text,eq,$function
        ,,,sleep,,,,1.5
      # DONE: 1: visit.csv
        #,data_store読み込み,,,,,,
        ,自動で仕訳_連携サービスから入力_通常・カード他,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 連携サービスから入力"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,通帳・カード他
        ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_通常,,click,div[class='ca-table-header'] a[class='ca-caret-right'],text,eq,自動仕訳ルール
        ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_単一,,click,ul[class='ca-tab'] a[data-tab='simple'],text,eq,単一自動仕訳ルール
        ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='simple'],,eq,単一自動仕訳ルール
        ,自動で仕訳_連携サービスから入力_通常・カード他_自動仕訳ルール_複合,,click,ul[class='ca-tab'] a[data-tab='compound'],text,eq,複合自動仕訳ルール
        ,assert,,assert_text,ul[class='ca-tab'] li[class='active'] a[data-tab='compound'],,eq,複合自動仕訳ルール
        ,自動仕訳に戻る,,click,div[class='ca-table-header'] a[class='ca-caret-left'],text,eq,自動仕訳に戻る
        ,明示sleep,,sleep,,,,1.5
        ,自動で仕訳_連携サービスから入力_通常・カード他_登録済一覧,,click,div[class='ca-table-header'] a[class='ca-caret-right mf-ml10'],text,eq,登録済一覧
        ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
      # visit.csv
        ,自動で仕訳_連携サービスから入力_通常・カード他,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 連携サービスから入力"
        ,自動で仕訳_連携サービスから入力_ビジネスカテゴリ,,click,ul[class='ca-tab-large'] li a,text,eq,ビジネスカテゴリ
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,ビジネスカテゴリ
        ,自動で仕訳_連携サービスから入力_ビジネスカテゴリ_登録済一覧,,click,div[class='ca-table-header'] a[class='ca-caret-right mf-ml10'],text,eq,登録済一覧
        ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
      # visit.csv
        ,自動で仕訳_請求書から入力_仕訳候補,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 請求書から入力"
        ,assert,,assert_title,,,eq,請求書から入力｜マネーフォワード クラウド会計
      # visit.csv
        ,自動で仕訳_給与から入力,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 給与から入力"
        ,assert,,assert_title,,,eq,給与から入力｜マネーフォワード クラウド会計
      # visit.csv
        ,自動で仕訳_経費・債務支払から入力_仕訳候補,,load_action_file,visit.csv,,,"menu: 自動で仕訳, function: 経費・債務支払から入力"
        ,assert,,assert_title,,,eq,経費・債務支払から入力｜マネーフォワード クラウド会計
      # visit.csv
        ,手動で仕訳_振替伝票入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 振替伝票入力"
        ,assert,,assert_title,,,eq,振替伝票入力｜マネーフォワード クラウド会計
      # visit.csv
        ,手動で仕訳_簡単入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 簡単入力"
        ,assert,,assert_title,,,eq,簡単入力｜マネーフォワード クラウド会計
      # visit.csv
        ,手動で仕訳_仕訳帳入力,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 仕訳帳入力"
        ,assert,,assert_title,,,eq,仕訳帳入力｜マネーフォワード クラウド会計
      # visit.csv
        ,手動で仕訳_取引から入力_収入,,load_action_file,visit.csv,,,"menu: 手動で仕訳, function: 取引から入力"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収入
        ,手動で仕訳_取引から入力_収入_収入先の設定,,click,#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1),,,
        ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a,,eq,収入先
        ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
        ,手動で仕訳_取引から入力_収入_自動仕訳ルール_収入ルール,,click,a.ca-caret-right.mf-ml15,text,eq,自動仕訳ルール
        ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.active > a,,eq,収入ルール
        ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
        ,手動で仕訳_取引から入力_支出,,click,ul[class='ca-tab-large'] li a,text,eq,支出
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,支出
        ,手動で仕訳_取引から入力_支出_支出先の設定,,click,#js-ca-main-contents > div:nth-child(6) > div > a:nth-child(1),,,
        ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.is-active > a,,eq,支出先
        ,取引から入力に戻る,,click,#js-ca-main-contents > div.ca-table-header > div > a,,,
        ,手動で仕訳_取引から入力_支出_自動仕訳ルール_支出ルール,,click,a.ca-caret-right.mf-ml15,text,eq,自動仕訳ルール
        ,assert,,assert_text,#js-ca-main-container > div.ca-navigation-container > ul > li.active > a,,eq,支出ルール
      # visit.csv
        ,取引管理_債務管理,,load_action_file,visit.csv,,,"menu: 取引管理, function: 債務管理"
        ,,,sleep,,,,1
        ,assert,,assert_text,#js-premium-modal-corporate-business > div > div > div.modal-body > div.text-center > p.js-premium-title.premium-title,,match,ビジネスプランへの.*
      # visit.csv
        ,よくわからないけどモーダルの閉じるボタンが押せないので一旦homeに戻る,,get,$home_url,,,
        ,会計帳簿_仕訳帳,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 仕訳帳"
        ,assert,,assert_title,,,eq,仕訳帳｜マネーフォワード クラウド会計
      # $books_overlap_check_url
        ,会計帳簿_仕訳帳_重複チェック画面,,get,$books_overlap_check_url,,,
        ,assert,,assert_title,,,eq,重複チェック｜マネーフォワード クラウド会計
        ,仕訳帳に戻る,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 仕訳帳"
        ,会計帳簿_仕訳帳_一括編集,,click,button.js-bulk-update-search-button.ca-btn-default.ca-btn-size-xsmall.mf-ml10,text,eq,一括編集
        ,assert,,assert_title,,,eq,一括編集｜マネーフォワード クラウド会計
        ,会計帳簿_現預金出納帳,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 現預金出納帳"
        ,assert,,assert_title,,,eq,現預金出納帳｜マネーフォワード クラウド会計
      # $general_ledgers_url (cont.)
        ,会計帳簿_総勘定元帳_新形式(β),,get,$general_ledgers_url,,,
        ,,,sleep,,,,4
        ,assert,,assert_title,,,eq,総勘定元帳｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,新形式(β)
      # $general_ledgers_url
        ,会計帳簿_総勘定元帳_旧型式,,get,$books_general_ledger_url,,,
        ,,,sleep,,,,2
        ,assert,,assert_title,,,eq,総勘定元帳｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,旧形式
      # $books_subsidiary_ledger_url
      ,会計帳簿_補助元帳_旧形式,,get,$books_subsidiary_ledger_url,,,
      ,,,sleep,,,,2
      ,assert,,assert_title,,,eq,補助元帳｜マネーフォワード クラウド会計
      ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,旧形式
      
      # $subsidiary_ledgers_url
        ,会計帳簿_補助元帳_新形式(β),,get,$subsidiary_ledgers_url,,,
        ,,,sleep,,,,2
        ,assert,,assert_title,,,eq,補助元帳｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,新形式(β)
        # load_action_file,visit.csv
        ,会計帳簿_残高試算表_貸借対照表,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 残高試算表"
        ,assert,,assert_title,,,eq,残高試算表｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表
        ,会計帳簿_残高試算表_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_title,,,eq,残高試算表｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_推移表_月次_貸借対照表,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 推移表"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表
        ,会計帳簿_推移表_月次_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_推移表_四半期_貸借対照表,,click,ul[class='ca-tab-large'] a,text,eq,四半期
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,四半期
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表
        ,会計帳簿_推移表_四半期_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,四半期
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_推移表_半期_貸借対照表,,click,ul[class='ca-tab-large'] a,text,eq,半期
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,半期
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表
        ,会計帳簿_推移表_半期_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,半期
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_推移表_年次_貸借対照表,,click,ul[class='ca-tab-large'] a,text,eq,年次
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,年次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='bs'],text,eq,貸借対照表
        ,会計帳簿_推移表_年次_損益計算書,,click,ul[class*='js-book-tab-menu'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,年次
        ,assert,,assert_text,ul[class*='js-book-tab-menu'] li[class='active'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,会計帳簿_部門別集計表_貸借対照表,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 部門別集計表"
        ,assert,,assert_title,,,eq,部門別集計表｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a[data-activate-tab-content='bs'],,eq,貸借対照表
        ,会計帳簿_部門別集計表_損益計算書,,click,ul[class='ca-tab-large'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_title,,,eq,部門別集計表｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a[data-activate-tab-content='pl'],,eq,損益計算書
        ,会計帳簿_前期比較_貸借対照表,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 前期比較"
        ,assert,,assert_title,,,eq,前期比較｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a[data-activate-tab-content='bs'],,eq,貸借対照表
        ,会計帳簿_前期比較_損益計算書,,click,ul[class='ca-tab-large'] a[data-activate-tab-content='pl'],text,eq,損益計算書
        ,assert,,assert_title,,,eq,前期比較｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a[data-activate-tab-content='pl'],,eq,損益計算書
        ,会計帳簿_帳簿管理_仕訳入力の期間制限,,load_action_file,visit.csv,,,"menu: 会計帳簿, function: 帳簿管理"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,仕訳入力の期間制限
        ,会計帳簿_帳簿管理_取引No.の振り直し,,click,ul[class='ca-tab-large'] li a,text,eq,取引No.の振り直し
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,取引No.の振り直し
        ,,,sleep,,,,1.5
        ,保存ボタンを押してモーダルを出す,,click,#tab-renumber-journal-number > dd > form > input.ca-btn-save.ca-btn-size-xsmall,,,
        ,ダイアログ消去,,alert_accept,,,,
        ,,,sleep,,,,1.5
        ,assert,,assert_title,,,eq,帳簿管理｜マネーフォワード クラウド会計
        ,,,sleep,,,,1.5
        ,assert,,assert_text,#js-premium-modal-corporate-business > div > div > div.modal-body > div.text-center > p.js-premium-title.premium-title,,match,ビジネスプランへの.*
      # $book_setting_url
        ,会計帳簿_帳簿管理,,get,$book_setting_url,,,
        ,会計帳簿_帳簿管理_仕訳一括削除,,click,ul[class='ca-tab-large'] li a,text,eq,仕訳一括削除
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,仕訳一括削除
        ,レポート_キャッシュフローレポート,,load_action_file,visit.csv,,,"menu: レポート, function: キャッシュフローレポート"
        ,assert,,assert_title,,,eq,キャッシュフローレポート｜マネーフォワード クラウド会計
        ,レポート_収益レポート_収益内訳,,load_action_file,visit.csv,,,"menu: レポート, function: 収益レポート"
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,assert,,assert_title,,,eq,収益レポート｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収益内訳
        ,レポート_収益レポート_月次推移,,click,ul[class='ca-tab-large'] li a,text,eq,月次推移
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,レポート_収益レポート_月次推移_勘定科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,勘定科目別
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0'],text,eq,勘定科目別
        ,レポート_収益レポート_月次推移_補助科目別,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,補助科目別
        ,20231221_random-fail発生のためsleep追加,,sleep,,,,3.5
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='1'],text,eq,補助科目別
        ,レポート_収益レポート_収益詳細,,click,ul[class='ca-tab-large'] li a,text,eq,収益詳細
        ,レポート_収益レポート_収益詳細_実現済一覧,,click,ul[class='ca-tab-in-tab-large'] a,text,eq,実現済一覧
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収益詳細
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='0'],text,eq,実現済一覧
        ,レポート_収益レポート_収益詳細_未実現一覧,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,未実現一覧
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,収益詳細
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='1'],text,eq,未実現一覧
        ,レポート_費用レポート_費用内訳,,load_action_file,visit.csv,,,"menu: レポート, function: 費用レポート"
        ,assert,,assert_title,,,eq,費用レポート｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用内訳
        ,レポート_費用レポート_月次推移,,click,ul[class='ca-tab-large'] li a,text,eq,月次推移
        ,20231222_random-fail発生のためsleep追加,,sleep,,,,9
        ,レポート_費用レポート_月次推移_勘定科目別,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,勘定科目別
        ,20231222_random-fail発生のためsleep追加,,sleep,,,,9
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='0'],text,eq,勘定科目別
        ,レポート_費用レポート_月次推移_補助科目別,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,補助科目別
        ,20231222_random-fail発生のためsleep追加,,sleep,,,,5
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,月次推移
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-transaction-item'][data-id='1'],text,eq,補助科目別
        ,レポート_費用レポート_費用詳細,,click,ul[class='ca-tab-large'] li a,text,eq,費用詳細
        ,レポート_費用レポート_費用詳細_実現済一覧,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,実現済一覧
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用詳細
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='0'],text,eq,実現済一覧
        ,レポート_費用レポート_費用詳細_未実現一覧,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,未実現一覧
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,費用詳細
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a[class='js-pl-detail'][data-id='1'],text,eq,未実現一覧
        ,レポート_収入先レポート,,load_action_file,visit.csv,,,"menu: レポート, function: 収入先レポート"
        ,assert,,assert_title,,,eq,収入先レポート｜マネーフォワード クラウド会計
        ,レポート_支出先レポート,,load_action_file,visit.csv,,,"menu: レポート, function: 支出先レポート"
        ,assert,,assert_title,,,eq,支出先レポート｜マネーフォワード クラウド会計
        ,レポート_財務指標(β)_グラフ,,load_action_file,visit.csv,,,"menu: レポート, function: 財務指標(β)"
        ,assert,,assert_title,,,eq,財務指標(β)｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,グラフ
        ,レポート_財務指標(β)_実績値,,click,ul[class='ca-tab-large'] li a,text,eq,実績値
        ,assert,,assert_title,,,eq,財務指標(β)｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,実績値
        ,レポート_外部サービス,,load_action_file,visit.csv,,,"menu: レポート, function: 外部サービス"
        ,assert,,assert_title,,,eq,外部サービス｜マネーフォワード クラウド会計
        ,決算・申告_固定資産台帳_固定資産の一覧,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 固定資産台帳"
        ,assert,,assert_title,,,eq,固定資産台帳｜マネーフォワード クラウド会計
        ,決算・申告_固定資産台帳_固定資産の追加,,click,ul[class='ca-tab-large'] li a,text,eq,固定資産の追加
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,固定資産の追加
        ,決算・申告_消費税集計_勘定科目別税区分集計表,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 消費税集計"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,勘定科目別税区分集計表
        ,決算・申告_消費税集計_税区分集計表,,click,ul[class='ca-tab-large'] li a,text,eq,税区分集計表
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,税区分集計表
        ,決算・申告_決算書_貸借対照表,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 決算書"
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,貸借対照表
        ,決算・申告_決算書_損益計算書,,click,ul[class='ca-tab-large'] li a,text,eq,損益計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,損益計算書
        ,決算・申告_決算書_販管費内訳書,,click,ul[class='ca-tab-large'] li a,text,eq,販管費内訳書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,販管費内訳書
        ,決算・申告_決算書_製造原価報告書,,click,ul[class='ca-tab-large'] li a,text,eq,製造原価報告書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,製造原価報告書
        ,決算・申告_決算書_株主資本等変動計算書,,click,ul[class='ca-tab-large'] li a,text,eq,株主資本等変動計算書
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,株主資本等変動計算書
        ,決算・申告_決算書_個別注記表,,click,ul[class='ca-tab-large'] li a,text,eq,個別注記表
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,個別注記表
        ,決算・申告_決算書_表紙,,click,ul[class='ca-tab-large'] li a,text,eq,表紙
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,表紙
        ,決算・申告_達人シリーズ連携,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 達人シリーズ連携"
        ,assert,,assert_title,,,eq,達人シリーズ連携｜マネーフォワード クラウド会計
        ,決算・申告_次年度繰越,,load_action_file,visit.csv,,,"menu: 決算・申告, function: 次年度繰越"
        ,assert,,assert_title,,,eq,次年度繰越｜マネーフォワード クラウド会計
        ,データ連携_新規登録,,load_action_file,visit.csv,,,"menu: データ連携, function: 新規登録"
        ,モーダル表示待ち,,sleep,,,,1
        ,モーダル消去,,click,a#btn-modal-close,,,
        ,モーダル閉じ待ち,,sleep,,,,1.8
        ,assert,,assert_title,,,eq,新規登録｜マネーフォワード クラウド会計
        ,データ連携_登録済一覧,,load_action_file,visit.csv,,,"menu: データ連携, function: 登録済一覧"
        ,assert,,assert_title,,,eq,登録済一覧｜マネーフォワード クラウド会計
        ,データ連携_登録済一覧_連携サービスの選択,,click,#js-ca-main-contents div > a,text,eq,連携サービスの選択
        ,assert,,assert_text,#js-ca-main-contents > div.ca-general-container.mf-mb10,,match,.*で使用する連携サービスを選択してください。.*
      # $accounts_trans_list_url
        ,データ連携_登録済一覧_明細一覧,,get,$accounts_trans_list_url,,,
        ,assert,,assert_title,,,eq,明細一覧｜マネーフォワード クラウド会計
        ,データ連携_電子証明書連携ソフト,,load_action_file,visit.csv,,,"menu: データ連携, function: 電子証明書連携ソフト"
        ,assert,,assert_title,,,eq,電子証明書連携ソフト｜マネーフォワード クラウド会計
        ,各種設定_事業者,,load_action_file,visit.csv,,,"menu: 各種設定, function: 事業者"
        ,assert,,assert_title,,,eq,事業者｜マネーフォワード クラウド会計
        ,各種設定_開始残高,,load_action_file,visit.csv,,,"menu: 各種設定, function: 開始残高"
        ,assert,,assert_title,,,eq,開始残高｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,開始残高
        ,各種設定_開始残高_部門別開始残高,,click,ul[class='ca-tab-large'] li a,text,eq,部門別開始残高
        ,assert,,assert_title,,,eq,開始残高｜マネーフォワード クラウド会計
        ,assert,,assert_text,ul[class='ca-tab-large'] li[class='active'] a,,eq,部門別開始残高
        ,各種設定_勘定科目_通常入力用_貸借対照表,,load_action_file,visit.csv,,,"menu: 各種設定, function: 勘定科目"
        ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
        ,各種設定_勘定科目_通常入力用_損益計算書,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,損益計算書
        ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
        ,各種設定_勘定科目_通常入力用_製造原価報告書,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,製造原価報告書
        ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,通常入力用
        ,貸借対照表に戻る,,click,ul[class='ca-tab-in-tab-large pull-left'] li a,text,eq,貸借対照表
        ,各種設定_勘定科目_簡単入力用_支出,,click,div[class='ca-navigation-container-large'] li a,text,eq,簡単入力用
        ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,簡単入力用
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a,,eq,支出
        ,各種設定_勘定科目_簡単入力用_収入,,click,ul[class='ca-tab-in-tab-large'] li a,text,eq,収入
        ,assert,,assert_text,div[class='ca-navigation-container-large'] li[class='active'] a,,eq,簡単入力用
        ,assert,,assert_text,ul[class='ca-tab-in-tab-large'] li[class='active'] a,,eq,収入
        ,各種設定_税区分,,load_action_file,visit.csv,,,"menu: 各種設定, function: 税区分"
        ,assert,,assert_title,,,eq,税区分｜マネーフォワード クラウド会計
        ,各種設定_部門,,load_action_file,visit.csv,,,"menu: 各種設定, function: 部門"
        ,assert,,assert_text,#js-ca-main-contents > div.ca-explanation.mf-mb10 > div,,eq,部門を設定することで、残高試算表や月次推移を部門別に分析することができます。部門を追加
        ,各種設定_部門_配賦基準の設定,,click,ul[class='ca-tab-large'] li a,text,eq,配賦基準の設定
        ,assert,,assert_text,#js-allocation-patterns > div > div,,eq,部門の設定をすると配賦基準の設定が利用可能になります
        ,各種設定_部門_配賦基準の割当,,click,ul[class='ca-tab-large'] li a,text,eq,配賦基準の割当
        ,assert,,assert_text,#js-ca-main-contents > div.ca-hint-container.mf-mb10 > div,,eq,配賦基準の設定をすると配賦基準の割当が利用可能になります
        ,ホームに戻る,,click,div.sidebar-container a,text,eq,ホーム
        ,各種設定_タグ,,load_action_file,visit.csv,,,"menu: 各種設定, function: タグ"
        ,assert,,assert_title,,,eq,タグ｜マネーフォワード クラウド会計
        ,各種設定_摘要辞書,,load_action_file,visit.csv,,,"menu: 各種設定, function: 摘要辞書"
        ,assert,,assert_title,,,eq,摘要辞書｜マネーフォワード クラウド会計
        ,各種設定_仕訳辞書,,load_action_file,visit.csv,,,"menu: 各種設定, function: 仕訳辞書"
        ,assert,,assert_title,,,eq,仕訳辞書｜マネーフォワード クラウド会計
        ,各種設定_仕訳辞書_新規作成,,click,a.ca-btn-default.ca-btn-size-xsmall.mf-ml10,text,eq,新規作成
        ,assert,,assert_title,,,eq,仕訳辞書の新規作成｜マネーフォワード クラウド会計
        ,各種設定_取引先_収入先,,load_action_file,visit.csv,,,"menu: 各種設定, function: 取引先"
        ,assert,,assert_title,,,eq,取引先一覧｜マネーフォワード クラウド会計
        ,各種設定_他社ソフトデータの移行,,load_action_file,visit.csv,,,"menu: 各種設定, function: 他社ソフトデータの移行"
        ,assert,,assert_text,#js-ca-main-contents > div.mf-text-large.mf-mb15,,eq,他社ソフトとのデータの移行ができます
        ,書類管理_ストレージ,,load_action_file,visit.csv,,,"menu: 書類管理, function: ストレージ"
        ,assert,,assert_title,,,eq,ストレージ｜マネーフォワード クラウド会計
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,上部Menu_事業者・年度の管理,,click,a,text,eq,事業者・年度の管理
        ,assert,,assert_text,#js-ca-main-container > div.ca-header > ul > li:nth-child(2),,eq,事業者・年度の管理
        ,上部Menu_事業者・年度の管理_新しい事業者を作成,,click,#js-ca-main-contents > dl > dd > a,text,eq,新しい事業者を作成
        ,ダイアログ消去,,alert_accept,,,,
        ,assert,,assert_title,,,eq,マネーフォワード クラウド
        ,,,sleep,,,,3
        ,assert,,assert_text,h1[class*='MuiTypography-mfidH1 css-'],,eq,新規事業者作成（無料）
      # $home_url
        ,mfidの画面なので会計のhomeに戻る,,get,$home_url,,,
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,上部Menu_事業者・年度の管理,,click,a,text,eq,事業者・年度の管理
        ,上部Menu_事業者・年度の管理_会計年度削除,,click,#js-ca-main-contents > dl > dd > a,text,eq,削除する会計年度を選択
        ,assert,,assert_title,,,eq,会計年度の削除｜マネーフォワード クラウド会計
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,上部Menu_ユーザー設定,,click,a,text,eq,ユーザー設定
        ,assert,,assert_text,#js-ca-main-container > div.ca-header > ul > li:nth-child(2),,eq,ユーザー設定
        ,上部Menu_ユーザー設定_メールアドレスの変更,,click,#js-ca-main-contents > table > tbody > tr:nth-child(1) > td > a,text,eq,メールアドレスの変更
        ,ウィンドウを切り替える,,switch_to_new_window,,,,
        ,assert,,assert_text,body > main > div > div > div > div > section > h1,,eq,メールアドレスを変更する
        ,ウィンドウを閉じる,,close_current_window,,,,
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,上部Menu_ユーザー設定,,click,a,text,eq,ユーザー設定
        ,上部Menu_ユーザー設定_パスワード変更,,click,#js-ca-main-contents > table > tbody > tr:nth-child(2) > td > a,text,eq,パスワードの変更
        ,ウィンドウを切り替える,,switch_to_new_window,,,,
        ,assert,,assert_text,body > main > div > div > div > div > section > h1,,eq,パスワードを変更する
        ,ウィンドウを閉じる,,close_current_window,,,,
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,,,sleep,,,,1
        ,上部Menu_ユーザー設定,,click,a,text,eq,ユーザー設定
        ,上部Menu_ユーザー設定_退会する,,click,a,text,eq,退会
        ,assert,,assert_text,#alert-danger,,match,.*様の管理事業者オーナーは退会出来ません。.*$
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,,,sleep,,,,1
        ,上部Menu_ユーザー設定,,click,a,text,eq,ユーザー設定
        ,上部Menu_ユーザー設定_フィードバックを送る,,click,#js-ca-main-contents > dl > dd > a.ca-btn-default.ca-btn-size-xsmall.mf-ml20,text,eq,フィードバックを送る
        ,assert,,assert_text,#js-ca-main-contents > dl > dt,,eq,フィードバック・お問い合わせ
        ,ホームに戻る,,click,div.sidebar-container a,text,eq,ホーム
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,,,sleep,,,,1
        ,上部Menu_メンバーの追加・管理,,click,a,text,eq,メンバーの追加・管理
        ,assert,,assert_title,,,eq,メンバーの追加・管理｜マネーフォワード クラウド会計
        ,上部Menu_メニューを開く,,click,a#dropdown-office,,,
        ,,,sleep,,,,1
        ,assert_メニューに有料プランへの申し込みが含まれること,,assert_text,#js-ca-main-container > div.ca-header.dropdown.open > ul.dropdown-menu.pull-right > li:nth-child(5) > a,,eq,有料プランへの申し込み
        ,上部Menu_メプロダクトキーの利用,,click,#js-ca-main-container > div.ca-header.dropdown.open > ul.dropdown-menu.pull-right > li:nth-child(6) > a,text,eq,プロダクトキーの利用
        ,assert,,assert_text,#js-ca-main-contents > form > table > tbody > tr:nth-child(1) > th,,eq,プロダクトキー・クーポンコードの入力
      ,設定_ご利用明細,,get,$office_usage_detail_statements_url,,,
      ,assert,,assert_title,,,eq,ご利用明細｜マネーフォワード クラウド会計
      ,ホームに戻る,,click,div.sidebar-container a,text,eq,ホーム
      ,ヘルプメニューを開く,,click,a#dropdown-help,,,
      ,,,sleep,,,,1
      ,ヘルプ_使い方・FAQ,,click,a,text,eq,使い方・FAQ
      ,,,sleep,,,,2
      ,ウィンドウを切り替える,,switch_to_new_window,,,,
      ,assert,,assert_title,,,eq,マネーフォワード クラウド会計のサポートページ
      """;
}
