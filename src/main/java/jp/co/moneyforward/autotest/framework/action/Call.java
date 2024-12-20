package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

/// 
/// An interface that models an occurrence of an action in a test scenario.
/// 
/// As a design policy, a call is defined for structural actions of **actionunit**, such as `retry`, `sequential`, `parallel`, etc.
/// Functionalities exercised in tests are represented as implementations of `LeafAct`.
/// 
/// An instance of a `Call` must be added only once to a `Scene.Builder` at most.
/// Otherwise,
/// 
/// @see Act
/// @see Scene
/// 
public sealed interface Call permits ActCall, CallDecorator, SceneCall {
  /// 
  /// Converts this call to action to an action object.
  /// 
  /// This is an `Node#accept` method in the **Visitor** pattern.
  /// Usually implementations of this method should call back by `actionComposer#create(this)` to make a double dispatch happen.
  /// 
  /// Each value in the `ongoingResolverBundle` is a function that resolves a value of a variable designated by a corresponding key.
  /// 
  /// @param actionComposer A visitor, which creates an action from this object.
  /// @return An action created by `actionComposer`.
  /// 
  Action toAction(ActionComposer actionComposer);
}
