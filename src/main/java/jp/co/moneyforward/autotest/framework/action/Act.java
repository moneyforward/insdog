package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;

/**
 * This interface represents the smallest and indivisible unit of action in ngauto-mf's programming model.
 */
public interface Act<T, R> extends ActionFactory<T, R> {
  R perform(T value, ExecutionEnvironment executionEnvironment);
  
  
  @Override
  default Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return actionComposer.create(this, inputFieldName, outputFieldName);
  }

  default Optional<String> name() {
    return this.getClass().isAnonymousClass() ? Optional.empty()
                                              : Optional.of(this.getClass().getSimpleName());
  }
  
  default Action toAction(Function<Context, T> inputProvider, Function<Context, Consumer<R>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
    return leaf(c -> outputConsumerProvider.apply(c)
                                           .accept(perform(inputProvider.apply(c),
                                                           executionEnvironment)));
  }
  
  class Let<T> implements Act<Void, T> {
    private final T value;
    
    public Let(T value) {
      this.value = value;
    }
    
    public T perform(Void value, ExecutionEnvironment executionEnvironment) {
      return this.value;
    }
  }
}
