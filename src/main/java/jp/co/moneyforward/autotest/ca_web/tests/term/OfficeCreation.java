package jp.co.moneyforward.autotest.ca_web.tests.term;

import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.annotations.When;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

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
  @When("createOffice")
  public static Scene thenOfficeCreatedCorrectly() {
    return new Scene.Builder("page").build();
  }
}
