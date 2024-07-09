package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;

import static com.github.valid8j.classic.Requires.requireNonNull;

public abstract class ActCall<T, R> implements Call {
  private final String inputFieldName;
  private final String outputFieldName;
  
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
  
  @Override
  public String outputFieldName() {
    return this.outputFieldName;
  }
}
