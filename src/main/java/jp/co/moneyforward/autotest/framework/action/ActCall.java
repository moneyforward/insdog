package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

/**
 * An act that models a call to a form, such as a function or an assertion.
 *
 * @param <T> Type input parameter
 */
public record ActCall<T, R>(String outputVariableName, Act<T, R> act, String inputVariableName) implements Call {
  /**
   * Creates an instance of this class.
   *
   * @param act                An act to be called.
   * @param outputVariableName A name of a field for output.
   * @param inputVariableName  A name of a field for input.
   * @see Act
   */
  public ActCall(String outputVariableName, Act<T, R> act, String inputVariableName) {
    this.inputVariableName = requireNonNull(inputVariableName);
    this.outputVariableName = requireNonNull(outputVariableName);
    this.act = requireNonNull(act);
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> resolverBundle) {
    return actionComposer.create(this);
  }
  
  /**
   * Returns an input variable name of this call.
   * A variable needs to be defined in a scope where `act` is performed.
   *
   * @return An input variable name of this call.
   */
  @Override
  public String inputVariableName() {
    return this.inputVariableName;
  }
  
  /**
   * Returns an input variable's value for this `ActCall` object.
   *
   * This method is a shorthand of ```sceneCall.workingVariableStore(context).get(inputVariableName())```.
   *
   * @param sceneCall An ongoing `sceneCall` to which this `ActCall` object belongs.
   * @param context   A context, in which the `sceneCall` 's input field value is resolved.
   * @return A value of an input field name of a `sceneCall`.
   * @see ActCall#resolveVariable(SceneCall, Context)
   */
  @SuppressWarnings("unchecked")
  T resolveVariable(SceneCall sceneCall, Context context) {
    return (T) sceneCall.workingVariableStore(context).get(inputVariableName());
  }
  
  /**
   * Returns a name of a field for output.
   *
   * @return A name of a field for output.
   */
  @Override
  public String outputVariableName() {
    return this.outputVariableName;
  }
  
  @Override
  public List<String> requiredVariableNames() {
    return List.of(inputVariableName());
  }
}
