package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.framework.annotations.PreparedBy;

import java.util.List;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

/**
 * A class to provide a construct to "ensure" a specific state checked by `target` call.
 *
 * Each element in `ensurers` is performed first, then `target` is performed.
 * This procedure is repeated until:
 *
 * * Both an element from `ensurers` and the `target` are successfully performed.
 * * Either an element from `ensurer` or the `target` throws a non-recoverable exception.
 *
 * `target` should be a call to an action, which succeeds when and only when the state you want to ensure is satisfied.
 * Each element in `ensurers` should be an action that tries to make the state meet your expectation.
 * The ensurers should be sorted by an ascending order of their execution cost.
 *
 * @see PreparedBy
 * @see EnsuredCall#EnsuredCall(Call, List)
 */
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
