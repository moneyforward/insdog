package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.action.EnsuredCall;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 
/// An annotation to declare a procedure to ensure the state defined by the target method is satisfied.
/// 
/// @see EnsuredCall
/// 
@Repeatable(PreparedBys.class)
@Target(METHOD)
@Retention(RUNTIME)
public @interface PreparedBy {
  /// 
  /// Returns a sequence of scene names, by which the target state is satisfied.
  /// They are executed sequentially in an order this method returns, then target scene call will be performed
  /// in order to check if the desired state is really satisfied.
  /// 
  /// @return A sequence of scene names.
  /// 
  String[] value() default {};
}
