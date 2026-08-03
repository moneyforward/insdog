package jp.co.moneyforward.autotest.actions.mobile;

import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.BiConsumer;

import static com.github.valid8j.classic.Requires.requireNonNull;

///
/// A general-purpose act.
/// Convenient starting point for writing **insdog** based tests.
///
public abstract class MobileAct implements Act<AppiumDriver, AppiumDriver> {
  private final String description;
  
  ///
  /// Creates a new instance of this class.
  ///
  /// It is advised to give a concise and descriptive string to `description` parameter as it is printed the test report.
  /// The `description` should be concise but informative enough for a reader to reproduce the same action that this `Act` performs.
  ///
  /// @param description A string that describes this object.
  ///
  protected MobileAct(String description) {
    this.description = requireNonNull(description);
  }
  
  ///
  /// Creates a `MobileAct with a given description and an action
  ///
  /// @param description A string to describe created page act.
  /// @param action      An action to be performed.
  /// @return A page act that performs `action`.
  ///
  public static MobileAct mobileAct(String description,
                                    BiConsumer<AppiumDriver, ExecutionEnvironment> action) {
    return new MobileAct(description) {
      @Override
      protected void action(AppiumDriver driver, ExecutionEnvironment env) {
        action.accept(driver, env);
      }
    };
  }
  
  ///
  /// Performs an action defined for this class.
  /// Its execution is delegated to `perform(AppiumDriver,ExecutionEnvironment)` method.
  ///
  /// @param value                A page object on which this `act` is performed.
  /// @param executionEnvironment An execution environment, in which this act is performed.
  /// @return The `value` itself should be returned, usually.
  ///
  @Override
  public AppiumDriver perform(AppiumDriver value, ExecutionEnvironment executionEnvironment) {
    this.action(value, executionEnvironment);
    return value;
  }
  
  ///
  /// A method that defines the `act` to be performed by this object.
  ///
  /// @param driver               A driver object on which this `act` is performed.
  /// @param executionEnvironment An execution environment, in which this act is performed.
  ///
  protected abstract void action(AppiumDriver driver, ExecutionEnvironment executionEnvironment);
  
  ///
  /// Returns a name of this object.
  ///
  /// @return A name of this object.
  ///
  @Override
  public String name() {
    return "Driver[" + this.description + "]";
  }
}
