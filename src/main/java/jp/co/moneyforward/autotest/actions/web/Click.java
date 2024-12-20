package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

/// 
/// An act that models a user behavior, which clicks a specified element.
/// 
/// 
/// 
/// 
public class Click extends ClickBase {
  /// 
  /// Creates an object of this class.
  /// @param selector A selector to designate an element to click.
  /// 
  public Click(String selector) {
    this(Printables.function("@" + selector, p -> p.locator(selector)));
  }
  
  /// 
  /// Creates an objct of this class.
  /// 
  /// @param locatorFunction A locator for an element to click.
  /// 
  public Click(Function<Page, Locator> locatorFunction) {
    super(locatorFunction);
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    this.locatorFunction.apply(page).click();
    return page;
  }
}
