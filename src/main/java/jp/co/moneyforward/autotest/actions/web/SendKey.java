package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class SendKey implements LeafAct<Page, Page> {
  private final String keys;
  private final Function<Page, Locator> locatorFunction;
  
  public SendKey(String selector, String keys) {
    this(Printables.function("@" + selector, (Page p) -> p.locator(selector)), keys);
  }
  
  public SendKey(Function<Page, Locator> locatorFunction, String keys) {
    this.locatorFunction = requireNonNull(locatorFunction);
    this.keys = requireNonNull(keys);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    locatorFunction.apply(value).focus();
    value.keyboard().type(this.keys);
    return value;
  }
}
