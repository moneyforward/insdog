package jp.co.moneyforward.autotest.ut.misc;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface DependsOn {
  String[] value() default {};
}
