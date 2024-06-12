package jp.co.moneyforward.autotest.ut.framework.scene;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.ca_web.core.Credentials;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.ututils.ActUtils;
import jp.co.moneyforward.autotest.ututils.ActionUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SceneTest extends TestBase {
  @Test
  public void givenEmptyScene_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder().build();
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenSceneWithSingleAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add("out", ActUtils.helloAct(), "in")
        .build();
    
    Action action = scene.toAction(ActionUtils.createActionComposer(),
                                   "input",
                                   "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add("out", ActUtils.let("Scott Tiger"), "in")
        .add("out", ActUtils.helloAct(), "in")
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  
  @Test
  public void givenSceneWithVariableReadingActPassingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder()
        .add("out", ActUtils.let("John Doe"), "in")
        .add("out", ActUtils.helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:John Doe")), "in")
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  public static ExecutionEnvironment createExecutionEnvironment() {
    return new ExecutionEnvironmentForCa() {
      @Override
      public String endpointRoot() {
        return "http://www.example.com";
      }
      
      @Override
      public Credentials credentials() {
        return new Credentials() {
        
        };
      }
    };
  }
}
