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
        .add(ActUtils.helloAct())
        .build();
    
    Action action = scene.toAction(ActionUtils.createActionComposer(),
                                   "input",
                                   "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add(ActUtils.let("Scott Tiger"))
        .add(ActUtils.helloAct())
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenSceneWithVariableReadingActFailingAssertionAppended_whenToActionExecuted_thenActionTreeThatFailsIfPerformed() {
    Scene scene = new Scene.Builder()
        .add(ActUtils.let("John Doe"))
        .add(ActUtils.helloAct()
                     .assertion(x -> value(x).toBe()
                                             .equalTo("HELLO:John Doe!")))
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    assertStatement(value((assertThrows(AssertionFailedError.class,
                                        () -> ActionUtils.performAction(action)))).getMessage()
                                                                                  .toBe()
                                                                                  .containing("HELLO:John Doe!"));
  }
  
  
  @Test
  public void givenSceneWithVariableReadingActPassingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder()
        .add(ActUtils.let("John Doe"))
        .add(ActUtils.helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:John Doe")))
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenNestedSceneWithVariableReadingActPassingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder()
        .add("var1", ActUtils.let("John Doe"))
        .add(new Scene.Builder()
                 .assign("var1")
                 .add("var2",
                      ActUtils.helloAct()
                              .assertion(x -> value(x).toBe()
                                                      .equalTo("HELLO:John Doe")), "var1")
                 .build(), "input")
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "input", "output");
    
    ActionUtils.performAction(action);
  }
  
  @Test
  public void givenSequencedScenes_whenToActionExecuted_thenValueIsTakenOver() {
    Scene scene = new Scene.Builder("scene1")
        .add("var1", ActUtils.let("John Doe"))
        .add("seq1", new Scene.Builder()
            .assign("var1")
            .add("var2",
                 ActUtils.helloAct()
                         .assertion(x -> value(x).toBe()
                                                 .equalTo("HELLO:John Doe")),
                 "var1")
            .build(), "in")
        .add("seq2", new Scene.Builder()
            .assign("var2")
            .add("var3",
                 ActUtils.exclamationAct()
                         .assertion(x -> value(x).toBe().startingWith("HELLO:John Doe"))
                         .assertion(x -> value(x).toBe().endingWith("!")),
                 "var2")
            
            .build(), "seq1")
        .build();
    
    
    Action action = scene.toAction(ActionUtils.createActionComposer(), "in", "out");
    
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
