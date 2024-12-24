package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/// 
/// An annotation that specifies a name to specify a method in a test class.
/// 
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {
  /// 
  /// A default value.
  /// 
  /// The name of the annotated method is recognized using the method name itself.
  /// The signature will not be taken into account.
  /// To avoid ambiguity, specify the name explicitly.
  /// 
  String DEFAULT_VALUE = "";
  
  /// 
  /// Should return a name to specify a method this annotation is attached to.
  /// If the value is a string without length, the name of the method (`Method#getName()`) itself will be used for it.
  /// 
  /// @return A name of a method this annotation is attached to.
  /// 
  String value() default DEFAULT_VALUE;
}
