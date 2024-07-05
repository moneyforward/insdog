package jp.co.moneyforward.autotest.ut.framework.execution.context_variables;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.*;
import jp.co.moneyforward.autotest.framework.utils.AutotestSupport;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.ututils.ActUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.framework.utils.AutotestSupport.*;
import static jp.co.moneyforward.autotest.ututils.ActUtils.*;
import static jp.co.moneyforward.autotest.ututils.ActionUtils.*;

public class VariablesTest extends TestBase {
  @Test
  public void givenSceneWithVariableReadingAct_whenToActionExecuted_thenActionTreeLooksCorrect() {
    LinkedList<String> out = new LinkedList<>();
    LeafAct<?, String> leaf = ActUtils.let("Scott Tiger");
    Scene scene = scene(List.of(leafCall("x", leaf, "NONE"),
                                leafCall("x", helloAct(), "x"),
                                leafCall("x", printlnAct(), "x"),
                                leafCall("x", addToListAct(out), "x")));
    
    
    Action action = sceneCall("output", scene, List.of()).toAction(createActionComposer());
    
    performAction(action, Writer.Std.OUT);
    
    assertStatement(value(out).elementAt(0)
                              .asString()
                              .toBe()
                              .containing("HELLO")
                              .containing("Scott Tiger"));
  }
  
  @Test
  public void takeOvers() {
    LinkedList<String> out = new LinkedList<>();
    LeafAct<?, String> leaf = let("Scott Tiger");
    Scene scene = scene(List.of(
        sceneCall("SCENE1",
                  List.of(leafCall("out", leaf, "NONE"),
                          leafCall("x", helloAct(), "out")),
                  List.of()),
        sceneCall("SCENE2",
                  List.of(leafCall("y", addToListAct(out), "in")),
                  List.of(new Resolver("in", Resolver.valueFrom("SCENE1", "x"))))));
    
    
    Action action = sceneCall("output", scene, List.of()).toAction(createActionComposer());
    
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    actionPerformer.performAndReport(action, Writer.Std.OUT);
    
    assertStatement(value(out).elementAt(0)
                              .asString()
                              .toBe()
                              .containing("HELLO")
                              .containing("Scott Tiger"));
  }
  
  @Test
  public void takeOvers2() {
    LeafAct<?, String> leaf = let("Scott Tiger");
    Scene scene = scene(List.of(
        sceneCall("SCENE1",
                  List.of(leafCall("out", leaf, "NONE"),
                          leafCall("x", helloAct(), "out")),
                  List.of()),
        sceneCall("SCENE2",
                  List.of(AutotestSupport.assertionCall("y",
                                                        helloAct(),
                                                        List.of(x -> value(x).toBe()
                                                                             .startingWith("HELLO:")
                                                                             .containing("Scott")), "in")),
                  List.of(new Resolver("in", Resolver.valueFrom("SCENE1", "x"))))));
    
    Action action = sceneCall("OUT", scene, List.of()).toAction(createActionComposer());
    performAction(action, Writer.Std.OUT);
  }
  
  @Test
  public void action1() {
    SceneCall sceneCall = sceneCall("sceneOut",
                                    List.of(leafCall("var", let("Scott"), "NONE"),
                                                 leafCall("var", helloAct(), "var"),
                                                 leafCall("var", printlnAct(), "var")),
                                    List.of());
    performAction(createActionComposer().create(sceneCall), Writer.Std.OUT);
  }
  
  
  @Test
  public void action2() {
    LeafAct<?, String> leaf = let("Scott");
    SceneCall sceneCall1 = sceneCall("S1",
                                     List.of(
                                              leafCall("var", leaf, "NONE"),
                                              leafCall("var", helloAct(), "var"),
                                              leafCall("var", printlnAct(), "var")),
                                     List.of());
    SceneCall sceneCall2 = sceneCall("S2",
                                     List.of(leafCall("var", helloAct(), "foo"),
                                                  leafCall("var", printlnAct(), "foo")),
                                     List.of(new Resolver("foo", Resolver.valueFrom("S1", "var"))));
    
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    performAction(createActionComposer().create(sceneCall1), actionPerformer, Writer.Std.OUT);
    performAction(createActionComposer().create(sceneCall2), actionPerformer, Writer.Std.OUT);
  }
  
  @Test
  public void action3() {
    SceneCall sceneCall1 = new SceneCall("S1",
                                         new Scene.Builder("sceneCall1").addCall(leafCall("var", let("Scott"), "NONE"))
                                                                                  .addCall(leafCall("var", helloAct(), "var"))
                                                                                  .addCall(leafCall("var", printlnAct(), "var"))
                                                                                  .build(), new HashMap<>());
    SceneCall sceneCall2 = new SceneCall("S2",
                                         new Scene.Builder("sceneCall2").addCall(leafCall("foo", helloAct(), "foo"))
                                                                                  .addCall(getStringStringAssertionActCall())
                                                                                  .build(),
                                         new HashMap<>() {{
                                                     this.put("foo", new Function<Context, Object>() {
                                                       @Override
                                                       public Object apply(Context context) {
                                                         return context.<Map<String, Object>>valueOf("S1").get("var");
                                                       }
                                                     });
                                                   }});
    
    ReportingActionPerformer actionPerformer = createReportingActionPerformer();
    performAction(createActionComposer().create(sceneCall1), actionPerformer, Writer.Std.OUT);
    performAction(createActionComposer().create(sceneCall2), actionPerformer, Writer.Std.OUT);
  }
  
  private static AssertionActCall<String, String> getStringStringAssertionActCall() {
    return new AssertionActCall<>(new LeafActCall<>("foo", printlnAct(), "foo"),
                                  List.of(s -> value(s).toBe()
                                                            .containing("HELLO")
                                                            .containing("Scott")));
  }
}
