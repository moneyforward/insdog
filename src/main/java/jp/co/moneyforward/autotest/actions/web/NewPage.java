package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class NewPage implements LeafAct<Browser, Page> {
  @Override
  public Page perform(Browser value, ExecutionEnvironment executionEnvironment) {
    return value.newPage();
  }
}
