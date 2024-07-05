package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to define a dependency of a method to which this is attached.
 */
@Retention(RUNTIME)
public @interface DependsOn {
  String[] value() default {};
  Parameter[] variables() default {};

  @Deprecated
  @interface Parameter {
    String DEFAULT_FIELD_NAME_IN_SOURCE_SCENE="";
    String name();
    String sourceSceneName();
    
    /**
     * By default, the same value as `Parameter#name` will be used.
     *
     * @return The name of the field, from which the parameter value is taken.
     */
    String fieldNameInSourceScene() default DEFAULT_FIELD_NAME_IN_SOURCE_SCENE;
  }
}
