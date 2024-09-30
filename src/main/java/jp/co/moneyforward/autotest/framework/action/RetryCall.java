package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class RetryCall extends CallDecorator.Base<Call>  {
  private final int interval;
  private final int retryTimes;
  private final Class<? extends Throwable> onExceptionType;
  
  /**
   * Creates an instance of this class.
   */
  public RetryCall(Call target, Class<? extends Throwable> onExceptionType, int retryTimes, int interval) {
    super(target);
    this.interval = interval;
    this.retryTimes = retryTimes;
    this.onExceptionType = onExceptionType;
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, ResolverBundle ongoingResolverBundle) {
    return actionComposer.create(this, ongoingResolverBundle);
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
