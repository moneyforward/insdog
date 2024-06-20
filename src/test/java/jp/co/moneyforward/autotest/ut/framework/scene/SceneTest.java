package jp.co.moneyforward.autotest.ut.framework.scene;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.ututils.ActionUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static com.github.valid8j.pcond.forms.Predicates.containsString;
import static jp.co.moneyforward.autotest.framework.utils.AutotestSupport.sceneCall;
import static jp.co.moneyforward.autotest.ututils.ActUtils.helloAct;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.createActionComposer;

public class SceneTest extends TestBase {
  @Test
  public void givenEmptyScene_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene").build();
    
    
    List<String> out = new LinkedList<>();
    ActionUtils.performAction(createActionComposer().create(sceneCall("out",
                                                                      scene,
                                                                      List.of(new Resolver("in", c -> "Scott Tiger")))), createWriter(out));
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("END"))));
  }
  
  
  @Test
  public void givenSceneWithSingleAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add("out", helloAct(), "in")
        .build();
    
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    ActionUtils.performAction(createActionComposer().create(sceneCall("OUT",
                                                                      scene,
                                                                      List.of(new Resolver("in", c -> "Scott Tiger")))),
                              context,
                              createWriter(out));
    Expectations.assertAll(value(out).toBe()
                                     .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                        containsString("helloAct"),
                                                                        containsString("END"))),
                           value(context).invoke("valueOf", "OUT")
                                         .invoke("get", "out")
                                         .asString()
                                         .toBe()
                                         .containing("HELLO")
                                         .containing("Scott Tiger"));
  }
  
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add("in", let("Scott Tiger"), "in")
        .add("out", helloAct(), "in")
        .build();
    
    
    List<String> out = new LinkedList<>();
    ActionUtils.performAction(createActionComposer().create(sceneCall("out",
                                                                      scene,
                                                                      List.of())),
                              createWriter(out));
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("let"),
                                                                 containsString("helloAct"),
                                                                 containsString("END"))));
  }
  
  
  @Test
  public void givenSceneWithVariableReadingActPassingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder("scene")
        .add("out", let("John Doe"), "in")
        .add("out",
             helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:John Doe"))
                       .assertion(x -> value(x).toBe().containing("HELLO")),
             "out")
        .build();
    
    
    List<String> out = new LinkedList<>();
    ActionUtils.performAction(createActionComposer().create(sceneCall("out",
                                                                      scene,
                                                                      List.of())),
                              createWriter(out));
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("let"),
                                                                 containsString("helloAct"),
                                                                 containsString("assertion").and(containsString("stringIsEqualTo")),
                                                                 containsString("assertion").and(containsString("containsString")),
                                                                 containsString("END"))));
  }
  
  @Test
  public void givenSceneWithVariableReadingActFailingAssertionAppended_whenToActionExecuted_thenActionTreeThatPassesIfPerformed() {
    Scene scene = new Scene.Builder("scene")
        .add("out", let("John Doe"), "in")
        .add("out", helloAct().assertion(x -> value(x).toBe().equalTo("HELLO:Scott Tiger")), "in")
        .build();
    
    
    List<String> out = new LinkedList<>();
    ActionUtils.performAction(createActionComposer().create(sceneCall("out",
                                                                      scene,
                                                                      List.of(new Resolver("in", c -> "Scott Tiger")))), createWriter(out));
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("let"),
                                                                 containsString("helloAct"),
                                                                 containsString("assertion"),
                                                                 containsString("END"))));
  }
  
  public static ExecutionEnvironment createExecutionEnvironment() {
    return AutotestEngine.createExecutionEnvironment(SceneTest.class.getCanonicalName());
  }
  
  private static Writer createWriter(List<String> out) {
    return s -> {
      System.err.println(s);
      out.add(s);
    };
  }
}
