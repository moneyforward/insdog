package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation to specify the fields that can be used by other scenes.
 */
@Retention(RUNTIME)
public @interface Export {
  String[] value() default {};
}
