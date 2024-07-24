package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * An act that models a call to a form, such as a function or an assertion.
 *
 * @param <T> Type input parameter
 */
public abstract class ActCall<T> implements Call {
  private final String inputFieldName;
  private final String outputFieldName;
  
  /**
   * Creates an instance of this class.
   *
   * @param inputFieldName A name of a field for input.
   * @param outputFieldName A name of a field for output.
   */
  protected ActCall(String inputFieldName, String outputFieldName) {
    this.inputFieldName = requireNonNull(inputFieldName);
    this.outputFieldName = requireNonNull(outputFieldName);
  }
  
  /**
   * Returns an input field name of this call.
   *
   * @return An input field name of this call.
   */
  String inputFieldName() {
    return this.inputFieldName;
  }
  
  /**
   * Returns an input field's value of the `sceneCall`.
   *
   * @param sceneCall A `sceneCall` whose input field value is returned.
   * @param context   A context, in which the `sceneCall` 's input field value is resolved.
   * @return A value of an input field name of a `sceneCall`.
   */
  @SuppressWarnings("unchecked")
  T inputFieldValue(SceneCall sceneCall, Context context) {
    return (T) sceneCall.workArea(context).get(inputFieldName());
  }
  
  /**
   * Returns a name of a field for output.
   *
   * @return A name of a field for output.
   */
  @Override
  public String outputFieldName() {
    return this.outputFieldName;
  }
}
