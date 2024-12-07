package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.annotations.PreparedBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @see EnsuredCall#EnsuredCall(SceneCall, List, String, ResolverBundle)  EnsuredCall
 */
public final class EnsuredCall extends CallDecorator.Base<SceneCall> {
  private final List<SceneCall> ensurers;
  private final ResolverBundle resolverBundle;
  
  /**
   * Creates an object of this class.
   *
   * @param target A target class to be decorated.
   */
  public EnsuredCall(SceneCall target, List<SceneCall> ensurers, ResolverBundle resolverBundle) {
    super(target);
    this.ensurers = List.copyOf(requireNonNull(ensurers));
    this.resolverBundle = resolverBundle;
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
  public List<SceneCall> ensurers() {
    return this.ensurers;
  }
  
  /**
   * DUPLICATED!!
   * Returns a map (variable store), with which a targetScene can interact to store/read data.
   * Initial values of variables are resolved by giving a `context` parameter value to each element in `resolverBundle`.
   *
   * @param ensuredCall A scene call for which a returned map is created.
   * @param context     A context in which actions created from the target scene are performed.
   * @return A data store map.
   * @see ResolverBundle
   */
  private static Map<String, Object> composeWorkingVariableStore(EnsuredCall ensuredCall,
                                                                 Context context) {
    var ret = new HashMap<String, Object>();
    ensuredCall.resolverBundle()
               .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }
  
  private ResolverBundle resolverBundle() {
    return this.resolverBundle;
  }
}
