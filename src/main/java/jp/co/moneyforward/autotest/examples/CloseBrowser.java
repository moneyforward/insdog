package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Browser;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class CloseBrowser implements Act<Browser, Object> {
  @Override
  public Object perform(Browser value, ExecutionEnvironment executionEnvironment) {
    value.close();
    return null;
  }
}
