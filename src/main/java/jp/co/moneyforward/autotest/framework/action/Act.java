package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.printables.PrintableFunction;
import com.github.valid8j.pcond.forms.Printables;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface represents the smallest and indivisible unit of action in ngauto-mf's programming model.
 */
public interface Act<T, R> extends ActionFactory<T, R> {
  R perform(T value, ExecutionEnvironment executionEnvironment);
  
  @Override
  default Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return actionComposer.create(this, inputFieldName, outputFieldName);
  }
  
  
  default Action toAction(Function<Context, T> inputProvider, Function<Context, Consumer<R>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
    return Utils.action("  " + outputConsumerProvider + ":=" + this.name() + "[" + inputProvider + "]",
                        c -> outputConsumerProvider.apply(c)
                                                   .accept(perform(inputProvider.apply(c),
                                                                   executionEnvironment)));
  }
  
  class Let<T> implements Act<Void, T> {
    private final T value;
    
    public Let(T value) {
      this.value = value;
    }
    
    @Override
    public String name() {
      return String.format("let[%s]", this.value);
    }
    
    @Override
    public T perform(Void value, ExecutionEnvironment executionEnvironment) {
      return this.value;
    }
  }
  
  class Func<T, R> implements Act<T, R> {
    private final Function<T, R> func;
    
    public Func(Function<T, R> func) {
      this.func = func;
    }
    
    public Func(String name, Function<T, R> func) {
      this.func = Printables.function(name, func);
    }
    
    @Override
    public R perform(T value, ExecutionEnvironment executionEnvironment) {
      return this.func.apply(value);
    }
    
    @Override
    public String name() {
      return this.func instanceof PrintableFunction<T, R> ? this.func.toString()
                                                          : "func";
    }
  }
  
  class Sink<T> extends Func<T, Void> {
    
    public Sink(Consumer<T> sink) {
      this("sink[x]", sink);
    }
    
    public Sink(String name, Consumer<T> sink) {
      super(Printables.function(name, (T value) -> {
        sink.accept(value);
        return null;
      }));
    }
  }
}
