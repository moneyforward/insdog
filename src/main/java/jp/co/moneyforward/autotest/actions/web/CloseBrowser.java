package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Browser;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class CloseBrowser implements LeafAct<Browser, Void> {
  @Override
  public Void perform(Browser value, ExecutionEnvironment executionEnvironment) {
    value.close();
    return null;
  }
}
