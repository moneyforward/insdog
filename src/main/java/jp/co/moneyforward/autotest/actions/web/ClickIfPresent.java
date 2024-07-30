package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

/**
 * A class that represents an act, where:
 *
 * * Click a specified element exists in a page if it exists.
 * * Otherwise, does nothing.
 *
 * Check for presence is done by `Locator#isVisible`.
 */
public class ClickIfPresent extends ClickBase {
  /**
   * Creates an object of this class.
   *
   * @param locatorFunction A function to locate an element to be clicked by this object on `perform` method's call.
   */
  public ClickIfPresent(Function<Page, Locator> locatorFunction) {
    super(locatorFunction);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    Locator targetElement = this.locatorFunction.apply(value);
    if (targetElement.isVisible())
      targetElement.click();
    return value;
  }
}
