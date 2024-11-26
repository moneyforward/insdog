package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.List;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

public final class EnsuredCall extends CallDecorator.Base<Call> {
  private final List<Call> ensurers;
  
  /**
   * Creates an object of this class.
   *
   * @param target A target class to be decorated.
   */
  public EnsuredCall(Call target, List<Call> ensurers) {
    super(target);
    this.ensurers = List.copyOf(requireNonNull(ensurers));
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer) {
    return actionComposer.create(this);
  }
  
  /**
   * Returns "ensurer" calls.
   *
   * @return A list of "ensurer" calls.
   */
  public List<Call> ensurers() {
    return this.ensurers;
  }
}
