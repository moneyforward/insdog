package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An interface that models an occurrence of an action in a test scenario.
 *
 * As a design policy, a call is defined for structural actions of **actionunit**, such as `retry`, `sequential`, `parallel`, etc.
 * Functionalities exercised in tests are represented as implementations of `LeafAct`.
 *
 * An instance of a `Call` must be added only once to a `Scene.Builder` at most.
 * Otherwise,
 *
 * @see Act
 */
public sealed interface Call permits ActCall, TargetedCall, SceneCall {
  /**
   * A method to return an output variable name.
   *
   * Note that semantics of a value returned by this method are slightly different when it is called on `SceneCall` and other `Call` objects.
   *
   * When this is called on `SceneCall`, it returns a key in a `Context` object, whose value is a `Map`, which is a "variable store".
   * Otherwise, it returns a key in a map returned by `SceneCall#outputVariableName`.
   *
   * @return An output variable name of this object.
   */
  //String outputVariableName();
  
  /**
   * A method to return a list of input variable names.
   *
   * If this object is a `SceneCall`, this returns a union of variables from which its child calls read values.
   *
   * @return a list of input variable names
   */
  List<String> requiredVariableNames();
  
  /**
   * Converts this call to action to an action object.
   *
   * This is an `Node#accept` method in the **Visitor** pattern.
   * Usually implementations of this method should call back by `actionComposer#create(this)` to make a double dispatch happen.
   *
   * Each value in the `assignmentResolversFromCurrentCall` is a function that resolves a value of a variable designated by a corresponding key.
   *
   * @param actionComposer A visitor, which creates an action from this object.
   * @param assignmentResolversFromCurrentCall Resolvers to assign values to context variables referenced by a created action.
   * @return An action created by `actionComposer`.
   */
  Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall);
}
