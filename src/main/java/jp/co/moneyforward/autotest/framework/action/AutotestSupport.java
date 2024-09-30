package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.List;
import java.util.function.Function;

/**
 * A facade class of the "autotest" framework.
 */
public enum AutotestSupport {
  ;
  
  /**
   * Returns a `Call` object for a given `scene`.
   *
   * @param outputVariableStoreName A name of variable store, where the children of `scene` write there output variables.
   * @param scene                   A scene for which a call is created
   * @param resolverBundle          A resolver bundle which gives values of variables during the execution of the returned scene call.
   * @return A `SceneCall` object for `scene`.
   */
  public static SceneCall sceneCall(String outputVariableStoreName, Scene scene, ResolverBundle resolverBundle) {
    return new SceneCall(outputVariableStoreName, scene, resolverBundle);
  }
  
  /**
   * Returns an `Call` object for a given `act`.
   *
   * @param outputVariableName A name of an output variable, to which `act` writes its output to.
   * @param act                An `act`, for which a call is created.
   * @param inputVariableName  A name of variable from which an `act` takes its input.
   * @param <T>                Input type of `act`.
   * @param <R>                Output type of `act`.
   * @return An `ActCall` object.
   */
  public static <T, R> ActCall<T, R> actCall(String outputVariableName, Act<T, R> act, String inputVariableName) {
    return new ActCall<>(outputVariableName, act, inputVariableName);
  }
  
  /**
   * Creates an `AssertionCall` object which executes `act`, then validates it using `assertions`.
   * An output from act will be stored in a context with `outputVariableName`.
   *
   * @param outputVariableName A name of a variable, where an output from `act` is stored.
   * @param act                An `act` whose output should be validated.
   * @param assertions         A list of functions each of which creates a `Statement` that validates an output from `act`.
   * @param inputVariableName  A name of a variable, whose value is given to `act`.
   * @param <T>                Type of the input to `act`.
   * @param <R>                Type of the output from `act`.
   * @return An `AssertionCall` object.
   * @see Act
   */
  public static <T, R> AssertionCall<R> assertionCall(String outputVariableName, Act<T, R> act, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    return new AssertionCall<>(actCall(outputVariableName, act, inputVariableName), assertions);
  }
  
  /**
   * Returns a call that retries a given `call`.
   *
   * @param call        A call to be retried on a failure.
   * @param onException An exception class on which `call` should be retried.
   * @param times       Number of retried to be attempted.
   * @param interval    Interval between tries.
   * @return A call that retries a given `call`.
   */
  public static RetryCall retryCall(Call call, Class<? extends Throwable> onException, int times, int interval) {
    return new RetryCall(call, onException, times, interval);
  }
}
