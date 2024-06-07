package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class CloseWindow implements Act<Playwright, Object> {
  @Override
  public Object perform(Playwright value, ExecutionEnvironment executionEnvironment) {
    value.close();
    return null;
  }
}
