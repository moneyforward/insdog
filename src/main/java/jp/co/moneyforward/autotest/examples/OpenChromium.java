package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class OpenChromium implements Act<Playwright, Browser> {
  @Override
  public Browser perform(Playwright value, ExecutionEnvironment executionEnvironment) {
    return value.chromium().launch();
  }
}
