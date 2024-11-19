package jp.co.moneyforward.autotest.ca_web.tests.erp;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Click;
import jp.co.moneyforward.autotest.actions.web.Navigate;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.jupiter.api.Tag;

import java.nio.file.Paths;

import static com.github.valid8j.fluent.Expectations.value;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.microsoft.playwright.options.AriaRole.BUTTON;
import static com.microsoft.playwright.options.AriaRole.LINK;
import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.textContent;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.locatorByText;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.*;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.materializeResource;

/**
 * Data need to prepare before execution, example Office: abc-154206
 *
 * Pre-conditions
 * - ERP Plan type: Corporate Business / 法人 ビジネス
 * - Complete initial setting in 各種設定 > 事業者
 *
 */
@Tag("businessPlan")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "toHome",
            "openEnterJournalAutomatically_fromAI_OCR",
            "uploadInvoiceAsAI_OCR",
            "openEnterJournalAutomatically_fromE_Invoice",
            "openTransactionManagement_debtManagement",
            "openTransactionManagement_balanceSheet",
            "openAccountingBooks_generalJournal",
            "openAccountingBooks_booksSetting",
            "setDateLimitsJournalEntry",
            "openAccountingBooks_booksSetting_transactionNumberReassignment",
            "executionTransactionNumberReassignment",
            "openReportItems_externalService",
            "clickFileExport",
            "openSettlementAndDeclaration_consumptionTaxReturn",
            "openDocumentManagement_cloudBox",
            "openDocumentManagement_storage",
            "openDataLinkage_electronicCertificateLinkingSoftware",
            "clickAndIssueAuthenticationKey",
            "openVariousSettings_office",
            "updateOfficeInfoAndCheckJournalHistoryRecord",
            "openVariousSettings_Category",
            "createDepartment",
            "createSubDepartment",
            "openAddingAndManagingMembers",
            "createMembers"
          
        },
        afterEach = {"screenshot"}))
public class CorporateBusiness extends CawebErpAccessingModel {
  @Named
  @Export("page")
  @DependsOn("testInitialize")
  public static Scene toHome() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openEnterJournalAutomatically_fromAI_OCR() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("自動で仕訳")))
        .add(new Click(linkLocatorByText("AI-OCRから入力")))
        .add(assertLocatorIsDisplayed("#voucher-journals-candidates-index > main > div.body___WkbVx > table > tbody:nth-child(2)"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openEnterJournalAutomatically_fromAI_OCR")
  public Scene uploadInvoiceAsAI_OCR() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("ファイル選択")))
        .add(fileUploadAsAI_OCR("ca_web/invoiceImage.png",
                                "領収書", "電帳法の対象外"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("uploadInvoiceAsAI_OCR")
  public Scene thenUploadInvoiceAsAI_OCR() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("li > div"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("ファイルをアップロードしました（1件）"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openEnterJournalAutomatically_fromE_Invoice() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("自動で仕訳")))
        .add(new Click(linkLocatorByText("デジタルインボイスから入力")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openEnterJournalAutomatically_fromE_Invoice")
  public Scene thenOpenEnterJournalAutomatically_fromE_Invoice() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-header.dropdown > ul.header-category-label > li:nth-child(3)"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("デジタルインボイスから入力"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openTransactionManagement_debtManagement() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToMenuItemUnderSidebarItem("債務管理","取引管理"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openTransactionManagement_debtManagement")
  public Scene thenOpenTransactionManagement_debtManagement() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("支払先から取引を作成"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openTransactionManagement_balanceSheet() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToMenuItemUnderSidebarItem("残高照合","取引管理"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openTransactionManagement_balanceSheet")
  public Scene thenOpenTransactionManagement_balanceSheet() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("銀行口座一覧"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openAccountingBooks_generalJournal() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToMenuItemUnderSidebarItem("仕訳帳","会計帳簿"))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-header.dropdown > ul.header-category-label > li:nth-child(3)"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("仕訳帳"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportPDF_generalJournal() {
    return new Scene.Builder("page")
        .add(exportDataSpecifiedFormat("#download-btn-menu", "PDF出力", assertLocatorIsDisplayed("#alert-success > p")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportCSV_generalJournal() {
    return new Scene.Builder("page")
        .add(exportDataSpecifiedFormat("#download-btn-menu", "CSV出力", assertLocatorIsDisplayed("#alert-success > p")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportMFFormat_generalJournal() {
    return new Scene.Builder("page")
        .add(exportFileAsMFFormat(assertLocatorIsDisplayed("#alert-success > p")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openAccountingBooks_booksSetting() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("会計帳簿")))
        .add(new Click(linkLocatorByText("帳簿管理")))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("仕訳入力の制限日付を設定する"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openAccountingBooks_booksSetting")
  public Scene setDateLimitsJournalEntry() {
    return new Scene.Builder("page")
        .add(clickAndFill("#acts-search-recognized-at-from","2024/09/11"))
        .add(clickAndWaitForCompletion("保存"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("setDateLimitsJournalEntry")
  public Scene thenSetDateLimitsJournalEntry() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#alert-success > p"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("仕訳入力の期間制限を設定しました"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openAccountingBooks_booksSetting_transactionNumberReassignment() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("会計帳簿")))
        .add(new Click(linkLocatorByText("帳簿管理")))
        .add(new Click(linkLocatorByText("取引No.の振り直し")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openAccountingBooks_booksSetting_transactionNumberReassignment")
  public Scene executionTransactionNumberReassignment() {
    return new Scene.Builder("page")
        .add(clickButtonAndCloseDialog())
        .build();
  }
  
  @Named
  @Export("page")
  @When("executionTransactionNumberReassignment")
  public Scene thenExecutionTransactionNumberReassignment() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#alert-success > p"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("取引No.の振り直し処理を開始しました。"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_externalService() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("外部サービス")))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.ca-navigation-container > ul > li > a"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("MAP3連携"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openReportItems_externalService")
  public Scene clickFileExport() {
    return new Scene.Builder("page")
        .add(new Click("#js-ca-main-contents > dl > dd:nth-child(2) > form > button"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("clickFileExport")
  public Scene thenClickFileExport() {
    return new Scene.Builder("page")
        .add(assertLocatorIsDisplayed("#alert-success > p"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openSettlementAndDeclaration_consumptionTaxReturn() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToNewTabUnderSidebarItemAndAct("決算・申告", "消費税申告",
                                                    assertLocatorHasExpectedText("#__next > div.css-h2zygn > div > div > button",
                                                                                 "新規作成")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openDocumentManagement_cloudBox() {
    return new Scene.Builder("page")
        .add(navigateToNewTabUnderSidebarItemAndAct("書類管理", "クラウドBox",
                                                    assertLocatorHasExpectedText("#__next > div.flex.h-screen.min-h-\\[700px\\].min-w-\\[1290px\\].flex-col.overflow-auto > div.flex.grow.overflow-hidden > main > nav > h1", "ファイル")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openDocumentManagement_storage() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("書類管理")))
        .add(new Click(linkLocatorByText("ストレージ")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openDocumentManagement_storage")
  public Scene thenOpenDocumentManagement_storage() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.equal-distance-container.align-items-center.mf-mb10 > div:nth-child(1)"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("10 GB"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openDataLinkage_electronicCertificateLinkingSoftware() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("データ連携")))
        .add(new Click(linkLocatorByText("電子証明書連携ソフト")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openDataLinkage_electronicCertificateLinkingSoftware")
  public Scene clickAndIssueAuthenticationKey() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("認証キーを発行")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("clickAndIssueAuthenticationKey")
  public Scene thenClickAndIssueAuthenticationKey() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-token-pane > tbody > tr:nth-child(1) > th"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("認証キー"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openVariousSettings_office() {
    return new Scene.Builder("page")
        .add(navigateToMenuItemUnderSidebarItem("事業者","各種設定"))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("基本設定"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openVariousSettings_office")
  public Scene updateOfficeInfoAndCheckJournalHistoryRecord() {
    return new Scene.Builder("page")
        .add(checkJournalHistoryRecord())
        .add(new Click(locatorByText("設定を保存")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("updateOfficeInfoAndCheckJournalHistoryRecord")
  public Scene thenUpdateOfficeInfoAndCheckJournalHistoryRecord() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#alert-success > p"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("事業者情報を更新しました。"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openVariousSettings_Category() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("各種設定")))
        .add(new Click(linkLocatorByText("部門")))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container-large > ul > li.active > a"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("部門の設定"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openVariousSettings_Category")
  public Scene createDepartment() {
    return new Scene.Builder("page")
        .add(clickButtonToDisplayModalAndEnterDepartmentNameAndRegister("#js-new-root-dept", "大部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("createDepartment")
  public Scene thenCreateDepartment() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.dept-container"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("大部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openVariousSettings_Category")
  public Scene createSubDepartment() {
    return new Scene.Builder("page")
        .add(clickButtonToDisplayModalAndEnterDepartmentNameAndRegister("#js-dept-rows > li > ul > li > a", "子部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("createSubDepartment")
  public Scene thenCreateSubDepartment() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-dept-rows > li:nth-child(1)"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("子部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openAddingAndManagingMembers() {
    return new Scene.Builder("page")
        .add(navigateToMenuItemUnderOfficeSettingItem("メンバーの追加・管理", "#dropdown-office"))
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("メンバー一覧"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openAddingAndManagingMembers")
  public Scene createMembers() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("メンバー追加")))
        .add(createMembersWithRandomNumber())
        .build();
  }
  
  @Named
  @Export("page")
  @When("createMembers")
  public Scene thenCreateMembers() {
    return new Scene.Builder("page")
        .add(assertLocatorHasExpectedText("#alert-success > p", "メンバー追加メールを送信しました"))
        .build();
  }
  
  
  /**
   * Open add-members-modal and fill member info with random number, then register
   *
   * @return The page act that performs the behavior in the description.
   */
  public static PageAct createMembersWithRandomNumber() {
    return new PageAct("Close modal for adding members") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator addMembersModal= page.locator("#js-add-members-modal > div > div");
        final String differentiatingSuffix = InternalUtils.dateToSafeString(InternalUtils.now());
        
        page.waitForSelector("#js-add-members-modal");
        
        if (addMembersModal.isVisible()) {
          addMembersModal.locator("#user_email").click();
          clickAndFill("#js-add-members-modal #user_email", "user"+ differentiatingSuffix+"@hogehoge.com").perform(page, executionEnvironment);
          clickAndFill("#js-add-members-modal #office_member_name", "ユーザー"+ differentiatingSuffix).perform(page, executionEnvironment);
          addMembersModal.locator("#invitation-form > div.text-center > input").click();
        }
      }
    };
  }
  
  /**
   * Always accept the displayed dialog
   *
   * @return The page act that performs the behavior in the description
   */
  static PageAct clickButtonAndCloseDialog() {
    return new PageAct("close dialog display") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.onDialog(Dialog::accept);
        page.getByText("実行").click();
      }
    };
  }
  
  /**
   * Enabling journal history saving function(仕訳履歴保存機能)
   *
   * @return The page act that performs the behavior in the description
   */
  public static PageAct checkJournalHistoryRecord() {
    return new PageAct("Check a checkbox on 仕訳履歴保存機能") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator("#js-ca-main-contents > form > table:nth-child(12) > tbody > tr:nth-child(1) > td > div.mf-mb5.is-relative > label").check();
      }
    };
  }
  
  /**
   * Uploading files via AI OCR feature
   *
   * @param imageResourcePath Path of the file want to upload
   * @param documentType Select the type of 書類種別 from (領収書/請求書)
   * @param categoryOfElectronicBookkeeping Select the type of 電子帳簿保存法区分 from ([ 電子取引 ] メールや電子データで受領したもの/電帳法の対象外)
   * @return The page act that performs the behavior in the description
   */
  // TODO: Need to refactor following PageAct due to duplicated code
  public static PageAct fileUploadAsAI_OCR(final String imageResourcePath, final String documentType, final String categoryOfElectronicBookkeeping) {
    return new PageAct(String.format("Upload target file (%s) as AI OCR, then select invoice type as %s and %s", imageResourcePath, documentType, categoryOfElectronicBookkeeping)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        String tmpFileName = materializeResource(imageResourcePath).getAbsolutePath();
        
        //Select specified file and reflected it to page
        Locator fileInput = page.locator("input[type='file']");
        fileInput.first().setInputFiles(Paths.get(tmpFileName));
        
        Locator locatorFileUploadModal =  page.locator("#voucher-journals-candidates-index > main > div:nth-child(1) > div > div.dialog___GHuHL.large___bTKw0");
        
        // Select 書類種別
        locatorFileUploadModal.getByText(documentType).click();
        
        // Select 電子帳簿保存法区分
        locatorFileUploadModal.getByText(categoryOfElectronicBookkeeping).click();
        
        page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("アップロード")).click();
      }
    };
  }
  
  /**
   * Exporting MF format data
   * Run PageAct after the file has been prepared
   *
   * @param pageAct PageAct after export has started
   * @return The page act that performs the behavior in the description
   */
  // TODO: Need to refactor following PageAct due to duplicated code
  public static PageAct exportFileAsMFFormat(PageAct pageAct) {
    return new PageAct("Select export data type as MF format, and then act specified PageAct") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator("#download-btn-menu").click();
        page.getByRole(LINK, new Page.GetByRoleOptions().setName("MF形式")).click();
        
        String exportFormSelector = "#page-books > div.modal.fade.modal-io.js-modal-exports-mf.in > div > div";
        
        page.waitForSelector(exportFormSelector);
        page.locator("#js-export-form > dl > dd > button").click();
        
        pageAct.perform(page, executionEnvironment);
      }
    };
  }
  
  /**
   * Confirm that #alert-success is displayed
   *
   * @return The page act that performs the behavior in the description
   */
  public static PageAct assertLocatorIsDisplayed(final String targetLocator) {
    return new PageAct("Confirm that #alert-success is displayed") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.locator(targetLocator)).isVisible();
      }
    };
  }
  
  /**
   * Checking whether the page contains the elements expecting
   *
   * @param targetLocator Locator of the element to be checked
   * @param expectedText The text that is expected for the element
   * @return The page act that performs the behavior in the description
   */
  public static PageAct assertLocatorHasExpectedText(final String targetLocator, final String expectedText) {
    return new PageAct("assert that element-is-equal") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.locator(targetLocator)).hasText(expectedText);
      }
    };
  }
  
  /**
   * Creating a departments 部門
   *
   * @param locatorButton Locator of the button that displays the form for creating departments
   * @param value Department name
   * @return The page act that performs the behavior in the description
   */
  // TODO: Need to refactor following PageAct due to duplicated code
  public static PageAct clickButtonToDisplayModalAndEnterDepartmentNameAndRegister(final String locatorButton, final String value) {
    return new PageAct("create department: Display modal and enter value") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator categoryFormModal = page.locator("#js-add-dept-modal");
        
        page.locator(locatorButton).first().click();
        
        page.waitForSelector("#js-add-dept-modal");
        
        if (categoryFormModal.isVisible()) {
          categoryFormModal.locator("#dept_name").fill(value);
          categoryFormModal.locator("#js-btn-add-dept").click();
          
        }
        categoryFormModal.locator("#btn-modal-close > img").click();
      }
    };
  }
  
  /**
   * Exporting data such as journal data, Click and select file type
   * Run PageAct after the file has been prepared
   *
   * @param locatorExportButton Buttons for selecting the data format, it is usually described as "エクスポート"
   * @param dataFormat name of data format, ex PDF出力
   * @param pageAct PageAct after export has started, ex Confirm file exporting status
   * @return The page act that performs the behavior in the description
   */
  // TODO: Need to refactor following PageAct due to duplicated code
  public static PageAct exportDataSpecifiedFormat(final String locatorExportButton, final String dataFormat, PageAct pageAct) {
    return new PageAct(String.format("Click '%s'->'%s, and then act specified PageAct'", locatorExportButton, dataFormat)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator(locatorExportButton).click();
        Page newPage = page.waitForPopup(()-> page.getByRole(LINK, new Page.GetByRoleOptions().setName(dataFormat)).click());
        
        pageAct.perform(newPage, executionEnvironment);
      }
    };
  }
}