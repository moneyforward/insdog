package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@TestTemplate
@ExtendWith(AutotestEngine.class)
public @interface ActionTest {
}
