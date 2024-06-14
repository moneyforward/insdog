package jp.co.moneyforward.autotest.actions.web;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Locator;

import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public enum LocatorFunctions {
  ;
  public static Function<Locator, Locator> nth(int i) {
    return Printables.function("nth[" + i + "]", l -> l.nth(i));
  }
  
  public static Function<Locator, Locator> byText(String text) {
    requireNonNull(text);
    return Printables.function("byText[" + text + "]", l -> l.getByText(text));
  }
}
