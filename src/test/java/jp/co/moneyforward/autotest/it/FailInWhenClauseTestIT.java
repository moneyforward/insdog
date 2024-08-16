package jp.co.moneyforward.autotest.it;

import jp.co.moneyforward.autotest.it.t4t.FailInWhenClause;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

import static com.github.valid8j.fluent.Expectations.assertAll;
import static com.github.valid8j.fluent.Expectations.value;

class FailInWhenClauseTestIT extends TestBase {
  @Test
  void runTestFailingInWhenClause() {
    var testReport = CliITUtils.executeTestClass(FailInWhenClause.class);
    
    int numFailures = testReport.getFailures().size();
    List<String> failedTests = testReport.getFailures().stream()
                                         .map(TestExecutionSummary.Failure::getTestIdentifier)
                                         .map(TestIdentifier::getDisplayName)
                                         .toList();
    long numSucceeded = testReport.getTestsSucceededCount();
    assertAll(value(numFailures).toBe().equalTo(1),
              value(numSucceeded).toBe().equalTo(1L),
              value(failedTests).elementAt(0).asString().toBe().containing("thenFail"),
              value(failedTests).size().toBe().equalTo(1));
  }
  
}
