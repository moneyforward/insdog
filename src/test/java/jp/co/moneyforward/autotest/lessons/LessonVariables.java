package jp.co.moneyforward.autotest.lessons;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

import java.util.LinkedHashMap;

import static com.github.valid8j.pcond.forms.Printables.function;
import static java.util.Objects.requireNonNull;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.createContext;

@AutotestExecution(defaultExecution = @AutotestExecution.Spec(
                              value = "performTargetFunction",
                  planExecutionWith = DEPENDENCY_BASED))
public class LessonVariables extends LessonBase {
  @Named
  @Export
  public Scene openBasePage() {
    return Scene.begin()
                .add(openNewPage())
                .end();
  }
  
  @Named
  @Export({"page", "childPage"})
  @DependsOn("openBasePage")
  public Scene performTargetFunction() {
    return Scene.begin()
                .add(clickButton1())
                .add("childPage", openChildPage())
                .add(Scene.begin("childPage")
                          .act(screenshot())
                          .end())
                .end();
  }
  
  @Named
  @Export()
  @When("performTargetFunction")
  public Scene thenClickButton2() {
    return Scene.begin()
                .add(clickButton2())
                .end();
  }
  
  @Named
  @Export()
  @When("performTargetFunction")
  public Scene thenClickButton3() {
    return Scene.begin("childPage")
                .add(clickButton3())
                .end();
  }
  
  private Act<Object, String> openNewPage() {
    return new Act.Let<>("newPage");
  }
  
  private Act<Object, Object> screenshot() {
    return new Act.Func<>(function("screenshot", o -> "screenshot:[" + printVariableValue(o) + "]"));
  }
  
  private Act<Object, Object> clickButton1() {
    return new Act.Func<>(function("clickButton1", o -> "clickButton1:[" + printVariableValue(o) + "]"));
  }
  
  private Act<Object, Object> clickButton2() {
    return new Act.Func<>(function("clickButton2", o -> "clickButton2:[" + printVariableValue(o) + "]"));
  }
  
  private Act<Object, Object> clickButton3() {
    return new Act.Func<>(function("clickButton3", o -> "clickButton3:[" + printVariableValue(o) + "]"));
  }
  
  private Act<Object, Object> openChildPage() {
    return new Act.Func<>(function("openChildPage", o -> "openChildPage:[" + printVariableValue(o) + "]"));
  }
  
  private static Object printVariableValue(Object o) {
    System.out.println(o);
    return requireNonNull(o);
  }
}
