package jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Click;
import jp.co.moneyforward.autotest.actions.web.Navigate;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.junit.jupiter.api.Tag;

import java.nio.file.Paths;

import static com.github.valid8j.fluent.Expectations.value;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.microsoft.playwright.options.AriaRole.LINK;
import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.textContent;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.locatorByText;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.*;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.materializeResource;

/**
 * Data need to prepare before execution, example Office: abc-140129
 *
 */
@Tag("freePlan")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
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
public class FreePlanScenario extends CawebAccessingModel {
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
  public static Scene openEnterJournalAutomatically_fromAI_OCR() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("自動で仕訳")))
        .add(new Click(linkLocatorByText("AI-OCRから入力")))
        .assertion((Page p) -> value(p).function(locatorBySelector("#voucher-journals-candidates-index > main > div.tabMenuWrapper___S4Z39 > nav > ul > li:nth-child(2)"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("仕訳候補"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openEnterJournalAutomatically_fromAI_OCR")
  public static Scene uploadInvoiceAsAI_OCR() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("アップロード")))
        .add(fileUploadAsAI_OCR("ca_web/invoiceImage.png",
                                "領収書", "電帳法の対象外"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("uploadInvoiceAsAI_OCR")
  public static Scene thenUploadInvoiceAsAI_OCR() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("ファイルをアップロードするには有料プラン登録が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openEnterJournalAutomatically_fromE_Invoice() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("自動で仕訳")))
        .add(new Click(linkLocatorByText("デジタルインボイスから入力")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openEnterJournalAutomatically_fromE_Invoice")
  public static Scene thenOpenEnterJournalAutomatically_fromE_Invoice() {
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
  public static Scene openTransactionManagement_debtManagement() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToMenuItemUnderSidebarItem("債務管理","取引管理"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openTransactionManagement_debtManagement")
  public static Scene thenOpenTransactionManagement_debtManagement() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("ビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openTransactionManagement_balanceSheet() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToMenuItemUnderSidebarItem("残高照合","取引管理"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openTransactionManagement_balanceSheet")
  public static Scene thenOpenTransactionManagement_balanceSheet() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("残高照合機能をご利用いただくためにはビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openAccountingBooks_generalJournal() {
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
  public static Scene exportPDF_generalJournal() {
    return new Scene.Builder("page")
        .add(exportDataSpecifiedFormat("#download-btn-menu","PDF出力", assertAlertSuccessIsDisplayed()))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public static Scene exportCSV_generalJournal() {
    return new Scene.Builder("page")
        .add(exportDataSpecifiedFormat("#download-btn-menu", "CSV出力", assertMessageAndClosePremiumModalCorporate("スモールビジネス以上のプランへの加入が必要です")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public static Scene exportMFFormat_generalJournal() {
    return new Scene.Builder("page")
        .add(exportFileAsMFFormat(assertMessageAndClosePremiumModalCorporate("スモールビジネス以上のプランへの加入が必要です")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openAccountingBooks_booksSetting() {
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
  public static Scene setDateLimitsJournalEntry() {
    return new Scene.Builder("page")
        .add(clickAndFill("#acts-search-recognized-at-from","2024/09/11"))
        .add(clickAndWaitForCompletion("保存"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("setDateLimitsJournalEntry")
  public static Scene thenSetDateLimitsJournalEntry() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("ビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openAccountingBooks_booksSetting_transactionNumberReassignment() {
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
  public static Scene executionTransactionNumberReassignment() {
    return new Scene.Builder("page")
        .add(clickButtonAndCloseDialog("実行"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("executionTransactionNumberReassignment")
  public static Scene thenExecutionTransactionNumberReassignment() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("ビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openReportItems_externalService() {
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
  public static Scene clickFileExport() {
    return new Scene.Builder("page")
        .add(new Click("#js-ca-main-contents > dl > dd:nth-child(2) > form > button"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("clickFileExport")
  public static Scene thenClickFileExport() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("スモールビジネス以上のプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openSettlementAndDeclaration_consumptionTaxReturn() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToNewTabUnderSidebarItemAndAct("決算・申告", "消費税申告", assertMessageAndClosePremiumModalCorporate("ビジネスプランへの加入が必要です")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openDocumentManagement_cloudBox() {
    return new Scene.Builder("page")
        .add(navigateToNewTabUnderSidebarItemAndAct("書類管理", "クラウドBox", assertLocatorHasExpectedText("#__next > div.flex.h-screen.flex-col > div.flex.max-h-\\[calc\\(100vh_-_40px\\)\\].min-h-\\[calc\\(900px_-_40px\\)\\].grow > main > div.mb-8 > div.flex.w-full.place-content-between.px-3\\.5.py-3.bg-\\[\\#FDE2DE\\] > div.flex.h-\\[22px\\].items-end.text-5 > span", "現在のプランでは、新規ファイルをアップロードできません。")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openDocumentManagement_storage() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("書類管理")))
        .add(new Click(linkLocatorByText("ストレージ")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openDocumentManagement_storage")
  public static Scene thenOpenDocumentManagement_storage() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-contents > div.equal-distance-container.align-items-center.mf-mb10 > div:nth-child(1)"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("100 MB"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openDataLinkage_electronicCertificateLinkingSoftware() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("データ連携")))
        .add(new Click(linkLocatorByText("電子証明書連携ソフト")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openDataLinkage_electronicCertificateLinkingSoftware")
  public static Scene clickAndIssueAuthenticationKey() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("認証キーを発行")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("clickAndIssueAuthenticationKey")
  public static Scene thenClickAndIssueAuthenticationKey() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("ビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openVariousSettings_office() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("各種設定")))
        .add(new Click(linkLocatorByText("事業者")))
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
  public static Scene updateOfficeInfoAndCheckJournalHistoryRecord() {
    return new Scene.Builder("page")
        .add(officeSetting("#js-ca-main-contents > form > table:nth-child(6) > tbody > tr:nth-child(8) > td > div.is-hidden.js-business-type-list-corporate > span:nth-child(6) > label", "11〜30人"))
        .add(checkJournalHistoryRecord())
        .add(new Click(locatorByText("設定を保存")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("updateOfficeInfoAndCheckJournalHistoryRecord")
  public static Scene thenUpdateOfficeInfoAndCheckJournalHistoryRecord() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("仕訳履歴保存機能をご利用いただくためにはビジネスプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openVariousSettings_Category() {
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
  public static Scene createDepartment() {
    return new Scene.Builder("page")
        .add(clickButtonToDisplayModalAndEnterDepartmentNameAndRegister("#js-new-root-dept", "大部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("createDepartment")
  public static Scene thenCreateDepartment() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("部門登録数が上限の2件になりましたビジネスプランでは部門を無制限に登録できます"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openVariousSettings_Category")
  public static Scene createSubDepartment() {
    return new Scene.Builder("page")
        .add(clickButtonToDisplayModalAndEnterDepartmentNameAndRegister("#js-dept-rows > li > ul > li > a", "子部門"))
        .build();
  }
  
  @Named
  @Export("page")
  @When("createSubDepartment")
  public static Scene thenCreateSubDepartment() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("部門登録数が上限の2件になりましたビジネスプランでは部門を無制限に登録できます"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public static Scene openAddingAndManagingMembers() {
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
  public static Scene createMembers() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("メンバー追加")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("createMembers")
  public static Scene thenCreateMembers() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalCorporate("メンバー登録数が上限に達しています。ビジネスプランへアップグレードするとメンバーを追加することができます。※従量課金の対象になります。"))
        .build();
  }
  
  
  /**
   * There are two types of modal related to ERP. If either of them is displayed, close it.
   *
   * @return The page act that performs the behavior in the description.
   */
  public static PageAct assertMessageAndClosePremiumModalCorporate(final String displayedMessage) {
    return new PageAct("Assert Displayed message on Premium Modal for Corporate and close it") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator corporateBusinessModal = page.locator("#js-premium-modal-corporate-business");
        Locator smallBusinessModal = page.locator("#js-premium-modal-corporate-small-business");
        
        boolean modalAppeared = false;
        long startTime = System.currentTimeMillis();
        
        // enhanced waitForSelector feature: wait for either corporateBusinessModal or smallBusinessModal is displayed
        while (!modalAppeared && (System.currentTimeMillis() - startTime) < 5000) {
          if (corporateBusinessModal.isVisible() || smallBusinessModal.isVisible()) {
            modalAppeared = true;
          } else {
            page.waitForTimeout(100); // Wait for 100 milliseconds before checking again
          }
        }
        
        String modalCloseButtonSelector = "#btn-modal-close > img";
        if (corporateBusinessModal.isVisible()) {
          assertThat(corporateBusinessModal.getByText(displayedMessage)).isVisible();
          
          corporateBusinessModal.locator(modalCloseButtonSelector).click();
        } else {
          assertThat(smallBusinessModal.getByText(displayedMessage)).isVisible();
          
          smallBusinessModal.locator(modalCloseButtonSelector).click();
        }
      }
    };
  }
  
  /**
   * Always accept the displayed dialog
   *
   * @param buttonName The name of the button where the dialog displays
   * @return The page act that performs the behavior in the description
   */
  static PageAct clickButtonAndCloseDialog(final String buttonName) {
    return new PageAct("close dialog display") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.onDialog(Dialog::accept);
        page.getByText(buttonName).click();
      }
    };
  }
  
  /**
   * Changing items of office setting
   *
   * @param locatorIndustryCategoryCheckbox Locator of checkbox for industry category
   * @param locatorEmployeeCount Locator of selection box for employee counts
   * @return The page act that performs the behavior in the description
   */
  public static PageAct officeSetting(final String locatorIndustryCategoryCheckbox, final String locatorEmployeeCount) {
    return new PageAct("Update Office setting, check a checkbox and select for selection box") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        // Check a checkbox on 業種区分 section
        page.locator(locatorIndustryCategoryCheckbox).check();
        
        // Update value on 従業員数 selection box
        page.locator("#s2id_ca_office_setting_employee_count").click();
        
        Locator selectionOptionEmployeeCount = page.locator("#select2-drop > ul");
        selectionOptionEmployeeCount.getByText(locatorEmployeeCount).click();
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
  public static PageAct fileUploadAsAI_OCR(final String imageResourcePath, final String documentType, final String categoryOfElectronicBookkeeping) {
    return new PageAct(String.format("Upload target file (%s) as AI OCR, then select invoice type as %s and %s", imageResourcePath, documentType, categoryOfElectronicBookkeeping)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        String tmpFileName = materializeResource(imageResourcePath).getAbsolutePath();
        
        //Select specified file and reflected it to page
        Locator fileInput = page.locator("input[type='file']");
        fileInput.first().setInputFiles(Paths.get(tmpFileName));
        
        page.waitForSelector("#voucher-journals-index > main > div.dndArea___Asggy > div > div.container___P5zPk > div > table > thead > tr");
        
        // Select 書類種別
        page.locator("#voucher-journals-index > main > div.dndArea___Asggy > div > div.container___P5zPk > div > table > tbody > tr > td:nth-child(4) > div").click();
        page.locator("#page-voucher-journals > div.ca-client-bootstrap-reset-css.ca-client-ca-web-reset-css.ca-client-searchable-select-for-spreadsheet-drop-down-list.dropDownList___XplIs")
            .getByText(documentType).click();
        
        // Select 電子帳簿保存法区分
        page.locator("#voucher-journals-index > main > div.dndArea___Asggy > div > div.container___P5zPk > div > table > tbody > tr > td:nth-child(5) > div").click();
        page.locator("#page-voucher-journals > div.ca-client-bootstrap-reset-css.ca-client-ca-web-reset-css.ca-client-searchable-select-for-spreadsheet-drop-down-list.dropDownList___XplIs")
            .getByText(categoryOfElectronicBookkeeping).click();
        
        page.locator("#voucher-journals-index > main > footer > div > button").click();
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
  public static PageAct assertAlertSuccessIsDisplayed() {
    return new PageAct("Confirm that #alert-success is displayed") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.locator("#alert-success > p")).isVisible();
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
}