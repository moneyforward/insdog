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
  
  default List<String> beforeAll(Action action) {
    return performActionWithReporting(action);
  }
  
  default List<String> beforeEach(Action action) {
    return performActionWithReporting(action);
  }
  
  @TestTemplate
  default void runTestAction(String name, Action action) {
    boolean succeeded = false;
    try {
      performActionWithReporting(action).forEach(s -> LOGGER.info("{}: {}", name, s));
    } finally {
    
    }
  }
  
  default List<String> afterEach(Action action) {
    return performActionWithReporting(action);
  }
  
  default List<String> afterAll(Action action) {
    return performActionWithReporting(action);
  }
  
  default List<String> performActionWithReporting(Action action) {
    var out = new ArrayList<String>();
    actionPerformer().performAndReport(action, createWriter(out));
    return out;
  }
  
  default Writer createWriter(List<String> out) {
    return s -> {
      Writer.Slf4J.DEBUG.writeLine(s);
      out.add(s);
    };
  }
  
  ReportingActionPerformer actionPerformer();
}
