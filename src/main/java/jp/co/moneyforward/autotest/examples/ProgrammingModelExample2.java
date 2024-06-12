package jp.co.moneyforward.autotest.examples;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import org.junit.jupiter.api.Disabled;

import java.util.HashMap;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {},
        beforeEach = {},
        value = {"scene1", "scene2", "scene3", "scene4"},
        afterEach = {},
        afterAll = {},
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
public class ProgrammingModelExample2 implements AutotestRunner {
  private final ReportingActionPerformer actionPerformer = new ReportingActionPerformer(Context.create(), new HashMap<>());
  
  @Named
  public static Scene scene1() {
    return new Scene.Builder()
        .add("var1", new LeafAct.Let<>("Hello!")).build();
  }
  
  @Disabled
  @Named
  public static Scene scene2() {
    return new Scene.Builder()
        .add("", new Scene.Builder().add("", new LeafAct.Sink<>(System.out::println)).build())
        .build();
  }
  
  @Disabled
  @Named
  public static Scene scene3() {
    return new Scene.Builder()
        .add("", new LeafAct.Sink<>(System.out::println), "var")
        .build();
  }
  
  @Named
  public static Scene scene4() {
    return new Scene.Builder()
        .add("", new Scene.Builder().add("", new LeafAct.Sink<>(System.out::println), "var").build())
        .build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return actionPerformer;
  }
}
/*

 END:Context:(
 {
 out:SCENE3={_=null},
 out:SCENE2={},
 out:SCENE1={var1=Hello!},
 SCENE2={},
 SCENE3={_=null},
 SCENE1={var1=Hello!},
 out:SCENE4={},
 ONGOING_EXCEPTIONS={}, SCENE4={}, Scene={var1=Hello!, _=null}, _={var1=Hello!, _=null}}; parent=null)
*/