package jp.co.moneyforward.autotest.ca_web.tests.term;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

import java.util.function.Function;

@Tag("term")
@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {"screenshot"},
        value = {
            "createOffice"
        },
        afterEach = {"screenshot"}))
public class OfficeCreation extends CawebTermAccessingModel {
  @Named
  @DependsOn("login")
  @Export({"page", "officeName"})
  public static Scene createOffice() {
    String officeName = EXECUTION_PROFILE.officeName();
    LeafAct<Page, Page>[] acts = new LeafAct[]{
        navigateToTermSelection(),
        createOfficeViaNavis(officeName, EXECUTION_PROFILE.userDisplayName())};
    Scene.Builder builder = new Scene.Builder("page");
    builder.add("officeName", new LeafAct.Func<>((Function<Page, String>) page -> officeName), "page");
    for (LeafAct<Page, Page> act : acts) {
      builder.add(act);
    }
    return builder.build();
  }

  @Named
  @When("createOffice")
  public static Scene thenOfficeCreatedCorrectly() {
    return new Scene.Builder("page").build();
  }
}
