package jp.co.moneyforward.autotest.ca_web.tests.erp;

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
import static com.microsoft.playwright.options.AriaRole.LINK;
import static jp.co.moneyforward.autotest.actions.web.LocatorFunctions.textContent;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.*;
import static jp.co.moneyforward.autotest.actions.web.PageFunctions.locatorByText;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.*;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.materializeResource;

/**
 * Data need to prepare before execution, example Office: PersonalPaid
 *
 * Pre-conditions
 * - ERP Plan type: Individual Personal / 個人　パーソナル　クレカ
 */
@Tag("freePlan")
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
            "openReportItems_RevenueReport",
            "openReportItems_ExpenseReport",
            "openReportItems_RevenueDestinationReport",
            "openReportItems_PayeeReport",
            "openReportItems_externalService",
            "clickFileExport",
            "openSettlementAndDeclaration_consumptionTaxAggregation",
            "openSettlementAndDeclaration_consumptionTaxReturn",
            "openDocumentManagement_cloudBox",
            "openDocumentManagement_storage",
            "openDataLinkage_electronicCertificateLinkingSoftware",
            "clickAndIssueAuthenticationKey",
            "openVariousSettings_Category",
            "createDepartment",
            "createSubDepartment",
            "openAddingAndManagingMembers",
            "openAddMembersModal"
          
        },
        afterEach = {"screenshot"}))
public class IndividualPersonal extends CawebErpAccessingModel {
  @Named
  @Export("page")
  @DependsOn("testInitialize")
  public Scene toHome() {
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
        .assertion((Page p) -> value(p).function(locatorBySelector("#voucher-journals-candidates-index > main > div.tabMenuWrapper___S4Z39 > nav > ul > li:nth-child(2)"))
                                       .function(textContent())
                                       .toBe()
                                       .equalTo("仕訳候補"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("openEnterJournalAutomatically_fromAI_OCR")
  public Scene uploadInvoiceAsAI_OCR() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("アップロード")))
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
        .add(exportDataSpecifiedFormat("#download-btn-menu", "PDF出力", assertAlertSuccessIsDisplayed()))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportCSV_generalJournal() {
    return new Scene.Builder("page")
        .add(exportDataSpecifiedFormat("#download-btn-menu", "CSV出力", assertAlertSuccessIsDisplayed()))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportMFFormat_generalJournal() {
    return new Scene.Builder("page")
        .add(exportFileAsMFFormat(assertAlertSuccessIsDisplayed()))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_RevenueReport() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("収益レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_RevenueReport")
  public Scene thenOpenReportItems_RevenueReport() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#tab-summary > div.ca-general-container.js-menu-date-picker.mf-mb10 > ul > li:nth-child(1) > strong"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("収益の内訳"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_ExpenseReport() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("費用レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_ExpenseReport")
  public Scene thenOpenReportItems_ExpenseReport() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#tab-summary > div.ca-general-container.js-menu-date-picker.mf-mb10 > ul > li:nth-child(1) > strong"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("費用の内訳"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_RevenueDestinationReport() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("収入先レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_RevenueDestinationReport")
  public Scene thenOpenReportItems_RevenueDestinationReport() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-receivable > table.ca-table.mf-mb10 > thead > tr > th:nth-child(1)"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("収入先"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_PayeeReport() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("支出先レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_PayeeReport")
  public Scene thenOpenReportItems_PayeeReport() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(toTitle())
                                       .asString()
                                       .toBe()
                                       .equalTo("支出先レポート｜マネーフォワード クラウド確定申告"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_externalService() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
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
        .add(assertAlertSuccessIsDisplayed())
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openSettlementAndDeclaration_consumptionTaxAggregation() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(new Click(locatorByText("決算・申告")))
        .add(new Click(linkLocatorByText("消費税集計")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openSettlementAndDeclaration_consumptionTaxAggregation")
  public Scene thenOpenSettlementAndDeclaration_consumptionTaxAggregation() {
    return new Scene.Builder("page")
        .assertion((Page p) -> value(p).function(locatorBySelector("#js-ca-main-container > div.ca-navigation-container-large > ul > li.active > a"))
                                       .function(textContent())
                                       .asString()
                                       .toBe()
                                       .containing("勘定科目別税区分集計表"))
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
  public Scene openAddMembersModal() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("メンバー追加")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAddMembersModal")
  public Scene thenOpenAddMembersModal() {
    return new Scene.Builder("page")
        .add(assertMessageAndCloseModalForAddingMembers("メールアドレス"))
        .build();
  }
  
  /**
   * Confirm displayed message on "add members modal", then close it
   *
   * @return The page act that performs the behavior in the description.
   */
  public static PageAct assertMessageAndCloseModalForAddingMembers(final String displayedMessage) {
    return new PageAct("assert that modal with displayedMessage is displayed, and then create member") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator addMembersModal = page.locator("#js-add-members-modal > div > div");
        final String differentiatingSuffix = InternalUtils.dateToSafeString(InternalUtils.now());
        
        page.waitForSelector("#js-add-members-modal");
        
        if (addMembersModal.isVisible()) {
          assertThat(addMembersModal.getByText(displayedMessage)).isVisible();
          addMembersModal.locator("#user_email").click();
          clickAndFill("#user_email", "user" + differentiatingSuffix + "@hogehoge.com").perform(page, executionEnvironment);
          clickAndFill("#js-add-members-modal #office_member_name", "ユーザー" + differentiatingSuffix).perform(page, executionEnvironment);
          addMembersModal.locator("#invitation-form > div.text-center > input").click();
        }
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


