package jp.co.moneyforward.autotest.ut.framework.execution.context_variables;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.ututils.ActUtils;
import org.junit.jupiter.api.Test;

import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.ututils.ActUtils.helloAct;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.createActionComposer;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.performAction;

public class VariablesTest {
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add(ActUtils.let("Scott Tiger"))
        .add("X", helloAct())
        .build();
    
    
    Action action = scene.toAction(createActionComposer(), "input", "output");
    
    performAction(action);
  }
  
  @Test
  public void takeOvers() {
    Scene scene = new Scene.Builder("top")
        .add("scene1", new Scene.Builder("SCENE1")
            .add(ActUtils.let("Scott Tiger"))
            .add("X", helloAct())
            .build())
        .add(new Scene.Builder("SCENE2")
                 .add("X", helloAct())
                 .build(), "scene1")
        .build();
    
    Action action = scene.toAction(createActionComposer(), "input", "output");
    
    performAction(action);
  }
  
  @Test
  public void takeOvers2() {
    Scene scene = new Scene.Builder("top")
        .add("scene1", new Scene.Builder("SCENE1")
            .add(ActUtils.let("Scott Tiger"))
            .add("X", helloAct())
            .build())
        .add(new Scene.Builder("SCENE2")
                 .assign("name", "_")
                 .add("X", helloAct().assertion(x -> value(x).toBe()
                                                             .startingWith("HELLO:")
                                                             .containing("Scott")), "name")
                 .build(), "scene1")
        .build();
    
    Action action = scene.toAction(createActionComposer(), "input", "output");
    
    performAction(action);
  }
}
