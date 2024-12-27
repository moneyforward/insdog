package jp.co.moneyforward.autotest.lessons;


import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;

import java.util.function.Consumer;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;

@AutotestExecution(defaultExecution = @Spec(
    value = "sceneMethod",
    planExecutionWith = DEPENDENCY_BASED))
public class LessonDependsOn extends LessonBase {
  @Export
  @Named
  public Scene setUpMethod() {
    return Scene.begin()
                .act(let("InsDog"))
                .act(sink(System.out::println))
                .end();
  }
  
  @DependsOn("setUpMethod")
  @Named
  public Scene sceneMethod() {
    return Scene.begin()
                .act(let("InsDog"))
                .act(sink(System.out::println))
                .end();
  }
  
  public static Act.Sink<Object> sink(Consumer<Object> consumer) {
    return new Act.Sink<>(consumer);
  }
}