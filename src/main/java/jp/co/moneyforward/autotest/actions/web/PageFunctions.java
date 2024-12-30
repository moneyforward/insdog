package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

///
/// A utility class to handle `Page` object.
///
/// Methods in this class return a function whose parameter is a `Page` object of **Playwright**.
///
/// It is common to see a situation, where a single method call to a page object cannot determine a single element to be returned.
/// In such a case, you can use functions provided by `LocatorFunctions` in combination.
///
/// ```
/// new Click(PageFunctions.locatorBySelector("#js-sidebar-opener").andThen(LocatorFunctions.byText(sideMenuItem)))
/// ```
///
/// This is an example to find a locator, which is specified by a text `sideMenuItem` under a locator specified by a selector
/// string `#js-sidebar-opener`.
///
/// In general methods in this class are named in the following manner.
///
/// ```
/// {typeName}By{SelectionMethod}
/// ```
///
/// `typeName` can be, for instance, `locator`, `linkLocator`.
/// `SelectionMethod` can be `Name`, `Text`, `Label`, `Selector`, etc.
///
/// Functions returned by methods in this class can be pretty printed on a call of `toString` method call.
///
/// @see LocatorFunctions
///
public enum PageFunctions {
  ;
  
  ///
  /// Returns a function that resolves a locator specified by `name` in a given `Page` object.
  ///
  /// This is a shorthand method for `linkLocatorByName(name, false)`.
  ///
  /// @param name A name of a link.
  /// @return A function that resolves a locator specified by `name` in a given `Page` object.
  ///
  public static Function<Page, Locator> linkLocatorByName(String name) {
    return linkLocatorByName(name, false);
  }
  
  ///
  /// Returns a function that resolves a given `name` to a locator of a link whose name matches with it.
  ///
  /// @param name    A name of a link locator to be matched.
  /// @param lenient `true` - partial match / `false` - exact match.
  /// @return A function that resolves a given `name` to a locator of a link whose name matches with it.
  ///
  public static Function<Page, Locator> linkLocatorByName(String name, boolean lenient) {
    return Printables.function("link[name" + (lenient ? "~" : "=") + name + "]",
                               p -> p.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setExact(!lenient)
                                                                                          .setName(name)));
  }
  
  ///
  /// Returns a function that resolves a locator whose text contains `text` in a given page.
  ///
  /// @param text A text to be contained by the matching locator.
  /// @return a function that resolves a locator whose text contains `text` in a given page.
  ///
  public static Function<Page, Locator> locatorByText(String text) {
    return locatorByText(text, false);
  }
  
  ///
  /// Returns a function that resolves a locator which matches `text` in a given `Page` object.
  /// If `lenient` is `true`, a locator whose text contains it is considered matched.
  /// If `lenient` is `false`, a locator whose text equals to `text` is considered matched.
  ///
  /// The match is done by:
  ///
  /// ```Page#GetByText(text, new Page.GetByTextOptions().setExact(!lenient)```
  ///
  /// @param text    A text to be matched.
  /// @param lenient `true` - lenient / `false` - strict.
  /// @return A function that resolves a locator which matches `text` in a given `Page` object.
  ///
  public static Function<Page, Locator> locatorByText(String text, boolean lenient) {
    return Printables.function("@[text" + (lenient ? "~" : "=") + text + "]",
                               p -> p.getByText(text, new Page.GetByTextOptions().setExact(!lenient)));
  }
  
  ///
  /// Returns a function that resolve a locator to a button object in a `Page`, whose name is equal to `name`.
  ///
  /// @param name A string to be matched with a locator's name.
  /// @return A  function that resolve a locator to a button object in a `Page`, whose name is equal to `name`.
  ///
  public static Function<Page, Locator> buttonLocatorByName(String name) {
    return Printables.function("@[name=" + name + "]",
                               p -> p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)
                                                                                            .setExact(true)));
  }
  
  
  ///
  /// Returns a function that resolves a locator whose label matches with `label` in a given `Page`.
  ///
  /// @param label A string to be matched with a label of a locator.
  /// @return A function that resolves a locator whose label matches with `label`.
  ///
  public static Function<Page, Locator> locatorByLabel(String label) {
    return locatorByLabel(label, false);
  }
  
  ///
  /// Returns a function that resolves a locator whose label matches with `label` in a given `Page`.
  ///
  /// The resolution is done by a following method-call.
  ///
  /// ```java
  /// Page#getByLabel(label, new Page.GetByLabelOptions().setExact(!lenient))
  /// ```
  ///
  /// If `lenient`is set to `true`, a locator whose label contains `label` will be considered matched.
  /// If it is `false`, a locator whose label is equal to `label` will be considered matched.
  ///
  /// @param label   A string to be matched with a label of a locator.
  /// @param lenient `true` - lenient / `false` - strict.
  /// @return a function that resolves a locator whose label matches with `label` in a given `Page`.
  ///
  public static Function<Page, Locator> locatorByLabel(String label, boolean lenient) {
    return Printables.function("@[label" + (lenient ? "~" : "=") + label + "]",
                               p -> p.getByLabel(label, new Page.GetByLabelOptions().setExact(!lenient)));
  }
  
  
  ///
  /// Returns a function that resolves a locator whose placeholder is `placeholder`.
  ///
  /// @param placeholder A string to be matched with a locator's placeholder.
  /// @return A function that resolves a locator whose placeholder is `placeholder`.
  ///
  public static Function<Page, Locator> locatorByPlaceholder(String placeholder) {
    return Printables.function("@[placeholder=" + placeholder + "]",
                               p -> p.getByPlaceholder(placeholder, new Page.GetByPlaceholderOptions().setExact(true)));
  }
  
  ///
  /// Returns a function that resolves a locator specified by `selector` in a given `Page`.
  ///
  /// @param selector A selector string that specifies a locator.
  /// @return A function that resolves a locator specified by `selector` in a given `Page`.
  ///
  public static Function<Page, Locator> locatorBySelector(String selector) {
    requireNonNull(selector);
    return Printables.function("@[" + selector + "]", p -> p.locator(selector));
  }
  
  ///
  /// Returns a function that gives a locator of a link whose text contains a given `text` inside a `Page`.
  ///
  /// @param text A string to be matched with a text of a link in a given `Page.
  /// @return A function that gives a locator of a link whose text matches a given `text` inside a `Page`.
  ///
  public static Function<Page, Locator> linkLocatorByText(String text) {
    return linkLocatorByText(text, true);
  }
  
  ///
  /// Returns a function that gives a locator of a link whose text equals to a given `text` inside a `Page`.
  ///
  /// @param text A string to be matched with a text of a link in a given `Page.
  /// @return A function that gives a locator of a link whose text equals to a given `text` inside a `Page`.
  ///
  public static Function<Page, Locator> linkLocatorByExactText(String text) {
    return linkLocatorByText(text, false);
  }
  
  ///
  /// Returns a function that gives a title of the given page.
  ///
  /// @return A function that gives a title of the given page.
  ///
  public static Function<Page, String> toTitle() {
    return Printables.function("title", Page::title);
  }
  
  public static Function<Page, Locator> linkLocatorByText(String text, boolean lenient) {
    return Printables.function("link:@[text" + (lenient ? "~" : "=") + text + "]",
                               p -> p.getByRole(AriaRole.LINK,
                                                new Page.GetByRoleOptions().setName(text)
                                                                           .setExact(!lenient)));
  }
}
