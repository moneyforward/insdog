package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.github.valid8j.classic.Requires.requireNonNull;

public abstract class PageAct implements LeafAct<Page, Page> {
  private final String description;
  
  public PageAct(String description) {
    this.description = requireNonNull(description);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    this.action(value, executionEnvironment);
    return value;
  }
  
  protected abstract void action(Page page, ExecutionEnvironment executionEnvironment);
  
  @Override
  public String name() {
    return "Page[" + this.description + "]";
  }
}
