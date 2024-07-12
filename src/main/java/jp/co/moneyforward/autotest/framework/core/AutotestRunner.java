package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutotestExecution
public interface AutotestRunner {
  Logger LOGGER = LoggerFactory.getLogger(AutotestRunner.class);
  
  default void beforeAll(Action action, Writer writer) {
    performActionWithReporting(action, writer);
  }
  
  default void beforeEach(Action action, Writer writer) {
    performActionWithReporting(action, writer);
  }
  
  @TestTemplate
  default void runTestAction(String name, Action action) {
    var out = new ArrayList<String>();
    String stageName = "value:";
    boolean succeeded = false;
    try {
      performActionWithReporting(action, createWriter(out));
      succeeded = true;
    } finally {
      LOGGER.info(String.format("%-11s [%s]%s", stageName, succeeded ? "o" : "E", name));
      out.forEach(l -> LOGGER.info(String.format("%-11s %s", stageName, l)));
    }
  }
  
  default void afterEach(Action action, Writer writer) {
    performActionWithReporting(action, writer);
  }
  
  default void afterAll(Action action, Writer writer) {
    performActionWithReporting(action, writer);
  }
  
  default void performActionWithReporting(Action action, Writer writer) {
    actionPerformer().performAndReport(action, writer);
  }
  
  default Writer createWriter(List<String> out) {
    return s -> {
      Writer.Slf4J.DEBUG.writeLine(s);
      out.add(s);
    };
  }
  
  ReportingActionPerformer actionPerformer();
}
