package jp.co.moneyforward.autotest.ut.sandbox;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.ut.misc.ExecutionCompiler;
import jp.co.moneyforward.autotest.ut.misc.Play;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.ut.misc.CompileScenarioWith;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.ut.misc.PlayScenario;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Disabled
//@Configure()
@ExtendWith(AutotestEngine.class)
public class ActionSandbox extends TestBase {
  @BeforeAll
  public static void beforeAll() {
  }

//  @SetUpEach
  public Scene beforeEach() {
    return new Scene.Builder().build();
  }
  
  @PlayScenario
  public static Play givenSimpleActionStructureCreatedByPlay_whenPerform_thenOutputIsObserved() {
    return new Play.Builder().build();
  }
  
  @CompileScenarioWith
  public static ExecutionCompiler defaultCompiler() {
    return new ExecutionCompiler.Default();
  }
  
  @AutotestExecution
  public void test1(Action action) {
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
  @Test
  @AutotestExecution
  public void test2(Action action) {
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
}
