package jp.co.moneyforward.autotest.lessons;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.annotations.When;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;

@AutotestExecution(defaultExecution = @Spec(
    value = "performFunction",
    planExecutionWith = DEPENDENCY_BASED))
public class LessonWhen extends LessonBase {
  @Export()
  @Named
  public Scene performTargetFunction() {
    return Scene.begin()
                .act(let("Hello!"))
                .end();
  }
  
  @Named
  @When("performFunction")
  public Scene thenDatabaseRecordUpdated() {
    return Scene.begin().add(wasDatabaseRecordUpdated()).end();
  }
  
  private Act<Object, Object> wasDatabaseRecordUpdated() {
    return let("Database record updated!");
  }
}
