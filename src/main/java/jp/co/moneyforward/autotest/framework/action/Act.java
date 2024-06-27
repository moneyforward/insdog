package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;

public interface Act<T, R> extends ActionFactory {
  AssertionAct<T, R> assertion(Function<R, Statement<R>> assertion);
}
