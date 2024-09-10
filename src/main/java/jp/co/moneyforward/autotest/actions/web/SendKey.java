package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class that represents an action to send key sequence to a specified locator.
 *
 * If the key sequence starts with `SendKey.MASK_PREFIX`, the part after it will be sent to an element specified by a `locatorFunction` in a given page,
 * while the part before it (i.e., the `MASK_PREFIX` itselft) will be printed in the log.
 * This feature is useful for secret strings to be sent to elements, but not to be printed such as passwords.
 *
 */
public class SendKey implements LeafAct<Page, Page> {
  /**
   * A prefix to control a
   */
  public static final String MASK_PREFIX = "MASK!";
  private final Supplier<String> keySequenceGenerator;
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
    this(locatorFunction, toSupplier(requireNonNull(keys)));
  }
  
  /**
   * Creates an instance of this class.
   *
   * @param locatorFunction      A function to choose a locator from a given page.
   * @param keySequenceGenerator A supplier that generates a key sequence to be sent to a chosen locator.
   */
  public SendKey(Function<Page, Locator> locatorFunction, Supplier<String> keySequenceGenerator) {
    this.locatorFunction = requireNonNull(locatorFunction);
    this.keySequenceGenerator = requireNonNull(keySequenceGenerator);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    this.locatorFunction.apply(value).focus();
    String keys = this.keySequenceGenerator.get();
    value.keyboard().type(keys.startsWith(MASK_PREFIX) ? keys.substring(MASK_PREFIX.length())
                                                       : keys);
    return value;
  }
  
  @Override
  public String name() {
    String keys = keySequenceGenerator.get();
    return LeafAct.super.name() + "[" + locatorFunction + "][" +
        (keys.startsWith(MASK_PREFIX) ? MASK_PREFIX
                                      : keys) + "]";
  }
  
  private static Supplier<String> toSupplier(String keys) {
    return () -> keys;
  }
}
