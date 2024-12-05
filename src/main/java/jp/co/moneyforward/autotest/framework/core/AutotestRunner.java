package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.composeResultMessageLine;

/**
 * An interface that runs specified actions for stages: `beforeAll`, `beforeEach`, `tests`, `afterEach`, and `afterAll`.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutotestExecution
public interface AutotestRunner {
  /**
   * A logger to record the activity of this object.
   */
  Logger LOGGER = LoggerFactory.getLogger(AutotestRunner.class);
  
  /**
   * Performs a given `action` for **beforeAll** stage and writes a report of the activity to `writer`
   *
   * @param action An action to be performed as `beforeAll` stage.
   * @param writer A writer through which report of the `action` will be written.
   */
  default void beforeAll(Action action, Writer writer) {
    actionPerformer().performAndReport(action, writer);
  }
  
  /**
   * Performs a given `action` for **beforeEach** stage and writes a report of the activity to `writer`
   *
   * @param action An action to be performed as `beforeEach` stage.
   * @param writer A writer through which report of the `action` will be written.
   */
  default void beforeEach(Action action, Writer writer) {
    actionPerformer().performAndReport(action, writer);
  }
  
  /**
   * A template that defines the overall scenario of a test.
   *
   * @param name   A name of a test.
   * @param action An action executed as a test.
   */
  @TestTemplate
  default void runTestAction(String name, Action action) {
    var out = new ArrayList<String>();
    String stageName = "value";
    boolean succeeded = false;
    try {
      Writer writer = createWriter(out);
      actionPerformer().performAndReport(action, writer);
      succeeded = true;
    } finally {
      String message = composeResultMessage(this.getClass(), stageName, name, succeeded);
      LOGGER.info(message);
      out.forEach(l -> LOGGER.info(composeResultMessageLine(this.getClass(), stageName, l)));
    }
  }
  
  /**
   * Performs a given `action` for **afterEach** stage and writes a report of the activity to `writer`
   *
   * @param action An action to be performed.
   * @param writer A writer through which a report is written.
   */
  default void afterEach(Action action, Writer writer) {
    actionPerformer().performAndReport(action, writer);
  }
  
  /**
   * Performs a given `action` for **afterAll** stage and writes a report of the activity to `writer`
   *
   * @param action An action to be performed.
   * @param writer A writer through which a report is written.
   */
  default void afterAll(Action action, Writer writer) {
    actionPerformer().performAndReport(action, writer);
  }
  
  /**
   * Creates a writer through which action activities are reported.
   *
   * @param out A list to which a copy of the report is appended.
   * @return A writer through which action report is written.
   */
  default Writer createWriter(List<String> out) {
    return s -> {
      Writer.Slf4J.INFO.writeLine(s);
      out.add(s);
    };
  }
  
  /**
   * Creates a reporting action performer.
   * A class of reporting action performers is defined in **actionunit** library.
   *
   * @return A reporting action performer.
   */
  ReportingActionPerformer actionPerformer();
  
  /**
   * Composes a line in a report.
   *
   * @param aClass    A test class.
   * @param stageName A name of a stage such as `beforeAll`, `beforeEach`.
   * @param testName  A name of a test.
   * @param succeeded `true` - succeeded / `false` - otherwise.
   * @return A composed result message.
   */
  static String composeResultMessage(Class<? extends AutotestRunner> aClass, String stageName, String testName, boolean succeeded) {
    return String.format("%-20s: %-11s [%s]%s", InternalUtils.simpleClassNameOf(aClass), stageName + ":", succeeded ? "o" : "E", testName);
  }
}
