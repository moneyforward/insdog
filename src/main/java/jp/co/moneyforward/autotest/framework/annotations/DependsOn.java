package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to define a dependency of a method to which this is attached.
 */
@Retention(RUNTIME)
public @interface DependsOn {
  Parameter[] value() default {};
  @interface Parameter {
    String name();
    String sourceSceneName();
    String fieldNameInSourceScene();
  }
}
