package jp.co.moneyforward.autotest.ut.framework.scene;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import jp.co.moneyforward.autotest.framework.action.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.ututils.ActionUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Predicates.containsString;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.actCall;
import static jp.co.moneyforward.autotest.ututils.ActUtils.helloAct;
import static jp.co.moneyforward.autotest.ututils.ActUtils.let;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.createActionComposer;

public class SceneTest extends TestBase {
  
  @Test
  void whenSceneByChainingActs() {
    Scene scene = Scene.fromActs("testField", helloAct(), helloAct());
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).size().toBe().equalTo(2));
  }
  
  @Test
  void givenEmptyScene_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene").build();
    
    
    List<String> out = new LinkedList<>();
    final List<Resolver> in = List.of(new Resolver("in", c -> "Scott Tiger"));
    final jp.co.moneyforward.autotest.framework.action.SceneCall sceneCall = (SceneCall) AutotestSupport.sceneToSceneCall("out",
                                                                                                                          scene,
                                                                                                                          new ResolverBundle(in));
    ActionUtils.performAction(createActionComposer().create(sceneCall), createWriter(out));
    Assumptions.assumeFalse(false);
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("END"))));
  }
  
  @Test
  void givenSceneWithSingleAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene").add("out", helloAct(), "in").build();
    
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    final var sceneCall = AutotestSupport.sceneToSceneCall("OUT",
                                                           scene,
                                                           new ResolverBundle(List.of(new Resolver("in", c -> "Scott Tiger"))));
    ActionUtils.performAction(createActionComposer().create((SceneCall) sceneCall),
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
  void givenNestedSceneCall_whenPerformed_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add(new Scene.Builder("inner").add("out", helloAct(), "in").build())
        .build();
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    final var sceneCall = AutotestSupport.sceneToSceneCall("OUT",
                                                           scene,
                                                           new ResolverBundle(List.of(new Resolver("in", c -> "Scott Tiger"))));
    
    ActionUtils.performAction(createActionComposer().create((SceneCall) sceneCall),
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
  void givenDoubleNestedSceneCall_whenPerformed_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add(new Scene.Builder("inner1").add(new Scene.Builder("inner").add("out", helloAct(), "in").build()).build())
        .build();
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    final var sceneCall = AutotestSupport.sceneToSceneCall("OUT",
                                                           scene,
                                                           new ResolverBundle(List.of(new Resolver("in", c -> "Scott Tiger"))));
    
    ActionUtils.performAction(createActionComposer().create((SceneCall) sceneCall),
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
  void givenNestedSceneCallWithRetry_whenPerformed_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add(new Scene.Builder("inner").retry(actCall("out", helloAct(), "in")).build())
        .build();
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    final var sceneCall = AutotestSupport.sceneToSceneCall("OUT",
                                                           scene,
                                                           new ResolverBundle(List.of(new Resolver("in", c -> "Scott Tiger"))));
    
    ActionUtils.performAction(createActionComposer().create((SceneCall) sceneCall),
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
  void givenNestedSceneWithRetry_whenPerformed_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add(new Scene.Builder("inner").retry(Scene.fromActs("in", helloAct())).build())
        .build();
    List<String> out = new LinkedList<>();
    Context context = Context.create();
    final var sceneCall = AutotestSupport.sceneToSceneCall("OUT",
                                                           scene,
                                                           new ResolverBundle(List.of(new Resolver("in", c -> "Scott Tiger"))));
    
    ActionUtils.performAction(createActionComposer().create(sceneCall),
                              context,
                              createWriter(out));
    
    assertAll(value(out).toBe()
                        .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                           containsString("helloAct"),
                                                           containsString("END"))),
              value(context).invoke("valueOf", "OUT")
                            .invoke("get", "in")
                            .asString()
                            .toBe()
                            .containing("HELLO")
                            .containing("Scott Tiger"));
  }
  
  @Test
  void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    Scene scene = new Scene.Builder("scene")
        .add("in", let("Scott Tiger"), "in")
        .add("out", helloAct(), "in")
        .build();
    
    
    List<String> out = new LinkedList<>();
    ActionUtils.performAction(createActionComposer().create((SceneCall) AutotestSupport.sceneToSceneCall("out",
                                                                                                         scene,
                                                                                                         new ResolverBundle(List.of()))),
                              createWriter(out));
    assertStatement(value(out).toBe()
                              .containingElementsInOrder(List.of(containsString("BEGIN"),
                                                                 containsString("let"),
                                                                 containsString("helloAct"),
                                                                 containsString("END"))));
  }
  
  @Test
  void givenSceneWithVariableReadingAct_whenOutputVariableNames_thenOutputVariableNamesReturned() {
    Scene scene = new Scene.Builder("scene")
        .add("in", let("Scott Tiger"), "in")
        .add("out", helloAct(), "in")
        .build();
    
    List<String> out = scene.outputVariableNames();
    
    assertStatement(value(out).toBe().equalTo(List.of("in", "out")));
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
