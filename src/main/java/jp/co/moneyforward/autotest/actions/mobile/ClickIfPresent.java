package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.pcond.forms.Printables;
import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;

///
/// An act that models a user behavior, which clicks a specified element only if it is present.
///
public class ClickIfPresent extends ClickBase {
  ///
  /// Creates an object of this class.
  ///
  /// @param by A locator to designate an element to click if present.
  ///
  public ClickIfPresent(By by) {
    this(Printables.function("@" + by, (d) -> by));
  }

  ///
  /// Creates an object of this class.
  ///
  /// @param locatorFunction A function to locate an element to be clicked by this object on `perform` method's call.
  ///
  public ClickIfPresent(Function<AppiumDriver, By> locatorFunction) {
    super(locatorFunction);
  }

  @Override
  public AppiumDriver perform(AppiumDriver driver, ExecutionEnvironment executionEnvironment) {
    List<WebElement> elements = driver.findElements(this.locatorFunction.apply(driver));
    if (!elements.isEmpty()) {
      elements.getFirst().click();
    }
    return driver;
  }
}
