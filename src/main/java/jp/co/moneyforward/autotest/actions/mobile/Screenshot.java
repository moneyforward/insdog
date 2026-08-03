package jp.co.moneyforward.autotest.actions.mobile;

import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

///
/// An act that does screenshot.
/// The app screenshot is saved under `ExecutionEnvironment#testOutputFilenameFor("screenshot-{stepName}.png")`, where
/// `{stepName}` is one of `beforeAll`, `beforeEach`, `afterEach`, or `afterAll`.
///
/// @see ExecutionEnvironment#testOutputFilenameFor(String)
///
public class Screenshot implements Act<AppiumDriver, AppiumDriver> {
  ///
  /// Creates an instance of this class.
  ///
  public Screenshot() {
    // Make default constructor findable.
  }
  
  ///
  /// Performs the screenshot action.
  ///
  /// @param value A driver for which screenshot is executed.
  /// @param executionEnvironment An execution environment.
  /// @return The driver itself given as `value` parameter.
  ///
  @Override
  public AppiumDriver perform(AppiumDriver value, ExecutionEnvironment executionEnvironment) {
    try {
      Path destination = executionEnvironment.testOutputFilenameFor(String.format("screenshot-%s.png", executionEnvironment.stepName()));
      Files.createDirectories(destination.getParent());
      Files.write(destination, value.getScreenshotAs(OutputType.BYTES));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return value;
  }
}