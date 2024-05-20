package jp.co.moneyforward.autotest.ca_web.actions.gui;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Click implements Act<Page, Page> {
  private final String locatorString;
  
  public Click(String locatorString) {
    this.locatorString = locatorString;
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    page.click(locatorString);
    return page;
  }
}
