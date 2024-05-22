package jp.co.moneyforward.autotest.ut.sandbox;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.ExecutionCompiler;
import jp.co.moneyforward.autotest.framework.action.Play;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.ActionTest;
import jp.co.moneyforward.autotest.framework.annotations.CompileScenarioWith;
import jp.co.moneyforward.autotest.framework.annotations.Configure;
import jp.co.moneyforward.autotest.framework.annotations.SetUpEach;import jp.co.moneyforward.autotest.framework.testengine.PlayScenario;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Disabled;

@Disabled
@Configure()
public class ActionStructureTest extends TestBase {
  @SetUpEach
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
  
  @ActionTest
  public void test(Action action) {
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
}
