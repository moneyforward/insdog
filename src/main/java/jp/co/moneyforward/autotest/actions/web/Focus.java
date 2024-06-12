package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Focus implements LeafAct<Page, Page> {
  private final String locator;
  
  public Focus(String locator) {
    this.locator = locator;
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.focus(this.locator);
    return value;
  }
}
