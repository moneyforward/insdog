package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class Screenshot implements LeafAct<Page, Page> {
  
  public Screenshot() {
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.screenshot(new Page.ScreenshotOptions().setPath(executionEnvironment.testOutputFilenameFor("screenshot.png")));
    return value;
  }
}
