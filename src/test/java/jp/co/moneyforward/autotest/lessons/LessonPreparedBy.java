package jp.co.moneyforward.autotest.lessons;

import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.lessons.LessonDependsOn.sink;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;

@AutotestExecution(defaultExecution = @Spec(
    value = "performScenario",
    planExecutionWith = DEPENDENCY_BASED))
public class LessonPreparedBy extends LessonBase {
  @Export
  @Named
  public Scene login() {
    return Scene.begin()
                .act(let("login"))
                .act(sink(System.out::println))
                .end();
  }
  
  @Export
  @Named
  @PreparedBy({"toHomeScreen"})
  @PreparedBy({"loadLoginSession", "toHomeScreen"})
  @PreparedBy({"login", "saveLoginSession"})
  public Scene isLoggedIn() {
    return Scene.begin()
                .act(let("isLoggedIn"))
                .act(sink(System.out::println))
                .end();
  }
  
  @Named
  @DependsOn("isLoggedIn")
  public Scene performScenario() {
    return Scene.begin()
                .act(let("performScenario"))
                .act(sink(System.out::println))
                .end();
  }
  
  @Export
  @Named
  public Scene toHomeScreen() {
    return Scene.begin().act(let("toHomeScreen")).end();
  }
  
  @Export
  @Named
  public Scene loadLoginSession() {
    return Scene.begin().act(let("loadLoginSession")).end();
  }
  
  @Export
  @Named
  public Scene saveLoginSession() {
    return Scene.begin().act(let("saveLoginSession")).end();
  }
}