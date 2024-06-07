package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class OpenWindow implements Act<Object, Playwright> {
  @Override
  public Playwright perform(Object value, ExecutionEnvironment executionEnvironment) {
    return Playwright.create();
  }
}
