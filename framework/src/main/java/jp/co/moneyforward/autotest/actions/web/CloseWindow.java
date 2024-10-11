package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class CloseWindow implements Act<Playwright, Void> {
  @Override
  public Void perform(Playwright value, ExecutionEnvironment executionEnvironment) {
    value.close();
    return null;
  }
}
