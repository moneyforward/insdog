package jp.co.moneyforward.autotest.ut.framework.core;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import static com.github.valid8j.fluent.Expectations.assertStatement;

class AutotestRunnerTest extends TestBase {
  @Test
  void givenNotSucceeding_whenComposeTestResultMessage_thenCorrectlyComposed() {
    Class<? extends AutotestRunner> c = createAutotestRunner().getClass();
    String message = AutotestRunner.composeResultMessage(c, "stage1", "test1", false);
    
    System.out.println(message);
    assertStatement(Expectations.value(message).toBe()
                                .containing("AutotestRunner")
                                .containing("stage1")
                                .containing("test1")
                                .containing("E"));
  }
  
  @SuppressWarnings("Convert2Lambda")
  private static AutotestRunner createAutotestRunner() {
    return new AutotestRunner() {
      @Override
      public ReportingActionPerformer actionPerformer() {
        return new ReportingActionPerformer() {
        };
      }
    };
  }
}
