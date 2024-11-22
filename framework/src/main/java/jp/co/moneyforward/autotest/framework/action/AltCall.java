package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class AltCall extends CallDecorator.Base<Call> {
  private final List<Call> alternatives;
  
  /**
   * Creates an object of this class.
   *
   * @param target A target class to be decorated.
   */
  public AltCall(Call target, List<Call> alternatives) {
    super(target);
    this.alternatives = requireNonNull(alternatives);
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer) {
    return null;
  }
}
