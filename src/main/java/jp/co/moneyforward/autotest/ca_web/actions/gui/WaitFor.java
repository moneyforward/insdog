package jp.co.moneyforward.autotest.ca_web.actions.gui;


import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

/**
 * An act that waits for a given condition is met.
 * You can set maximum time to wait and interval to check the condition.
 */
public class WaitFor implements Act<Page, Page> {
  private final String selector;
  
  public WaitFor(String selector) {
    this.selector = selector;
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.waitForSelector(this.selector).innerHTML();
    return value;
  }
}
