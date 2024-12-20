package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.action.Scene;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 
/// // @formatter:off
/// An annotation to specify the fields that can be used by other scenes.
/// 
/// ```java
/// @Named
/// @Export
/// public Scene aMethod() {
///   return Scene.perform()
///               .acts()
///               .object();
/// }
/// 
/// @Named
/// @DependsOn("aMethod")
/// public Scene bMethod() {
///   return ...;
/// }
/// ```
/// 
/// The method with this annotation can be referenced by other method (`bMethod`) using `@DependsOn` annotation.
/// The scene in the referencing method can access fields written by a scene in referenced method (`aMethod`).
/// // @formatter:on
/// 
@Retention(RUNTIME)
public @interface Export {
  /// 
  /// Variable names to be exported from the scene returned by the method this annotation is attached to.
  /// 
  /// @return Exported variable names.
  /// 
  String[] value() default {Scene.DEFAULT_DEFAULT_VARIABLE_NAME};
}
