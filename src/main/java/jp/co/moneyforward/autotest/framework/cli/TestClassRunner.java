package jp.co.moneyforward.autotest.framework.cli;

import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public interface TestClassRunner {
  TestExecutionSummary runTestClass(Class<?> testClass);
  
  static TestClassRunner create() {
    return testClass -> {
      Launcher launcher = LauncherFactory.create();
      LauncherDiscoveryRequest request = request().selectors(selectClass(testClass))
                                                  .build();
      SummaryGeneratingListener listener = new SummaryGeneratingListener();
      launcher.execute(request, listener);
      return listener.getSummary();
    };
  }
}
