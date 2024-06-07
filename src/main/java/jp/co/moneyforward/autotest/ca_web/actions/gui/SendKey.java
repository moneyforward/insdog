package jp.co.moneyforward.autotest.ca_web.actions.gui;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class SendKey implements Act<Page, Page> {
  private final String selector;
  private final String keys;
  
  public SendKey(String selector, String keys) {
    this.selector = selector;
    this.keys = keys;
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.focus(this.selector);
    value.keyboard().type(this.keys);
    return value;
  }
}
