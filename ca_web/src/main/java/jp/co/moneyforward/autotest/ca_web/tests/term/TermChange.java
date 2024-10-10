package jp.co.moneyforward.autotest.ca_web.tests.term;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.actions.web.TableQuery;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static jp.co.moneyforward.autotest.actions.web.TableQuery.Term.term;

@Tag("term")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {"changeToNextTerm", "changeToPreviousTerm"},
        afterEach = {"screenshot"}))
public class TermChange extends CawebTermAccessingModel {
  @Named
  @DependsOn("createOffice")
  @Export("page")
  public static Scene changeToPreviousTerm() {
    String officeName = executionProfile().officeName();
    return previousTerm(officeName);
  }
  
  @Named
  @When("changeToPreviousTerm")
  public static Scene thenChangedToPreviousTerm() {
    return Scene.fromActs("page", new PageAct("changeToPreviousTerm") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.getByText("前期に移動しました")).isVisible();
      }
    });
  }
  
  @Named
  @DependsOn("changeToPreviousTerm")
  @Export("page")
  public static Scene changeToNextTerm() {
    String officeName = executionProfile().officeName();
    return nextTerm(officeName);
  }
  
  
  @Named
  @When("changeToNextTerm")
  public static Scene thenChangedToNextTerm() {
    return Scene.fromActs("page", new PageAct("changeToPreviousTerm") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        assertThat(page.getByText("事業者・年度を切替えました")).isVisible();
      }
    });
  }
  
  private static Scene previousTerm(final String officeName) {
    return new Scene.Builder("page")
        .add(new PageAct("Change to previous term") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            officeDropDownLocatorFor(page).click();
            getLink(page).click();
            
            page.onceDialog(Dialog::accept);
            TableQuery.select("事業者・年度の切替")
                      .from("#js-ca-main-contents > table")
                      .normalizeWith(normalizerFunctionForOfficeTable())
                      .where(term("事業者名", officeName),
                             term("会計年度", "前年度"))
                      .$()
                      .perform(page)
                      .getFirst()
                      .getByText("切替")
                      .click();
          }
        })
        .build();
  }
  
  public static Scene nextTerm(final String officeName) {
    return new Scene.Builder("page")
        .add(new PageAct("Change to next term") {
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            officeDropDownLocatorFor(page).click();
            getLink(page).click();
            
            page.onceDialog(Dialog::accept);
            TableQuery.select("事業者・年度の切替")
                      .from("#js-ca-main-contents > table")
                      .normalizeWith(normalizerFunctionForOfficeTable())
                      .where(term("事業者名", officeName),
                             term("会計年度", "2024")) // TODO: We need to come up with a way to avoid hard code the year for the "next year"
                      .$()
                      .perform(page)
                      .getFirst()
                      .getByText("切替")
                      .click();
            assertThat(page.getByText("事業者・年度を切替えました")).isVisible();
          }
        })
        .build();
  }
  
  public static BinaryOperator<List<Locator>> normalizerFunctionForOfficeTable() {
    return (lastCompleteRow, incompleteRow) -> {
      List<Locator> ret = new ArrayList<>(lastCompleteRow.size());
      for (int i = 0; i < lastCompleteRow.size(); i++) {
        int offset = lastCompleteRow.size() - incompleteRow.size() - 1;
        ret.add((i < offset || i == lastCompleteRow.size() - 1) ? lastCompleteRow.get(i)
                                                                : incompleteRow.get(i - (offset)));
      }
      return ret;
    };
  }
  
  public static Locator getLink(Page page) {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("事業者・年度の管理"));
  }
}

