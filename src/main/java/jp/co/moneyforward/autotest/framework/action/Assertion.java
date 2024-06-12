package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface Assertion<T, R> extends Act<T, R> {
  ActionFactory<T, R> target();
  
  Statement<R> assertion();
  
  class ForAct<T, R> implements Assertion<T, R> {
    private final Act<T, R> target;
    private final Function<R, Statement<R>> assertion;
    
    public ForAct(Act<T, R> target, Function<R, Statement<R>> assertion) {
      this.target = requireNonNull(target);
      this.assertion = requireNonNull(assertion);
    }
    
    @Override
    public Act<T, R> target() {
      return target;
    }
    
    @Override
    public Statement<R> assertion() {
      return this.assertion.apply(null);
    }
  }
}
