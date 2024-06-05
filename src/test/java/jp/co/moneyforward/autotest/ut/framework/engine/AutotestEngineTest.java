package jp.co.moneyforward.autotest.ut.framework.engine;

import jp.co.moneyforward.autotest.ut.testclasses.FailureContainingTestbed;
import jp.co.moneyforward.autotest.ut.testclasses.AllPassingTestbed;
import jp.co.moneyforward.autotest.ut.testclasses.EmptyTestbed;
import jp.co.moneyforward.autotest.ututils.TestBase;
import jp.co.moneyforward.autotest.ututils.TestResultValidatorExtension;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;

import static jp.co.moneyforward.autotest.ututils.TestResultValidatorExtension.forTestMatching;

public class AutotestEngineTest extends TestBase {
  @Test
  public  void examineAutotestEngineCanRunAndReportSuccessfulTestResultsAsDesigned() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, FailureContainingTestbed.class);
  }
  
  @Test
  public  void examineAutotestEngineCanRunAndReportTestResultsContainingFailureAsDesigned() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, AllPassingTestbed.class);
  }
  
  @Test
  public  void examineAutotestEngineCanRunAndReportTestResultsForEmptyTestClass() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    runTests(validator, EmptyTestbed.class);
  }
  
  private static void runTests(TestResultValidatorExtension validator, Class<?> testClass) {
    // Register the listener
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                                                                      .selectors(DiscoverySelectors.selectClass(testClass))
                                                                      .build();
    
    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(validator, summaryListener);
    launcher.execute(request);
    
    // Print the summary
    TestExecutionSummary summary = summaryListener.getSummary();
    summary.printTo(new PrintWriter(System.out));
  }
}
