package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.ActionFactory;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

public class Click implements LeafAct<Page, Page> {
  private final Function<Page, Locator> locatorFunction;
  
  public Click(String selector) {
    this(Printables.function("@" + selector, p -> p.locator(selector)));
  }
  
  public Click(Function<Page, Locator> locatorFunction) {
    this.locatorFunction = locatorFunction;
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    this.locatorFunction.apply(page).click();
    return page;
  }
  
  @Override
  public String name() {
    return this.getClass().getSimpleName() + "[" + this.locatorFunction + "]";
  }
}
