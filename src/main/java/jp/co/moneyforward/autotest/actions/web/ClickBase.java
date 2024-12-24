package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.function.Function;

/// 
/// An abstract base class for clicking acts.
/// 
public abstract class ClickBase implements Act<Page, Page> {
  final Function<Page, Locator> locatorFunction;
  
  /// 
  /// Creates an object of this class.
  /// 
  /// @param locatorFunction A function to locate an element to click.
  /// 
  protected ClickBase(Function<Page, Locator> locatorFunction) {
    this.locatorFunction = locatorFunction;
  }
  
  /// 
  /// Returns a name of this object.
  /// The returned name is printed in action trees.
  /// 
  /// @return A name of this object.
  /// 
  @Override
  public String name() {
    return InternalUtils.simpleClassNameOf(this.getClass()) + "[" + this.locatorFunction + "]";
  }
}
