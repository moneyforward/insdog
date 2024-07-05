package jp.co.moneyforward.autotest.ca_web.tests.journalCreation;

import com.microsoft.playwright.Locator.GetByTextOptions;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn.Parameter;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.annotations.When;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.*;

/**
 * This test checks if the "easy-journal entry" (簡単入力) function.
 *
 * It performs a following scenario:
 *
 * - clickEasyInputUnderManualEntry
 * - enterJournalRecordWithSimpleInput
 * - deleteJournalRecord
 *
 * This test assumes the account returned by the profile is clean.
 * That is:
 *
 * - it can log in to the SUT with its password
 *
 * @see ExecutionProfile#userEmail()
 * @see ExecutionProfile#userPassword()
 * @see ExecutionProfile#accountServiceId()
 */
@Tag("journal")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "clickEasyInputUnderManualEntry",
            "enterJournalRecordWithSimpleInput",
            "deleteJournalRecord"},
        afterEach = {"screenshot"}))
public class SimpleJournalCreation extends CawebAccessingModel {
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "login"))
  public static Scene clickEasyInputUnderManualEntry() {
    return new Scene.Builder("page")
        .add(clickSidebarItem("手動で仕訳", "簡単入力"))
        .build();
  }
  
  @Named
  @When("clickEasyInputUnderManualEntry")
  @DependsOn(@Parameter(name = "page", sourceSceneName = "clickEasyInputUnderManualEntry"))
  public static Scene thenClickedItemIsVisible() {
    return new Scene.Builder("page")
        .add(assertMainContainerHasItemNamed("簡単入力"))
        .build();
  }
  
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "clickEasyInputUnderManualEntry"))
  public static Scene enterJournalRecordWithSimpleInput() {
    return new Scene.Builder("page")
        .add(clickAndFill("#journal_recognized_at", "05/15"))
        .add(clickAndFill("#journal_value", "1111"))
        .add(clickAndWaitForCompletion("登録"))
        .build();
  }
  
  @Named
  @When("enterJournalRecordWithSimpleInput")
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "enterJournalRecordWithSimpleInput"))
  public static Scene thenJournalRecordUpdated() {
    return new Scene.Builder("page")
        .add(assertEmphasizedRecordHasExpectedContent("05/15",
                                                      "+1,111",
                                                      "現金 が増加して 現金 が減少した"))
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "enterJournalRecordWithSimpleInput"))
  public static Scene deleteJournalRecord() {
    return new Scene.Builder("page")
        .add(deleteCreatedJournalEntryAndDismissDialog())
        .add(clickAndWaitForCompletion("削除"))
        .build();
  }
  
  @Named
  @When("deleteJournalRecord")
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "deleteJournalRecord"))
  public static Scene thenJournalRecordDeleted() {
    return new Scene.Builder("page")
        .add(new PageAct("Delete the created journal") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            assertThat(page.locator("ca-tr-emphasis")).not().isAttached();
          }
        })
        .build();
  }
  
  private static PageAct assertEmphasizedRecordHasExpectedContent(
      final String expectedDate,
      final String expectedAmount,
      final String expectedMessage) {
    return new PageAct("Create a journal with easy input") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.locator(".ca-tr-emphasis")
                       .locator(".js-td-recognized-at")).containsText(expectedDate);
        assertThat(page.locator(".ca-tr-emphasis")
                       .locator(".js-td-value")).containsText(expectedAmount);
        assertThat(page.locator(".ca-tr-emphasis")
                       .locator(".js-td-item")).containsText(expectedMessage);
      }
    };
  }
  
  private static PageAct assertMainContainerHasItemNamed(final String itemName) {
    return new PageAct("Check simple journals") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.locator("#js-ca-main-container")
                       .getByText(itemName, new GetByTextOptions().setExact(true))).isVisible();
      }
    };
  }
  
  private static PageAct deleteCreatedJournalEntryAndDismissDialog() {
    return new PageAct("Delete the created journal") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator(".ca-tr-emphasis").locator("a").click();
        page.onceDialog(dialog -> {
          System.out.printf("Dialog message: %s%n", dialog.message());
          dialog.dismiss();
        });
      }
    };
  }
}
