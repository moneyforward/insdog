package jp.co.moneyforward.autotest.ut.framework.scene;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.action.AutotestSupport;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.ututils.ActionUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Predicates.containsString;
import static jp.co.moneyforward.autotest.ututils.ActUtils.helloAct;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.createActionComposer;

public class SceneTest extends TestBase {
  
  @Test
  void whenChainActs() {
    Scene scene = Scene.chainActs("testField", helloAct(), helloAct());
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).size().toBe().equalTo(2));
  }
  
  @Test
  public void givenEmptyScene_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene").build();
    
    
    List<String> out = new LinkedList<>();
    final jp.co.moneyforward.autotest.framework.action.SceneCall sceneCall = AutotestSupport.sceneCall("out",
                                                                                                       scene,
                                                                                                       List.of(new Resolver("in", c -> "Scott Tiger")));
    ActionUtils.performAction(createActionComposer().create(sceneCall, sceneCall.assignmentResolvers().orElseThrow()), createWriter(out));
    Assumptions.assumeFalse(false);
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
    final jp.co.moneyforward.autotest.framework.action.SceneCall sceneCall = AutotestSupport.sceneCall("OUT",
                                                                                                       scene,
                                                                                                       List.of(new Resolver("in", c -> "Scott Tiger")));
    ActionUtils.performAction(createActionComposer().create(sceneCall, sceneCall.assignmentResolvers().orElseThrow()),
                              context,
                              createWriter(out));
    assertAll(value(out).toBe()
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
    ActionUtils.performAction(createActionComposer().create(AutotestSupport.sceneCall("out",
                                                                                      scene,
                                                                                      List.of()), AutotestSupport.sceneCall("out",
                                                                                                                            scene,
                                                                                                                            List.of()).assignmentResolvers().orElseThrow()),
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
    ActionUtils.performAction(createActionComposer().create(AutotestSupport.sceneCall("out",
                                                                                      scene,
                                                                                      List.of()), AutotestSupport.sceneCall("out",
                                                                                                                            scene,
                                                                                                                            List.of()).assignmentResolvers().orElseThrow()),
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
    final jp.co.moneyforward.autotest.framework.action.SceneCall sceneCall = AutotestSupport.sceneCall("out",
                                                                                                       scene,
                                                                                                       List.of(new Resolver("in", c -> "Scott Tiger")));
    ActionUtils.performAction(createActionComposer().create(sceneCall, sceneCall.assignmentResolvers().orElseThrow()),
                              createWriter(out));
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
