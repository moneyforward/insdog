package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Screenshot implements LeafAct<Page, Page> {
  
  public Screenshot() {
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.screenshot(new Page.ScreenshotOptions().setPath(executionEnvironment.testOutputFilenameFor(String.format("screenshot-%s.png", executionEnvironment.stepName()))));
    return value;
  }
}
