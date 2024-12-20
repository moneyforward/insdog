package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

/// 
/// An act that does screenshot.
/// The browser screenshot is saved under `ExecutionEnvironment#testOutputFilenameFor("screenshot-{stepName}.png")`, where
/// `{stepName}` is one of `beforeAll`, `beforeEach`, `afterEach`, or `afterAll`.
/// 
/// @see ExecutionEnvironment#testOutputFilenameFor(String)
/// 
public class Screenshot implements Act<Page, Page> {
  /// 
  /// Creates an instance of this class.
  /// 
  public Screenshot() {
     // Make default constructor findable.
  }
  
  /// 
  /// Performs the screenshot action.
  /// 
  /// @param value A page for which screenshot is executed.
  /// @param executionEnvironment An execution environment.
  /// @return The page object itself given as `value` parameter.
  /// 
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.screenshot(new Page.ScreenshotOptions().setPath(executionEnvironment.testOutputFilenameFor(String.format("screenshot-%s.png", executionEnvironment.stepName()))));
    return value;
  }
}
