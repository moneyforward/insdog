package jp.co.moneyforward.autotest.ca_web.tests.journalCreation;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator.GetByTextOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.ElementState;
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
import static com.microsoft.playwright.options.AriaRole.BUTTON;

/**
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
        value = {"clickEasyInputUnderManualEntry", "enterJournalRecordWithSimpleInput", "deleteJournalRecord"},
        afterEach = {"screenshot"}))
public class SimpleJournalCreation extends CawebAccessingModel {
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "login"))
  public static Scene clickEasyInputUnderManualEntry() {
    return new Scene.Builder("page")
        .add(new PageAct("Click '簡単入力'") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.getByText("手動で仕訳").hover();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("簡単入力")).click();
          }
        })
        .build();
  }
  
  @Named
  @When("clickEasyInputUnderManualEntry")
  @DependsOn(@Parameter(name = "page", sourceSceneName = "clickEasyInputUnderManualEntry"))
  public static Scene thenClickedItemIsVisible() {
    return new Scene.Builder("page")
        .add(new PageAct("Check simple journals") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            assertThat(page.locator("#js-ca-main-container")
                           .getByText("簡単入力", new GetByTextOptions().setExact(true))).isVisible();
          }
        })
        .build();
  }
  
  @Named
  @DependsOn(@Parameter(name = "page", sourceSceneName = "clickEasyInputUnderManualEntry"))
  public static Scene enterJournalRecordWithSimpleInput() {
    return new Scene.Builder("page")
        .add(new PageAct("Create a journal with easy input") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.locator("#journal_recognized_at").click();
            page.locator("#journal_recognized_at").fill("05/15");
            page.locator("#journal_value").click();
            page.locator("#journal_value").fill("1111");
            page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("登録")).click();
            
            ElementHandle loader = page.querySelector(".ca-saving-cover");
            loader.waitForElementState(ElementState.HIDDEN);
          }
        })
        .build();
  }
  
  @Named
  @When("enterJournalRecordWithSimpleInput")
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "enterJournalRecordWithSimpleInput"))
  public static Scene thenJournalRecordUpdated() {
    return new Scene.Builder("page")
        .add(new PageAct("Create a journal with easy input") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-recognized-at")).containsText("05/15");
            assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-value")).containsText("+1,111");
            assertThat(page.locator(".ca-tr-emphasis").locator(".js-td-item")).containsText("現金 が増加して 現金 が減少した");
          }
        })
        .build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "enterJournalRecordWithSimpleInput"))
  public static Scene deleteJournalRecord() {
    return new Scene.Builder("page")
        .add(new PageAct("Delete the created journal") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            // Let's make this a shared function.
            ElementHandle loader = page.querySelector(".ca-saving-cover");
            loader.waitForElementState(ElementState.HIDDEN);
            page.locator(".ca-tr-emphasis").locator("a").click();
            page.onceDialog(dialog -> {
              System.out.printf("Dialog message: %s%n", dialog.message());
              dialog.dismiss();
            });
            page.getByText("削除", new Page.GetByTextOptions().setExact(true)).click();
            // Let's make this a shared function.
            loader.waitForElementState(ElementState.HIDDEN);
          }
        })
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
}
