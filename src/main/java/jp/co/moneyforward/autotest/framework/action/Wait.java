package jp.co.moneyforward.autotest.framework.action;

import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Use this only when you have no other choices.
 *
 * @param <T> The type of the variable to handle.
 */
public class Wait<T> implements Act<T, T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Wait.class);
  private final int time;
  private final TimeUnit unit;
  private final String excuse;
  
  /**
   * Creates an instance of this class.
   *
   * @param time   Time to wait.
   * @param unit   Unit of the time.
   * @param excuse An excuse to use this class.
   */
  public Wait(int time, TimeUnit unit, String excuse) {
    this.time = time;
    this.unit = requireNonNull(unit);
    this.excuse = requireNonNull(excuse);
  }
  
  @Override
  public T perform(T value, ExecutionEnvironment executionEnvironment) {
    try {
      Thread.sleep(unit.toMillis(time));
    } catch (InterruptedException e) {
      LOGGER.error("Sleep was interrupted");
      LOGGER.debug("- Stacktrace:", e);
      Thread.currentThread().interrupt();
    }
    return value;
  }
  
  @Override
  public String name() {
    return "Wait(because:" + this.excuse + ")[" + time + " " + unit + "]";
  }
}
