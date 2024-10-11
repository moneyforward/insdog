package jp.co.moneyforward.autotest.ca_web.tests.erp;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Click;
import jp.co.moneyforward.autotest.actions.web.Navigate;
import jp.co.moneyforward.autotest.actions.web.PageAct;

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
 * Data need to prepare before execution, example Office: PersonalFree
 *
 * Pre-conditions
 * - ERP Plan type: Individual Free / 個人　無料
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
            "openAddingAndManagingMembers",
            "createMembers"
          
        },
        afterEach = {"screenshot"}))
public class IndividualFree extends CawebErpAccessingModel {
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("ファイルをアップロードするには有料プラン登録が必要です"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("残高照合機能をご利用いただくためにはパーソナルプランへの加入が必要です"))
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
        .add(exportDataSpecifiedFormat("#download-btn-menu", "CSV出力", assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openAccountingBooks_generalJournal")
  public Scene exportMFFormat_generalJournal() {
    return new Scene.Builder("page")
        .add(exportFileAsMFFormat(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です")))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_ExpenseReport() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("費用レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_ExpenseReport")
  public Scene thenOpenReportItems_ExpenseReport() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_RevenueDestinationReport() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("収入先レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_RevenueDestinationReport")
  public Scene thenOpenReportItems_RevenueDestinationReport() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openReportItems_PayeeReport() {
    return new Scene.Builder("page")
        .add(new Click(locatorByText("レポート")))
        .add(new Click(linkLocatorByText("支出先レポート")))
        .build();
  }
  
  @Named
  @Export("page")
  @When("openReportItems_PayeeReport")
  public Scene thenOpenReportItems_PayeeReport() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openSettlementAndDeclaration_consumptionTaxReturn() {
    return new Scene.Builder("page")
        .add(new Navigate(executionProfile().homeUrl()))
        .add(navigateToNewTabUnderSidebarItemAndAct("決算・申告", "消費税申告", assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です")))
        .build();
  }
  
  @Named
  @Export("page")
  @DependsOn("login")
  public Scene openDocumentManagement_cloudBox() {
    return new Scene.Builder("page")
        .add(navigateToNewTabUnderSidebarItemAndAct("書類管理", "クラウドBox", assertPageHasItemNamed()))
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
                                       .containing("100 MB"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
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
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
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
        .build();
  }
  
  @Named
  @Export("page")
  @When("createMembers")
  public Scene thenCreateMembers() {
    return new Scene.Builder("page")
        .add(assertMessageAndClosePremiumModalIndividualPersonal("パーソナルプランへの加入が必要です"))
        .build();
  }
  
  
  /**
   * There are two types of modal related to ERP. If either of them is displayed, close it.
   *
   * @return The page act that performs the behavior in the description.
   */
  public static PageAct assertMessageAndClosePremiumModalIndividualPersonal(final String displayedMessage) {
    return new PageAct("Assert Displayed message on Premium Modal for Personal and close it") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator individualPersonalModal = page.locator("#js-premium-modal-individual-personal");
        Locator individualPersonalMiniModal = page.locator("#js-premium-modal-individual-personal-mini");
        
        
        boolean modalAppeared = false; // TODO: Need to refactor due to duplicated code
        long startTime = System.currentTimeMillis();
        
        // enhanced waitForSelector feature: wait for either corporateBusinessModal or smallBusinessModal is displayed
        while (!modalAppeared && (System.currentTimeMillis() - startTime) < 5000) {
          if (individualPersonalModal.isVisible() || individualPersonalMiniModal.isVisible()) {
            modalAppeared = true;
          } else {
            page.waitForTimeout(100); // Wait for 100 milliseconds before checking again
          }
        }
        
        String modalCloseButtonSelector = "#btn-modal-close > img";
        if (individualPersonalModal.isVisible()) {
          assertThat(individualPersonalModal.getByText(displayedMessage)).isVisible();
          
          individualPersonalModal.locator(modalCloseButtonSelector).click();
        } else {
          assertThat(individualPersonalMiniModal.getByText(displayedMessage)).isVisible();
          
          individualPersonalMiniModal.locator(modalCloseButtonSelector).click();
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
  
  public static PageAct assertPageHasItemNamed() {
    return new PageAct("Check simple journals") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.getByText("現在のプランでは、新規ファイルをアップロードできません。")).isVisible();
      }
    };
  }
}
