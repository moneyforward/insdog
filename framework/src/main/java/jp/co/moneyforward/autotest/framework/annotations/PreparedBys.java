package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A container annotation for `@PreparedBy`.
 * Not to be used by programmers directly.
 *
 * @see PreparedBy
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface PreparedBys {
  /**
   * Child elements.
   *
   * @return Child annotations.
   */
  PreparedBy[] value();
}
