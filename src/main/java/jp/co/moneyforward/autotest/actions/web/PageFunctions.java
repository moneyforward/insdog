package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A utility class that holds method returning functions which accept a page.
 */
public enum PageFunctions {
  ;
  
  public static Function<Page, Locator> getByName(String name) {
    return getByName(name, false);
  }
  
  public static Function<Page, Locator> getByName(String name, boolean lenient) {
    return Printables.function("link[name" + (lenient ? "~" : "=") + name + "]",
                               p -> p.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setExact(!lenient)
                                                                                          .setName(name)));
  }
  
  public static Function<Page, Locator> getButtonByName(String name) {
    return Printables.function("@[name=" + name + "]",
                               p -> p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)
                                                                                            .setExact(true)));
  }
  
  public static Function<Page, Locator> getByText(String text) {
    return getByText(text, false);
  }
  
  public static Function<Page, Locator> getByText(String text, boolean lenient) {
    return Printables.function("@[text" + (lenient ? "~" : "=") + text + "]",
                               p -> p.getByText(text, new Page.GetByTextOptions().setExact(!lenient)));
  }
  
  public static Function<Page, Locator> getByLabel(String label) {
    return getByLabel(label, false);
  }
  
  public static Function<Page, Locator> getByLabel(String label, boolean lenient) {
    return Printables.function("@[label" + (lenient ? "~" : "=") + label + "]",
                               p -> p.getByLabel(label, new Page.GetByLabelOptions().setExact(!lenient)));
  }
  
  public static Function<Page, Locator> getByPlaceholder(String placeholder) {
    return Printables.function("@[placeholder=" + placeholder + "]",
                               p -> p.getByPlaceholder(placeholder, new Page.GetByPlaceholderOptions().setExact(true)));
  }
  
  public static Function<Page, Locator> getBySelector(String selector) {
    requireNonNull(selector);
    return Printables.function("@[" + selector + "]", p -> p.locator(selector));
  }
  
  public static Function<Page, String> getTitle() {
    return Printables.function("title", Page::title);
  }
  
  public static Function<Page, Locator> getCellByName(String name) {
    return Printables.function("cell[name=" + name + "]", (Page p) -> p.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(name).setExact(true)));
  }
  
  public static Locator getLink(Page page) {
    return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("事業者・年度の管理"));
  }
}
