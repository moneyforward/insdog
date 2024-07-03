package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to let the framework know an access-model method should be closed by an action specified by the method name.
 *
 */
@Retention(RUNTIME)
public @interface ClosedBy {
  /**
   * A name of a method that returns an action, which closes an action returned by the annotated method.
   *
   * @return A name of a closer method.
   * @see Named
   */
  String value();
}
