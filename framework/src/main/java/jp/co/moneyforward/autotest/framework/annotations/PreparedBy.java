package jp.co.moneyforward.autotest.framework.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(PreparedBys.class)
@Target(METHOD)
@Retention(RUNTIME)
public @interface PreparedBy {
  String[] value() default {};
}
