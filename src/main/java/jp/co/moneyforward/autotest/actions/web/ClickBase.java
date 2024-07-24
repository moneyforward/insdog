package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;

import java.util.function.Function;

public abstract class ClickBase implements LeafAct<Page, Page> {
  final Function<Page, Locator> locatorFunction;
  
  protected ClickBase(Function<Page, Locator> locatorFunction) {
    this.locatorFunction = locatorFunction;
  }
  
  @Override
  public String name() {
    return this.getClass().getSimpleName() + "[" + this.locatorFunction + "]";
  }
}
