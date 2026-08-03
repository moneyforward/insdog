package jp.co.moneyforward.autotest.actions.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;

///
/// An act that models a user behavior of pressing the device's back button.
///
/// - **Android**: presses the hardware back key (`AndroidKey.BACK`, keycode 4) via `AndroidDriver.pressKey`.
/// - **iOS**: performs a left-edge swipe to trigger the interactive pop gesture (equivalent of the swipe-back navigation).
///
public class Back implements Act<AppiumDriver, AppiumDriver> {

  private final int count;

  ///
  /// Creates an object of this class that presses the back button once.
  ///
  public Back() {
    this(1);
  }

  ///
  /// Creates an object of this class.
  ///
  /// @param count The number of times to press the back button.
  ///
  public Back(int count) {
    this.count = count;
  }

  @Override
  public AppiumDriver perform(AppiumDriver driver, ExecutionEnvironment executionEnvironment) {
    for (int i = 0; i < count; i++) {
      if (driver instanceof AndroidDriver androidDriver) {
        androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
      } else if (driver instanceof IOSDriver) {
        edgeSwipeBack(driver);
      } else {
        driver.navigate().back();
      }
    }
    return driver;
  }

  ///
  /// Returns a name of this object.
  ///
  /// @return A name of this object.
  ///
  @Override
  public String name() {
    return InternalUtils.simpleClassNameOf(this.getClass()) + "[x" + count + "]";
  }

  private static void edgeSwipeBack(AppiumDriver driver) {
    Dimension size = driver.manage().window().getSize();
    int endX = (int)(size.getWidth() * 0.8);
    int centerY = size.getHeight() / 2;
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 0);
    swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), 0, centerY));
    swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
    swipe.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), endX, centerY));
    swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
    driver.perform(Collections.singletonList(swipe));
  }
}
