package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.pcond.forms.Printables;
import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

///
/// An act that scrolls the screen in a given direction until a target element becomes visible.
///
/// Performs a swipe gesture repeatedly, up to `maxScrollAttempts` times.
/// After each swipe the element's presence and visibility are checked.
/// If the element is still not visible after all attempts, a `NoSuchElementException` is thrown.
///
public class ScrollToElement implements Act<AppiumDriver, AppiumDriver> {

  ///
  /// The direction in which to scroll.
  ///
  public enum Direction { UP, DOWN, LEFT, RIGHT }

  private static final int DEFAULT_MAX_SCROLL_ATTEMPTS = 10;

  private final Function<AppiumDriver, By> locatorFunction;
  private final Direction direction;
  private final int maxScrollAttempts;

  ///
  /// Creates an object of this class that scrolls downward until the element located by `by` becomes visible.
  ///
  /// @param by A locator for the target element.
  ///
  public ScrollToElement(By by) {
    this(Printables.function("@" + by, d -> by));
  }

  ///
  /// Creates an object of this class that scrolls downward until the element resolved by `locatorFunction` becomes visible.
  ///
  /// @param locatorFunction A function to locate the target element.
  ///
  public ScrollToElement(Function<AppiumDriver, By> locatorFunction) {
    this(locatorFunction, Direction.DOWN, DEFAULT_MAX_SCROLL_ATTEMPTS);
  }

  ///
  /// Creates an object of this class.
  ///
  /// @param by                A locator for the target element.
  /// @param direction         The direction in which to scroll.
  /// @param maxScrollAttempts The maximum number of scroll attempts before failing.
  ///
  public ScrollToElement(By by, Direction direction, int maxScrollAttempts) {
    this(Printables.function("@" + by, d -> by), direction, maxScrollAttempts);
  }

  ///
  /// Creates an object of this class.
  ///
  /// @param locatorFunction   A function to locate the target element.
  /// @param direction         The direction in which to scroll.
  /// @param maxScrollAttempts The maximum number of scroll attempts before failing.
  ///
  public ScrollToElement(Function<AppiumDriver, By> locatorFunction, Direction direction, int maxScrollAttempts) {
    this.locatorFunction = requireNonNull(locatorFunction);
    this.direction = requireNonNull(direction);
    this.maxScrollAttempts = maxScrollAttempts;
  }

  @Override
  public AppiumDriver perform(AppiumDriver driver, ExecutionEnvironment executionEnvironment) {
    By by = this.locatorFunction.apply(driver);
    for (int attempt = 0; attempt < maxScrollAttempts; attempt++) {
      List<WebElement> elements = driver.findElements(by);
      if (!elements.isEmpty() && elements.getFirst().isDisplayed()) {
        return driver;
      }
      swipe(driver);
    }
    driver.findElement(by);
    return driver;
  }

  @Override
  public String name() {
    return InternalUtils.simpleClassNameOf(this.getClass()) + "[" + this.locatorFunction + "][" + this.direction + "]";
  }

  private void swipe(AppiumDriver driver) {
    Dimension size = driver.manage().window().getSize();
    int centerX = size.getWidth() / 2;
    int centerY = size.getHeight() / 2;
    int startX, startY, endX, endY;
    switch (this.direction) {
      case DOWN  -> { startX = centerX; startY = (int)(size.getHeight() * 0.7); endX = centerX; endY = (int)(size.getHeight() * 0.3); }
      case UP    -> { startX = centerX; startY = (int)(size.getHeight() * 0.3); endX = centerX; endY = (int)(size.getHeight() * 0.7); }
      case RIGHT -> { startX = (int)(size.getWidth() * 0.7); startY = centerY; endX = (int)(size.getWidth() * 0.3); endY = centerY; }
      case LEFT  -> { startX = (int)(size.getWidth() * 0.3); startY = centerY; endX = (int)(size.getWidth() * 0.7); endY = centerY; }
      default -> throw new IllegalStateException("Unexpected direction: " + this.direction);
    }
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 0);
    swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    driver.perform(Collections.singletonList(swipe));
  }
}