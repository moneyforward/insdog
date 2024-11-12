package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to define a dependency of a method to which this is attached.
 *
 * This annotation is used only when the `PlanningStrategy#DEPENDENCY_BASED` is activated and usually attached to methods for test results checking.
 * This and `@When` annotations are used mutually exclusively.
 *
 * @see When
 * @see PlanningStrategy#DEPENDENCY_BASED
 */
@Retention(RUNTIME)
public @interface DependsOn {
  /**
   * Returns names of scenes on which attached scene method is depending.
   *
   * @return Names of scenes on which attached scene method is depending.
   *
   * @see Named
   */
  String[] value() default {};
}
