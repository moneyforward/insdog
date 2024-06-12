package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class Navigate implements LeafAct<Page, Page> {
  private final String url;
  
  public Navigate(String url) {
    this.url = requireNonNull(url);
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    page.navigate(this.url);
    return page;
  }
  
  @Override
  public String name() {
    return this.getClass().getSimpleName() + "[" + this.url + "]";
  }
}
