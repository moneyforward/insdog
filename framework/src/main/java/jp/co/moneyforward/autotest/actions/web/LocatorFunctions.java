package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class that defines function returning utility methods.
 *
 * Utility methods in this class return a function whose parameter is `Locator`.
 * Typically, they are used in combination with methods in `PageFunctions`.
 *
 * For instance,
 *
 * ```
 *   new Click(locatorBySelector("#js-sidebar-opener").andThen(byText(sideMenuItem)))
 * ```
 *
 * This finds an element (locator) found by another function `PageFunctions#locatorBySelector(String)`, and then find an
 * element whose text is equal to `sideMenuItem`.
 *
 * Functions returned by methods in this class can be pretty printed on a call of `toString` method call.
 *
 * @see PageFunctions
 */
public enum LocatorFunctions {
  ;
  
  /**
   * A method to compose a function that returns an element at `i` th position in the element (locator) given as a parameter.
   *
   * @param i An index of an element to be returned.
   * @return A function that returns `i` th element in the given locator.
   */
  public static Function<Locator, Locator> nth(int i) {
    return Printables.function("nth[" + i + "]", l -> l.nth(i));
  }
  
  /**
   * Returns a function that finds an element whose text matches `text`, under a given `locator`.
   *
   * The returned function resolves a locator using `Locator#getByText(String)` method.
   *
   * @param text A string to be matched with the text of a given element.
   * @return A function that returns a locator which matches a given `text` under a locator.
   */
  public static Function<Locator, Locator> byText(String text) {
    requireNonNull(text);
    return Printables.function("byText[" + text + "]", l -> l.getByText(text));
  }
  
  
  /**
   * Returns a function that finds an element whose name matches `name`, under a given `locator`.
   *
   * The returned function resolves a locator using the following approach.
   *
   * ```java
   * (Locator l) -> l.getByRole(AriaRole.LINK,
   *                            new Locator.GetByRoleOptions().setName(name)
   *                                                          .setExact(!lenient))
   * ```
   *
   * @param name A string to be matched with the text of a given element.
   * @param lenient `true` - Make the search lenient / `false` - Make the search strict.
   * @return A function that returns a locator which matches a given `text` under a locator.
   */
  public static Function<Locator, Locator> byName(String name, boolean lenient) {
    requireNonNull(name);
    return Printables.function("@[name" + (lenient ? "~" : "=") + name + "]",
                               l -> l.getByRole(AriaRole.LINK,
                                                new Locator.GetByRoleOptions().setName(name)
                                                                              .setExact(!lenient)));
  }
  
  /**
   * Returns a function which gives a text content of an element given as an argument.
   *
   * @return A function which gives a text content of an element given as an argument.
   */
  public static Function<Locator, String> textContent() {
    return Printables.function("textContent", Locator::textContent);
  }
}
