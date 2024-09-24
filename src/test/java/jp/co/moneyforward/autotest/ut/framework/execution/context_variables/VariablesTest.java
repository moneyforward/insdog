package jp.co.moneyforward.autotest.ut.framework.execution.context_variables;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.*;
import jp.co.moneyforward.autotest.framework.action.AutotestSupport;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import jp.co.moneyforward.autotest.ututils.ActUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.*;
import static jp.co.moneyforward.autotest.ututils.ActUtils.*;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.*;

public class VariablesTest extends TestBase {
  public static SceneCall sceneCall(String outputFieldName, List<Call> children, List<Resolver> assignments) {
    var scene = scene(children);
    return AutotestSupport.sceneCall(outputFieldName, scene, assignments);
  }
  
  @Test
  void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    LinkedList<String> out = new LinkedList<>();
    Act<?, String> leaf = ActUtils.let("Scott Tiger");
    Scene scene = scene(List.of(actCall("x", leaf, "NONE"),
                                actCall("x", helloAct(), "x"),
                                actCall("x", printlnAct(), "x"),
                                actCall("x", addToListAct(out), "x")));
    
    
    Action action = AutotestSupport.sceneCall("output", scene, List.of()).toAction(createActionComposer(), AutotestSupport.sceneCall("output", scene, List.of()).assignmentResolvers().orElseThrow());
    
    performAction(action, Writer.Std.OUT);
    
    assertStatement(value(out).elementAt(0)
                              .asString()
                              .toBe()
                              .containing("HELLO")
                              .containing("Scott Tiger"));
  }
  
  @Test
  void takeOvers() {
    LinkedList<String> out = new LinkedList<>();
    Act<?, String> leaf = let("Scott Tiger");
    Scene scene = scene(List.of(
        sceneCall("SCENE1",
                  List.of(actCall("out", leaf, "NONE"),
                          actCall("x", helloAct(), "out")),
                  List.of()),
        sceneCall("SCENE2",
                  List.of(actCall("y", addToListAct(out), "in")),
                  List.of(new Resolver("in", Resolver.valueFrom("SCENE1", "x"))))));
    
    
    Action action = AutotestSupport.sceneCall("output", scene, List.of()).toAction(createActionComposer(), AutotestSupport.sceneCall("output", scene, List.of()).assignmentResolvers().orElseThrow());
    
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    actionPerformer.performAndReport(action, Writer.Std.OUT);
    
    assertStatement(value(out).elementAt(0)
                              .asString()
                              .toBe()
                              .containing("HELLO")
                              .containing("Scott Tiger"));
  }
  
  @Test
  void takeOvers2() {
    Act<?, String> leaf = let("Scott Tiger");
    Scene scene = scene(List.of(
        sceneCall("SCENE1",
                  List.of(actCall("out", leaf, "NONE"),
                          actCall("x", helloAct(), "out")),
                  List.of()),
        sceneCall("SCENE2",
                  List.of(AutotestSupport.assertionCall("y",
                                                        helloAct(),
                                                        List.of(x -> value(x).toBe()
                                                                             .startingWith("HELLO:")
                                                                             .containing("Scott")), "in")),
                  List.of(new Resolver("in", Resolver.valueFrom("SCENE1", "x"))))));
    
    Action action = AutotestSupport.sceneCall("OUT", scene, List.of()).toAction(createActionComposer(), AutotestSupport.sceneCall("OUT", scene, List.of()).assignmentResolvers().orElseThrow());
    performAction(action, Writer.Std.OUT);
  }
  
  @Test
  void action1() {
    SceneCall sceneCall = sceneCall("sceneOut",
                                    List.of(actCall("var", let("Scott"), "NONE"),
                                            actCall("var", helloAct(), "var"),
                                            actCall("var", printlnAct(), "var")),
                                    List.of());
    var out1 = new Writer.Impl();
    
    performAction(createActionComposer().create(sceneCall, sceneCall.assignmentResolvers().orElseThrow()), out1);
    
    assertStatement(value(toList(out1.iterator())).toBe().notEmpty());
  }
  
  
  @Test
  void action2() {
    Act<?, String> leaf = let("Scott");
    SceneCall sceneCall1 = sceneCall("S1",
                                     List.of(
                                         actCall("var", leaf, "NONE"),
                                         actCall("var", helloAct(), "var"),
                                         actCall("var", printlnAct(), "var")),
                                     List.of());
    SceneCall sceneCall2 = sceneCall("S2",
                                     List.of(actCall("var", helloAct(), "foo"),
                                             actCall("var", printlnAct(), "foo")),
                                     List.of(new Resolver("foo", Resolver.valueFrom("S1", "var"))));
    
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    var out1 = new Writer.Impl();
    var out2 = new Writer.Impl();
    
    performAction(createActionComposer().create(sceneCall1, sceneCall1.assignmentResolvers().orElseThrow()), actionPerformer, out1);
    performAction(createActionComposer().create(sceneCall2, sceneCall2.assignmentResolvers().orElseThrow()), actionPerformer, out2);
    
    assertAll(
        value(toList(out1.iterator())).toBe().notEmpty(),
        value(toList(out2.iterator())).toBe().notEmpty()
    );
  }
  
  @Test
  void action3() {
    SceneCall sceneCall1 = new SceneCall("S1",
                                         new Scene.Builder("sceneCall1").addCall(actCall("var", let("Scott"), "NONE"))
                                                                        .addCall(actCall("var", helloAct(), "var"))
                                                                        .addCall(actCall("var", printlnAct(), "var"))
                                                                        .build(), new HashMap<>());
    SceneCall sceneCall2 = new SceneCall("S2",
                                         new Scene.Builder("sceneCall2").addCall(actCall("foo", helloAct(), "foo"))
                                                                        .addCall(getStringStringAssertionActCall())
                                                                        .build(),
                                         composeMapFrom(InternalUtils.Entry.$("foo",
                                                                context -> context.<Map<String, Object>>valueOf("S1").get("var"))));
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    var out1 = new Writer.Impl();
    var out2 = new Writer.Impl();
    
    performAction(createActionComposer().create(sceneCall1, sceneCall1.assignmentResolvers().orElseThrow()), actionPerformer, out1);
    performAction(createActionComposer().create(sceneCall2, sceneCall2.assignmentResolvers().orElseThrow()), actionPerformer, out2);
    
    assertAll(
        value(toList(out1.iterator())).toBe().notEmpty(),
        value(toList(out2.iterator())).toBe().notEmpty()
    );
  }
  
  @SafeVarargs
  private static HashMap<String, Function<Context, Object>> composeMapFrom(InternalUtils.Entry<String, Function<Context, Object>>... entries) {
    HashMap<String, Function<Context, Object>> hashMap = new HashMap<>();
    for (InternalUtils.Entry<String, Function<Context, Object>> entry : entries) {
      hashMap.put(entry.key(), entry.value());
    }
    return hashMap;
  }
  
  private static AssertionCall<String> getStringStringAssertionActCall() {
    return new AssertionCall<>(new ActCall<>("foo", printlnAct(), "foo"),
                               List.of(s -> value(s).toBe()
                                                       .containing("HELLO")
                                                       .containing("Scott")));
  }
  
  private static <T> List<T> toList(Iterator<T> iterator) {
    List<T> ret = new ArrayList<>();
    while (iterator.hasNext()) {
      ret.add(iterator.next());
    }
    return ret;
  }
}
