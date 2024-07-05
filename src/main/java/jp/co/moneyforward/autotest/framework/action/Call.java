package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

/**
 * An interface that models an occurrence of an action in a test scenario.
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
   * @return An action created by `actionComposer`.
   */
  Action toAction(ActionComposer actionComposer);
}
