package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class NewPage implements Act<Browser, Page> {
  @Override
  public Page perform(Browser value, ExecutionEnvironment executionEnvironment) {
    return value.newPage();
  }
}
