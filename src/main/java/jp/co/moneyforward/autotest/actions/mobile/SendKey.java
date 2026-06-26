package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.pcond.forms.Printables;
import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.openqa.selenium.By;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class SendKey implements Act<AppiumDriver, AppiumDriver> {
  ///
  /// A prefix to control a
  ///
  public static final String MASK_PREFIX = "MASK!";
  private final Supplier<String> keySequenceGenerator;
  private final Function<AppiumDriver, By> locatorFunction;
  
  ///
  /// Creates an instance of this class.
  ///
  /// @param by A By function used to locate elements.
  /// @param keys     Keys to be sent to a locator chosen by  a `by` function.
  /// @see SendKey#SendKey(Function, String)
  ///
  public SendKey(By by, String keys) {
    this(Printables.function("@" + by, (d) -> by), keys);
  }
  
  ///
  /// Creates an instance of this class.
  ///
  /// @param locatorFunction A function to choose a locator from a given driver.
  /// @param keys            Keys to be sent to a chosen locator.
  ///
  public SendKey(Function<AppiumDriver, By> locatorFunction, String keys) {
    this(locatorFunction, toSupplier(requireNonNull(keys)));
  }
  
  ///
  /// Creates an instance of this class.
  ///
  /// @param locatorFunction      A function to choose a locator from a given driver.
  /// @param keySequenceGenerator A supplier that generates a key sequence to be sent to a chosen locator.
  ///
  public SendKey(Function<AppiumDriver, By> locatorFunction, Supplier<String> keySequenceGenerator) {
    this.locatorFunction = requireNonNull(locatorFunction);
    this.keySequenceGenerator = requireNonNull(keySequenceGenerator);
  }
  
  @Override
  public AppiumDriver perform(AppiumDriver value, ExecutionEnvironment executionEnvironment) {
    By by = this.locatorFunction.apply(value);
    String keys = keySequenceGenerator.get();
    value.findElement(by).sendKeys(keys.startsWith(MASK_PREFIX) ? keys.substring(MASK_PREFIX.length()) : keys);

    return value;
  }
  
  private static Supplier<String> toSupplier(String keys) {
    return () -> keys;
  }
}
