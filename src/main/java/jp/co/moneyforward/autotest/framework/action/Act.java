package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;

/**
 * This interface represents the smallest and indivisible unit of action in ngauto-mf's programming model.
 */
@FunctionalInterface
public interface Act<T, R> extends ActionFactory<T, R> {
  R perform(T value, ExecutionEnvironment executionEnvironment);
  
  default String name() {
    return this.getClass().getSimpleName();
  }
  default Action toAction(Function<Context, Io<T, R>> ioProvider, ExecutionEnvironment executionEnvironment) {
    return leaf(c -> {
      Io<T, R> io = ioProvider.apply(c);
      io.output(perform(io.input(), executionEnvironment));
    });
  }
  
  @FunctionalInterface
  interface ForSupplier<R> extends Act<Void, R> {
    R perform(ExecutionEnvironment executionEnvironment);
    
    @Override
    default R perform(Void value, ExecutionEnvironment executionEnvironment) {
      return perform(executionEnvironment);
    }
  }
}
