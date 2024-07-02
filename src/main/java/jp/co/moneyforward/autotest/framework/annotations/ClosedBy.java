package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface ClosedBy {
  String value() default "";
}
