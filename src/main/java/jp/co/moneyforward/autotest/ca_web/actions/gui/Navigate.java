package jp.co.moneyforward.autotest.ca_web.actions.gui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class Navigate implements Act<Browser, Page> {
  private final String url;
  
  public Navigate(String url) {
    this.url = requireNonNull(url);
  }
  
  @Override
  public Page perform(Browser value, ExecutionEnvironment executionEnvironment) {
    Page page = value.newPage();
    page.navigate(this.url);
    return page;
  }
}
