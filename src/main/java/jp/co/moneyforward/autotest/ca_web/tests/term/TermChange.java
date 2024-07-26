package jp.co.moneyforward.autotest.ca_web.tests.term;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("term")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "changeTerm"
        },
        afterEach = {"screenshot"}))
public class TermChange extends CawebTermAccessingModel {
  @Named
  @DependsOn("createOffice")
  @Export("page")
  public static Scene changeTerm() {
    return new Scene.Builder("page")
        .add(previousTerm())
        .add(nextTerm())
        .build();
  }
  
  public static Scene previousTerm() {
    return new Scene.Builder("page")
        .add(new PageAct("Change to previous term") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(") ")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("事業者・年度の管理")).click();
            page.onceDialog(Dialog::dismiss);
            int countElements = page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("スペシャルサンドボックス合同会社(法人) 前年度 切替")).getByRole(AriaRole.CELL).count();
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("abc（法人）前年度 切替")).getByRole(AriaRole.CELL).nth(countElements).click();
            //page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("abc（法人）")).getByRole(AriaRole.LINK).first().click();
            assertThat(page.getByText("前期に移動しました")).isVisible();
          }
        })
        .build();
  }
  
  public static Scene nextTerm() {
    return new Scene.Builder("page")
        .add(new PageAct("Change to previous term") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("スペシャルサンドボックス合同会社(法人) ")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("事業者・年度の管理")).click();
            page.onceDialog(Dialog::dismiss);
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("スペシャルサンドボックス合同会社(法人)")).getByRole(AriaRole.LINK).first().click();
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("スペシャルサンドボックス合同会社(法人） 次年度 切替")).getByRole(AriaRole.CELL).nth(1).click();
            assertThat(page.getByText("事業者・年度を切替えました")).isVisible();
          }
        })
        .build();
  }
  
}

