package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Optional;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;
import static com.github.dakusui.actionunit.core.ActionSupport.sequential;

public interface ActionFactory<T, R> {
  class Io<T, R> {
    private final T input;
    private R output;
    private boolean outputInitialized;
    
    public Io(T input) {
      this.input = input;
      this.outputInitialized = false;
    }
    
    public T input() {
      return this.input;
    }
    
    public Optional<R> output() {
      return outputInitialized ?
          Optional.of(this.output) :
          Optional.empty();
    }
    
    public Io<T, R> output(R output) {
      this.output = output;
      this.outputInitialized = true;
      return this;
    }
  }
  
  Action toAction(Function<Context, Io<T, R>> ioProvider, ExecutionEnvironment executionEnvironment);
  
  String name();
  
  default ActionFactory<T, R> assertion(String name, Function<R, Statement<R>> assertion) {
    return new ActionFactory<>() {
      @Override
      public Action toAction(Function<Context, Io<T,R>> ioProvider, ExecutionEnvironment executionEnvironment) {
        return sequential(
            ActionFactory.this.toAction(ioProvider, executionEnvironment),
            leaf(context -> {
              Io<T, R> io = ioProvider.apply(context);
              Expectations.assertStatement(assertion.apply(io.output().orElseThrow()));
            }));
      }
      
      
      @Override
      public String name() {
        return name;
      }
    };
  }
}
