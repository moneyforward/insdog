package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RetryCall implements Call {
  private final Call target;
  private final int interval;
  private final int retryTimes;
  private final Class<? extends Throwable> onExceptionType;
  
  /**
   * Creates an instance of this class.
   *
   */
  protected RetryCall(Call target, Class<? extends Throwable> onExceptionType, int retryTimes, int interval) {
    this.target = target;
    this.interval = interval;
    this.retryTimes = retryTimes;
    this.onExceptionType = onExceptionType;
  }
  
  @Override
  public String outputFieldName() {
    return target.outputFieldName();
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this, assignmentResolversFromCurrentCall);
  }
  
  public Call target() {
    return this.target;
  }
  
  public int times() {
    return retryTimes;
  }
  
  public Class<? extends Throwable> onException() {
    return onExceptionType;
  }
  
  public long interval() {
    return interval;
  }
  
  public TimeUnit intervalUnit() {
    return SECONDS;
  }
}
