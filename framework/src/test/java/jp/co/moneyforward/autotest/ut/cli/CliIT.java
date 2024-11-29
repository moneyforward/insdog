package jp.co.moneyforward.autotest.ut.cli;

import jp.co.moneyforward.autotest.framework.cli.CliUtils;
import jp.co.moneyforward.autotest.framework.selftest.Index;
import jp.co.moneyforward.autotest.ut.cli.impl.CliImpl;
import jp.co.moneyforward.autotest.framework.selftest.Selftest;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import picocli.CommandLine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Printables.function;

/**
 * A class for `Cli` class's "IT" tests.
 *
 * Be aware that this is to be run by `mvn verify`.
 * Running from an IDE such as IDEA may result in unintended result because an IDE's test runner shows different behaviors
 * from when it is run from `mvn`.
 *
 * For instance, from IntelliJ IDEA, only one test fails and the others are skipped.
 */
class CliIT extends TestBase {
  
  @Test
  void testHelp() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("--help");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void testHelpFrom_Cli$main() {
    CliImpl.main("--help");
    // Always passing as long as it reaches here as the exit code is already checked by `testHelp` test method.
    assertStatement(value(true).toBe().equalTo(true));
  }
  
  @Test
  void runSelfTest() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("-q", "classname:~.*SelfTest.*", "run");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void runListTestClasses() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("list-testclasses");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void runListTagsWithInvalidArgs() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("-q", "classname??.*", "list-testclasses");
    assertStatement(value(exitCode).toBe().equalTo(2));
  }
  
  @Test
  void runListTags() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("list-tags");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  /**
   * This test just checks if the CommandLine#execute finishes without an error.
   */
  @Test
  void runSelfTestWithPartialMatch() {
    int exitCode = new CommandLine(new CliImpl()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                 .execute("-q", "classname:%Selftest", "run");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  
  @Test
  void runSelfTestThroughCliUtils() {
    List<TestIdentifier> testIdentifiers = new LinkedList<>();
    
    Map<Class<?>, TestExecutionSummary> testReport = CliUtils.runTests(
        Index.class.getPackageName(),
        new String[]{"classname:%Selftest"}, new String[]{},
        new String[]{},
        new SummaryGeneratingListener() {
          @Override
          public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            testIdentifiers.add(testIdentifier);
            super.executionFinished(testIdentifier, testExecutionResult);
          }
        });
    
    int numFailures = testReport.values()
                                .stream()
                                .map(s -> s.getFailures().size())
                                .reduce(Integer::sum)
                                .orElseThrow(NoSuchElementException::new);
    assertAll(value(numFailures).toBe().equalTo(0),
              value(testIdentifiers).elementAt(0)
                                    .function(functionGetDisplayName())
                                    .asString()
                                    .satisfies()
                                    .containing("connect"),
              value(testIdentifiers).elementAt(1)
                                    .function(functionGetDisplayName())
                                    .asString()
                                    .satisfies()
                                    .containing("printDomain"),
              value(testIdentifiers).elementAt(2)
                                    .function(functionGetDisplayName())
                                    .asString()
                                    .satisfies()
                                    .containing("disconnect"));
  }
  
  /**
   * This test is known to be not passing (fails) when executed from IDE (IntelliJ IDEA).
   */
  @Test
  void runSelfTestWithExecutionProfileThroughCliUtils() {
    Selftest.enableAssertion();
    try {
      List<TestIdentifier> testIdentifiers = new LinkedList<>();
      Map<Class<?>, TestExecutionSummary> testReport = CliUtils.runTests(
          "jp.co.moneyforward.autotest.ca_web.tests",
          new String[]{
              "classname:%SelfTest"
          },
          new String[]{},
          new String[]{
              String.format("--execution-profile=domain:%s", Selftest.OVERRIDING_DOMAIN_NAME)
          },
          new SummaryGeneratingListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
              testIdentifiers.add(testIdentifier);
              super.executionFinished(testIdentifier, testExecutionResult);
            }
          });
      
      int numFailures = testReport.values()
                                  .stream()
                                  .map(s -> s.getFailures().size())
                                  .reduce(Integer::sum)
                                  .orElseThrow(NoSuchElementException::new);
      assertAll(value(numFailures).toBe().equalTo(0),
                value(testIdentifiers).elementAt(0)
                                      .function(functionGetDisplayName())
                                      .asString()
                                      .satisfies()
                                      .containing("connect"),
                value(testIdentifiers).elementAt(1)
                                      .function(functionGetDisplayName())
                                      .asString()
                                      .satisfies()
                                      .containing("printDomain"),
                value(testIdentifiers).elementAt(2)
                                      .function(functionGetDisplayName())
                                      .asString()
                                      .satisfies()
                                      .containing("disconnect"));
    } finally {
      Selftest.disableAssertion();
    }
  }
  
  private static Function<TestIdentifier, String> functionGetDisplayName() {
    return function("getDisplayName", TestIdentifier::getDisplayName);
  }
  
  static class NoExitExecutionStrategy implements CommandLine.IExecutionStrategy {
    @Override
    public int execute(CommandLine.ParseResult parseResult) {
      return new CommandLine.RunLast() {
        @Override
        public int execute(CommandLine.ParseResult parseResult) throws CommandLine.ExecutionException {
          return 0;
        }
      }.execute(parseResult);
    }
  }
}
