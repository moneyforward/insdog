package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import com.github.dakusui.actionunit.visitors.SimpleActionPerformer;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import org.junit.jupiter.api.TestTemplate;

@AutotestExecution
public interface AutotestRunner {
  default void beforeAll(Action action) {
    performAction(action);
  }
  
  default void beforeEach(Action action) {
    performAction(action);
  }
  
  @TestTemplate
  default void runTestAction(Action action) {
    performActionWithReporting(action);
  }
  
  default void afterEach(Action action) {
    performAction(action);
  }
  
  default void afterAll(Action action) {
    performAction(action);
  }
  
  default void performActionWithReporting(Action action) {
    ReportingActionPerformer.create().performAndReport(action, createWriter());
  }
  
  default void performAction(Action action) {
    action.accept(SimpleActionPerformer.create());
  }
  
  default Writer createWriter() {
    return Writer.Std.OUT;
  }
}
