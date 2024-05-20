package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.*;

@FunctionalInterface
public interface ActionFactory<T, R> {
  R perform(T input, ExecutionEnvironment executionEnvironment);
  
  default Optional<String> name() {
    return Optional.empty();
  }
  
  default Action toAction(String outputFieldName, ExecutionEnvironment executionEnvironment, String inputFieldName) {
    return toAction(inputProvider(inputFieldName), outputConsumerProvider(outputFieldName), executionEnvironment);
  }
  
  default Action toAction(Function<Context, T> inputProvider, Function<Context, Consumer<R>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
    Action action = createAction(inputProvider, outputConsumerProvider, executionEnvironment);
    return name().map(n -> named(n, action)).orElse(action);
  }
  
  private Action createAction(Function<Context, T> inputProvider, Function<Context, Consumer<R>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
    return leaf(c -> outputConsumerProvider.apply(c)
                                           .accept(perform(inputProvider.apply(c), executionEnvironment)));
  }
  
  default Function<Context, T> inputProvider(String inputFieldName) {
    return c -> c.valueOf(inputFieldName);
  }
  
  default Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName) {
    return c -> c.valueOf(outputFieldName);
  }
  
  default ActionFactory<T, R> assertion(Function<R, Statement<R>> assertion) {
    return new ActionFactory<>() {
      @Override
      public Action toAction(Function<Context, T> inputProvider, Function<Context, Consumer<R>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
        AtomicReference<R> output = new AtomicReference<>();
        return sequential(
            ActionFactory.this.toAction(inputProvider,
                                        context -> outputConsumerProvider.apply(context).andThen(output::set),
                                        executionEnvironment),
            leaf(context -> Expectations.assertStatement(assertion.apply(output.get()))));
      }
      
      @Override
      public R perform(T input, ExecutionEnvironment executionEnvironment) {
        return null;
      }
      
      @Override
      public Optional<String> name() {
        return ActionFactory.this.name();
      }
    };
  }
}
