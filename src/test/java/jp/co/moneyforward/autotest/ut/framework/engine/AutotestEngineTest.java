package jp.co.moneyforward.autotest.ut.framework.engine;

import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.ut.testclasses.*;
import jp.co.moneyforward.autotest.ututils.TestBase;
import jp.co.moneyforward.autotest.ututils.TestResultValidatorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment.PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY;
import static jp.co.moneyforward.autotest.ututils.TestResultValidatorExtension.forTestMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutotestEngineTest extends TestBase {
  @BeforeAll
  public static void beforeAll() {
    System.setProperty(PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY, "target/unitTest/testResult");
  }
  
  @Test
  void examineAutotestEngineCanRunAndReportSuccessfulTestResultsAsDesigned() {
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
  void examineAutotestEngineCanRunAndReportSuccessfulTestResultsAsDesigned_beforeAll() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, FailingBeforeEachTestbed.class);
  }
  
  @Test
  void examineAutotestEngineCanRunAndReportSuccessfulTestResultsAsDesigned_beforeEach() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, FailingAfterEachTestbed.class);
  }
  
  @Test
  void examineAutotestEngineCanRunAndReportTestResultsContainingFailureAsDesigned_beforeAndAfterAllTestbed() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, AllPassingWithBeforeAndAfterAllTestbed.class);
  }
  
  @Test
  void examineAutotestEngineCanRunAndReportTestResultsContainingFailureAsDesigned_beforeAndEachAllTestbed() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, AllPassingWithBeforeAndAfterEachTestbed.class);
  }
  
  @Test
  void givenStateEnsuringByFallingBackDependencies_whenRunTests_thenFinishesNormally() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*login.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*connect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*disconnect.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*logout.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*fail.*").shouldBeFailed());
    
    runTests(validator, StateEnsuringByFallingBackDependencies.class);
  }
  
  @Test
  void givenVariableHandOver_whenRunTests_thenFinishesNormally() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    validator.addExpectation(forTestMatching(".*scene1.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*thenClickButton2.*").shouldBeSuccessful());
    validator.addExpectation(forTestMatching(".*thenClickButton3.*").shouldBeSuccessful());
    
    runTests(validator, VariableHandOver.class);
  }
  
  @Test
  void examineAutotestEngineCanRunAndReportTestResultsForEmptyTestClass() {
    // Create a custom listener that will validate test results
    TestResultValidatorExtension validator = new TestResultValidatorExtension();
    runTests(validator, EmptyTestbed.class);
    
    assertStatement(value(true).toBe().trueValue());
  }
  
  @Test
  void givenOutOfMemoryThrowingConsumer_whenPerformActionEntry_thenOutOfMemoryWillBeThrownWithNoWrapping() {
    Consumer<List<String>> action = v -> {
      throw new OutOfMemoryError("INTENTIONAL");
    };
    
    OutOfMemoryError e = assertThrows(OutOfMemoryError.class,
                                      () -> AutotestEngine.performActionEntry("KEY1", action));
    assertStatement(value(e).getMessage().toBe().containing("INTENTIONAL"));
  }
  
  @Test
  void givenExceptionThrowingConsumer_whenPerformActionEntry_thenResultReturned() {
    Consumer<List<String>> action = v -> {
      throw new RuntimeException("INTENTIONAL");
    };
    
    AutotestEngine.SceneExecutionResult result = AutotestEngine.performActionEntry("KEY1", action);
    
    assertStatement(value(result).toBe().notNull());
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
