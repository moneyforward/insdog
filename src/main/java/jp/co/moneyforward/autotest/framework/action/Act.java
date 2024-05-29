package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
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
    return Utils.action(outputConsumerProvider + ":=" + this.name() + "[" + inputProvider + "]",
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
}
