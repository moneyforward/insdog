package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

/**
 * An act that models a call to a form, such as a function or an assertion.
 *
 * @param <T> Type input parameter
 */
public class ActCall<T, R> implements Call {
  private final String inputFieldName;
  private final String outputFieldName;
  
  private final Act<T, R> act;
  
  /**
   * Creates an instance of this class.
   *
   * @param act An act to be called.
   * @param outputFieldName A name of a field for output.
   * @param inputFieldName A name of a field for input.
   *
   * @see Act
   */
  public ActCall(String outputFieldName, Act<T, R> act, String inputFieldName) {
    this.inputFieldName = requireNonNull(inputFieldName);
    this.outputFieldName = requireNonNull(outputFieldName);
    this.act = act;
  }
  
  public Act<T, R> act() {
    return this.act;
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this);
  }
  
  /**
   * Returns an input field name of this call.
   *
   * @return An input field name of this call.
   */
  public String inputFieldName() {
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
