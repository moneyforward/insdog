package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class that represents an action to send keys to a specified locator.
 */
public class SendKey implements LeafAct<Page, Page> {
  public static final String MASK_PREFIX = "MASK!";
  private final String keys;
  private final Function<Page, Locator> locatorFunction;
  
  /**
   * Creates an instance of this class.
   *
   * @param selector A selector string.
   * @param keys     Keys to be sent to a locator chosen by  a `selector` string.
   * @see SendKey#SendKey(Function, String)
   */
  public SendKey(String selector, String keys) {
    this(Printables.function("@" + selector, (Page p) -> p.locator(selector)), keys);
  }
  
  /**
   * Creates an instance of this class.
   *
   * @param locatorFunction A function to choose a locator from a given page.
   * @param keys            Keys to be sent to a chosen locator.
   */
  public SendKey(Function<Page, Locator> locatorFunction, String keys) {
    this.locatorFunction = requireNonNull(locatorFunction);
    this.keys = requireNonNull(keys);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    locatorFunction.apply(value).focus();
    value.keyboard().type(this.keys.startsWith(MASK_PREFIX) ? this.keys.substring(MASK_PREFIX.length())
                                                            : this.keys);
    return value;
  }
  
  @Override
  public String name() {
    return LeafAct.super.name() + "[" + locatorFunction + "][" +
        (keys.startsWith(MASK_PREFIX) ? MASK_PREFIX
                                      : keys) + "]";
  }
}
