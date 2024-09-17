package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

/**
 * An interface that models an occurrence of an action in a test scenario.
 *
 * As a design policy, a call is defined for structural actions of **actionunit**, such as `retry`, `sequential`, `parallel`, etc.
 * Functionalities exercised in tests are represented as implementations of `LeafAct`.
 *
 * @see LeafAct
 */
public interface Call {
  /**
   * A method to return output field name.
   *
   * @return An output field name of this object.
   */
  String outputFieldName();
  
  /**
   * Converts this call to action to an action object.
   *
   * This is an `Node#accept` method in the **Visitor** pattern.
   * Usually implementations of this method should call back by `actionComposer#create(this)` to make a double dispatch happen.
   *
   * @param actionComposer A visitor, which creates an action from this object.
   * @param assignmentResolversFromCurrentCall Resolvers to assign values to context variables referenced by a created action.
   * @return An action created by `actionComposer`.
   */
  Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall);
}
