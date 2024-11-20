package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(EnsureWiths.class)
@Retention(RUNTIME)
@Target(METHOD)
public @interface EnsureWith {
  String[] value() default {};
}
