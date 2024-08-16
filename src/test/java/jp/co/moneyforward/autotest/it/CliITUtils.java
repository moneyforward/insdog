package jp.co.moneyforward.autotest.it;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public enum CliITUtils {
  ;
  
  public static TestExecutionSummary executeTestClass(Class<?>... testClasses) {
    Launcher launcher = LauncherFactory.create();
    LauncherDiscoveryRequestBuilder requestBuilder = request();
    for (Class<?> c : testClasses) {
      requestBuilder.selectors(selectClass(c));
    }
    SummaryGeneratingListener testExecutionListener = new SummaryGeneratingListener();
    launcher.execute(requestBuilder.build(), testExecutionListener);
    return testExecutionListener.getSummary();
  }
}
