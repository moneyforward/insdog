package jp.co.moneyforward.autotest.it;

import jp.co.moneyforward.autotest.it.t4t.Failing;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.List;

import static com.github.valid8j.fluent.Expectations.assertAll;
import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.it.CliITUtils.executeTestClass;

class FailingTestIT extends TestBase {
  @Test
  void runFailingTest() {
    var testReport = executeTestClass(Failing.class);
    
    int numFailures = testReport.getFailures().size();
    List<String> failedTests = testReport.getFailures().stream()
                                         .map(TestExecutionSummary.Failure::getTestIdentifier)
                                         .map(TestIdentifier::getDisplayName)
                                         .toList();
    assertAll(value(numFailures).toBe().equalTo(1),
              value(failedTests).elementAt(0).asString().toBe().containing("fail"),
              value(failedTests).size().toBe().equalTo(1));
  }
}
