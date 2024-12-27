package jp.co.moneyforward.autotest.lessons;

import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;

@AutotestExecution(defaultExecution = @AutotestExecution.Spec(
    value = "performScenario",
    planExecutionWith = DEPENDENCY_BASED))
public class LessonClosedBy extends LessonBase {
  @Export
  @Named
  @ClosedBy("closeExecutionSession")
  public Scene openExecutionSession() {
    return Scene.begin().act(let("openExecutionSession")).end();
  }
  
  @Named
  @DependsOn("openExecutionSession")
  public Scene performScenario() {
    return Scene.begin().act(let("openExecutionSession")).end();
  }

  @Named
  @DependsOn("openExecutionSession")
  public Scene closeExecutionSession() {
    return Scene.begin().act(let("closeExecutionSession")).end();
  }
}
