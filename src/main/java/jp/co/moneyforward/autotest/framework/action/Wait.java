package jp.co.moneyforward.autotest.framework.action;

import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Use this only when you have no other choices.
 *
 * @param <T> The type of the variable to handle.
 */
public class Wait<T> implements LeafAct<T, T> {
  private final int time;
  private final TimeUnit unit;
  
  public Wait(int time, TimeUnit unit) {
    this.time = time;
    this.unit = requireNonNull(unit);
  }
  @Override
  public T perform(T value, ExecutionEnvironment executionEnvironment) {
    try {
      Thread.sleep(unit.toMillis(time));
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }
    return value;
  }
  
  @Override
  public String name() {
    return "Wait[" + time + " " + unit + "]";
  }
}
