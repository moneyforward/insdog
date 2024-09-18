package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.forms.Printables;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This interface represents the smallest and indivisible unit of action in **autotest-ca** 's programming model.
 */
public interface Act<T, R> {
  R perform(T value, ExecutionEnvironment executionEnvironment);
  
  /**
   * Returns a name of an instance of this interface.
   *
   * @return A name of this instance.
   */
  default String name() {
    return InternalUtils.simpleClassNameOf(this.getClass());
  }

  /**
   * A leaf act, which represents a value assignment behavior.
   *
   * @param <T> The type of the value to be assigned.
   */
  class Let<T> extends Source<T> implements Act<Object, T> {
    private final T value;
    
    /**
     * Creates an instance of this class.
     *
     * @param value The value to be assigned to the target variable.
     */
    public Let(T value) {
      this.value = value;
    }
    
    /**
     * Returns a value to be assigned to the target variable.
     *
     * @return A value to be assigned to the target variable.
     */
    @Override
    protected T value() {
      return this.value;
    }
    
    /**
     * A name of this act.
     * @return A name of this act.
     */
    @Override
    public String name() {
      return String.format("let[%s]", this.value());
    }
    
  }
  
  class Func<T, R> implements Act<T, R> {
    private final Function<T, R> main;
    
    public Func(Function<T, R> func) {
      this.main = func;
    }
    
    public Func(String name, Function<T, R> func) {
      this.main = Printables.function(name, func);
    }
    
    @Override
    public R perform(T value, ExecutionEnvironment executionEnvironment) {
      return this.main.apply(value);
    }
    
    @Override
    public String name() {
      return InternalUtils.isToStringOverridden(this.main) ? this.main.toString()
                                                           : "func";
    }
  }
  
  /**
   * A leaf act, which models a "sink".
   * A sink is a concept, where value is consumed only.
   *
   * As the design of leaf act of the **autotest-ca** framework, it needs to return a value on a call of `perform` method,
   * it returns `null` instead.
   *
   * @param <T> A type of value consumed by this sink object.
   */
  class Sink<T> extends Func<T, Void> {
    /**
     * Creates an instance of this class.
     * @param sink A consumer that processes a target value in the context
     */
    public Sink(Consumer<T> sink) {
      this("sink", sink);
    }
    
    public Sink(String name, Consumer<T> sink) {
      super(Printables.function(name, (T value) -> {
        sink.accept(value);
        return null;
      }));
    }
  }
  
  abstract class Source<T> implements Act<Object, T> {
    @Override
    public T perform(Object value, ExecutionEnvironment executionEnvironment) {
      return this.value();
    }
    
    protected abstract T value();
  }
}
