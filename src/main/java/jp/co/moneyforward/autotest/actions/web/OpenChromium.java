package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class OpenChromium implements LeafAct<Playwright, Browser> {
  @Override
  public Browser perform(Playwright value, ExecutionEnvironment executionEnvironment) {
    return value.chromium().launch();
  }
}
