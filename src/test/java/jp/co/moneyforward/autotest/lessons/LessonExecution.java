package jp.co.moneyforward.autotest.lessons;

import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.annotations.Named;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.PASSTHROUGH;

@AutotestExecution(
    defaultExecution = @Spec(
        value = "aSceneMethod",
        planExecutionWith = PASSTHROUGH
    ))
public class LessonExecution extends LessonBase {
  @Named
  public Scene aSceneMethod() {
    return Scene.begin().end();
  }
}
