package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to define a dependency of a method to which this is attached.
 * This and `@When` annotations are used mutually exclusively.
 *
 * @see When
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
