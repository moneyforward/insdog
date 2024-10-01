package jp.co.moneyforward.autotest.ca_web.tests.term;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.jupiter.api.Tag;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("term")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "deleteOffice"
        },
        afterEach = {"screenshot"}))
public class OfficeDeletion extends CawebTermAccessingModel {
  @Named
  @DependsOn("createOffice")
  @Export("page")
  public static Scene deleteOffice() {
    return new Scene.Builder("page")
        .add(navigateToTermSelection())
        .add(deleteLastOfficeInTable())
        .build();
  }
  
  static PageAct deleteLastOfficeInTable() {
    return new PageAct("Delete Office") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        /*
         The whitespace before 'ca-btn-delete-icon` is intentional because the SUT has it.
         */
        page.locator("(//img[@class=\" ca-btn-delete-icon\"])[last()]").click();
        page.getByText("事業者のデータがすべて削除されます（仕訳データ・勘定科目・メンバー設定など）").click();
        page.getByText("削除した事業者の復旧はできません").click();
        page.onceDialog(Dialog::accept);
        page.locator("//form/button[@type=\"submit\"]").click();
      }
    };
  }
  
  @Named
  @When("deleteOffice")
  public static Scene thenOfficeDeleted() {
    return Scene.fromActs("page", new PageAct("Check if office removal message becomes visible") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.getByText("事業者の退会が完了しました").waitFor();
      }
    });
  }
}
