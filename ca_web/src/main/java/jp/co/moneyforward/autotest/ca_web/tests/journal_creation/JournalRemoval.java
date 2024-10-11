package jp.co.moneyforward.autotest.ca_web.tests.journal_creation;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import static com.microsoft.playwright.options.WaitForSelectorState.HIDDEN;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.clickAndWaitForCompletion;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.navigateToMenuItemUnderSidebarItem;

@Tag("journal")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "deleteFirstTwoJournalEntries"
        },
        afterEach = {"screenshot"}))
public class JournalRemoval extends CawebAccessingModel {
  @Named
  @DependsOn("login")
  @Export("page")
  public static Scene deleteFirstTwoJournalEntries() {
    return new Scene.Builder("page")
        .add(navigateToMenuItemUnderSidebarItem("簡単入力", "手動で仕訳"))
        .add(deleteFirstJournalEntryAndAcceptDialogIfPresent(1_000))
        .add(deleteFirstJournalEntryAndAcceptDialogIfPresent(1_000))
        .build();
  }
  
  static PageAct deleteFirstJournalEntryAndAcceptDialogIfPresent(final int timeOutInMilliSeconds) {
    return new PageAct("Delete the first journal record, if any") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator editButton = page.getByText("編集").nth(0);
        if (editButton.isVisible()) {
          editButton.waitFor(new Locator.WaitForOptions().setTimeout(timeOutInMilliSeconds));
          editButton.click();
          page.getByText("削除", new Page.GetByTextOptions().setExact(true)).nth(0).click();
          page.onceDialog(Dialog::accept);
          
          // This needs to be a shared unit to be re-used.
          page.getByText("削除", new Page.GetByTextOptions().setExact(true)).nth(0).click();
          page.locator(".ca-saving-cover").waitFor(new Locator.WaitForOptions().setState(HIDDEN));
        }
      }
    };
  }
}
