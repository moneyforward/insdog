package jp.co.moneyforward.autotest.ut.framework.scene;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.Credentials;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.HashMap;

import static com.github.valid8j.fluent.Expectations.value;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SceneTest extends TestBase {
  @Test
  public void givenEmptyScene_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder().build();
    
    Action action = scene.toAction(ActionComposer.createActionComposer(scene.name().orElse("TODO"), createExecutionEnvironment()), "input", "output");
    
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
  
  @Test
  public void givenSceneWithSingleAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add(helloAct())
        .build();
    
    Action action = scene.toAction(ActionComposer.createActionComposer(scene.name().orElse("TODO"), createExecutionEnvironment()), "input", "output");
    
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
  
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder()
        .add(let("Scott Tiger"))
        .add(helloAct())
        .build();
    
    
    Action action = scene.toAction(ActionComposer.createActionComposer(scene.name().orElse("TODO"), createExecutionEnvironment()), "input", "output");
    
    ReportingActionPerformer.create()
                            .performAndReport(action, Writer.Std.OUT);
  }
  
  @Test
  public void givenSceneWithVariableReadingActFailingAssertionAppended_whenToActionExecuted_thenActionTreeThatFailsIfPerformed() {
    Scene scene = new Scene.Builder()
        .add(let("John Doe"))
        .add(helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:John Doe!")))
        .build();
    
    
    Action action = scene.toAction(ActionComposer.createActionComposer(scene.name().orElse("TODO"), createExecutionEnvironment()), "input", "output");
    
    assertThrows(AssertionFailedError.class, () -> {
      ReportingActionPerformer.create()
                              .performAndReport(action, Writer.Std.OUT);
    });
  }
  
  @Test
  public void givenSceneWithVariableReadingActPassingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder()
        .add(let("John Doe"))
        .add(helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:John Doe")))
        .build();
    
    
    Action action = scene.toAction(ActionComposer.createActionComposer(scene.name().orElse("TODO"), createExecutionEnvironment()), "input", "output");
    
    ReportingActionPerformer.create()
                            .performAndReport(action, Writer.Std.OUT);
  }
  
  private static <T> Act.Let<T> let(T value) {
    return new Act.Let<>(value);
  }
  
  private static Act<String, String> helloAct() {
    return (value, executionEnvironment) -> "HELLO:" + value;
  }
  
  private static ExecutionEnvironment createExecutionEnvironment() {
    return new ExecutionEnvironment() {
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
