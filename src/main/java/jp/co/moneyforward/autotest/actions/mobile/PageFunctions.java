package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.classic.Requires;
import com.github.valid8j.pcond.forms.Printables;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.function.Function;

///
/// A utility class to handle `AppiumDriver` object.
///
/// Methods in this class return a function whose parameter is an `AppiumDriver` object of **Appium**.
///
/// It is common to see a situation, where a single method call to a driver object cannot determine a single element to be returned.
/// In such a case, you can use functions provided by `ElementFunctions` in combination.
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
/// @see ElementFunctions
///
public enum PageFunctions {;

    ///
    /// Returns a function that resolves a locator specified by `name` in a given `AppiumDriver` object.
    ///
    /// This is a shorthand method for `linkLocatorByName(name, false)`.
    ///
    /// @param name A name (accessibility id / content-desc) of a link-like element.
    /// @return A function that resolves a locator specified by `name` in a given `AppiumDriver` object.
    ///
    public static Function<AppiumDriver, WebElement> linkLocatorByName(String name) {
        return linkLocatorByName(name, false);
    }

    ///
    /// Returns a function that resolves a given `name` to a locator of a link-like element whose accessibility
    /// name matches with it.
    ///
    /// @param name    A name to be matched against `@content-desc` (Android) or `@name` (iOS).
    /// @param lenient `true` - partial match / `false` - exact match.
    /// @return A function that resolves a given `name` to a locator of a link-like element whose name matches with it.
    ///
    public static Function<AppiumDriver, WebElement> linkLocatorByName(String name, boolean lenient) {
        String xpath = lenient
            ? "//*[contains(@content-desc,'" + name + "') or contains(@name,'" + name + "')]"
            : "//*[@content-desc='" + name + "' or @name='" + name + "']";
        return Printables.function("link[name" + (lenient ? "~" : "=") + name + "]",
                                   d -> d.findElement(By.xpath(xpath)));
    }

    ///
    /// Returns a function that resolves a locator whose text contains `text` in a given driver.
    ///
    /// @param text A text to be contained by the matching element.
    /// @return A function that resolves a locator whose text contains `text` in a given driver.
    ///
    public static Function<AppiumDriver, WebElement> locatorByText(String text) {
        return locatorByText(text, false);
    }

    ///
    /// Returns a function that resolves a locator which matches `text` in a given `AppiumDriver` object.
    /// If `lenient` is `true`, an element whose text contains it is considered matched.
    /// If `lenient` is `false`, an element whose text equals to `text` is considered matched.
    ///
    /// Matches against `@text` (Android) and `@label` / `@name` (iOS).
    ///
    /// @param text    A text to be matched.
    /// @param lenient `true` - lenient / `false` - strict.
    /// @return A function that resolves a locator which matches `text` in a given `AppiumDriver` object.
    ///
    public static Function<AppiumDriver, WebElement> locatorByText(String text, boolean lenient) {
        String xpath = lenient
            ? "//*[contains(@text,'" + text + "') or contains(@label,'" + text + "') or contains(@name,'" + text + "')]"
            : "//*[@text='" + text + "' or @label='" + text + "' or @name='" + text + "']";
        return Printables.function("@[text" + (lenient ? "~" : "=") + text + "]",
                                   d -> d.findElement(By.xpath(xpath)));
    }

    ///
    /// Returns a function that resolves a locator to a button element in an `AppiumDriver`, whose name is equal to `name`.
    ///
    /// Matches `android.widget.Button[@text]` on Android and `XCUIElementTypeButton[@name]` on iOS.
    ///
    /// @param name A string to be matched with a button element's name.
    /// @return A function that resolves a locator to a button element whose name is equal to `name`.
    ///
    public static Function<AppiumDriver, WebElement> buttonLocatorByName(String name) {
        return Printables.function("@[name=" + name + "]",
                                   d -> d.findElement(By.xpath(
                                       "//android.widget.Button[@text='" + name + "'] | //XCUIElementTypeButton[@name='" + name + "']")));
    }

    ///
    /// Returns a function that resolves a locator whose accessibility label matches with `label` in a given driver.
    ///
    /// @param label A string to be matched with the accessibility label of a locator.
    /// @return A function that resolves a locator whose label matches with `label`.
    ///
    public static Function<AppiumDriver, WebElement> locatorByLabel(String label) {
        return locatorByLabel(label, false);
    }

    ///
    /// Returns a function that resolves a locator whose accessibility label matches with `label` in a given driver.
    ///
    /// Matches against `@content-desc` (Android) and `@label` (iOS).
    ///
    /// If `lenient` is set to `true`, an element whose label contains `label` will be considered matched.
    /// If it is `false`, an element whose label is equal to `label` will be considered matched.
    ///
    /// @param label   A string to be matched with an accessibility label of a locator.
    /// @param lenient `true` - lenient / `false` - strict.
    /// @return A function that resolves a locator whose label matches with `label` in a given driver.
    ///
    public static Function<AppiumDriver, WebElement> locatorByLabel(String label, boolean lenient) {
        String xpath = lenient
            ? "//*[contains(@content-desc,'" + label + "') or contains(@label,'" + label + "')]"
            : "//*[@content-desc='" + label + "' or @label='" + label + "']";
        return Printables.function("@[label" + (lenient ? "~" : "=") + label + "]",
                                   d -> d.findElement(By.xpath(xpath)));
    }

    ///
    /// Returns a function that resolves a locator whose placeholder is `placeholder`.
    ///
    /// Matches against `@hint` (Android) and `@placeholderValue` (iOS).
    ///
    /// @param placeholder A string to be matched with a locator's placeholder.
    /// @return A function that resolves a locator whose placeholder is `placeholder`.
    ///
    public static Function<AppiumDriver, WebElement> locatorByPlaceholder(String placeholder) {
        return Printables.function("@[placeholder=" + placeholder + "]",
                                   d -> d.findElement(By.xpath(
                                       "//*[@hint='" + placeholder + "' or @placeholderValue='" + placeholder + "']")));
    }

    ///
    /// Returns a function that resolves a locator specified by `by` in a given `AppiumDriver`.
    ///
    /// @param by A `By` selector that specifies a locator.
    /// @return A function that resolves a locator specified by `by` in a given `AppiumDriver`.
    ///
    public static Function<AppiumDriver, WebElement> locatorBySelector(By by) {
        Requires.requireNonNull(by);
        return Printables.function("@[" + by + "]", d -> d.findElement(by));
    }

    ///
    /// Returns a function that gives a locator of a link-like element whose text contains a given `text`.
    ///
    /// @param text A string to be contained in the text of a link-like element.
    /// @return A function that gives a locator of a link-like element whose text contains a given `text`.
    ///
    public static Function<AppiumDriver, WebElement> linkLocatorByText(String text) {
        return linkLocatorByText(text, true);
    }

    ///
    /// Returns a function that gives a locator of a link-like element whose text equals to a given `text`.
    ///
    /// @param text A string to be matched exactly with the text of a link-like element.
    /// @return A function that gives a locator of a link-like element whose text equals to a given `text`.
    ///
    public static Function<AppiumDriver, WebElement> linkLocatorByExactText(String text) {
        return linkLocatorByText(text, false);
    }

    ///
    /// Returns a function that gives the title of the current screen / web view.
    ///
    /// @return A function that gives the title of the given driver's current context.
    ///
    public static Function<AppiumDriver, String> toTitle() {
        return Printables.function("title", AppiumDriver::getTitle);
    }

    public static Function<AppiumDriver, WebElement> linkLocatorByText(String text, boolean lenient) {
        String xpath = lenient
            ? "//*[contains(@text,'" + text + "') or contains(@label,'" + text + "') or contains(@name,'" + text + "')]"
            : "//*[@text='" + text + "' or @label='" + text + "' or @name='" + text + "']";
        return Printables.function("link:@[text" + (lenient ? "~" : "=") + text + "]",
                                   d -> d.findElement(By.xpath(xpath)));
    }
}