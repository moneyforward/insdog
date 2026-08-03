package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.pcond.forms.Printables;
import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.openqa.selenium.By;

import java.util.function.Function;

///
/// An act that models a user behavior, which clicks a specified element.
///
public class Click extends ClickBase {
  ///
  /// Creates an object of this class.
  ///
  /// @param by A locator to designate an element to click.
  ///
  public Click(By by) {
    this(Printables.function("@" + by, (d) -> by));
  }

  ///
  /// Creates an object of this class.
  ///
  /// @param locatorFunction A locator for an element to click.
  ///
  public Click(Function<AppiumDriver, By> locatorFunction) {
    super(locatorFunction);
  }

  @Override
  public AppiumDriver perform(AppiumDriver driver, ExecutionEnvironment executionEnvironment) {
    driver.findElement(this.locatorFunction.apply(driver)).click();
    return driver;
  }
}