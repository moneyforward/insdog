package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.HashMap;
import java.util.Map;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class to model a "call" to a `Scene`.
 */
public final class SceneCall implements Call {
  private final Scene scene;
  private final ResolverBundle variableResolverBundle;
  private final String outputVariableStoreName;
  
  public SceneCall(String outputVariableStoreName, Scene scene, ResolverBundle resolverBundle) {
    this.outputVariableStoreName = requireNonNull(outputVariableStoreName);
    this.scene = requireNonNull(scene);
    this.variableResolverBundle = requireNonNull(resolverBundle);
  }
  
  /**
   * Returns a `Scene` object targeted by this call.
   *
   * @return A `Scene` object.
   */
  public Scene targetScene() {
    return this.scene;
  }
  
  public String outputVariableStoreName() {
    return this.outputVariableStoreName;
  }
  
  public static String workingVariableStoreNameFor(String objectId) {
    return "work-" + objectId;
  }
  
  public ResolverBundle variableResolverBundle() {
    return variableResolverBundle;
  }
  
  /**
   * Returns currently ongoing working variable store.
   *
   * @param context A context in which this call is being executed.
   * @return A currently ongoing working variable store.
   */
  public Map<String, Object> workingVariableStore(Context context) {
    return context.valueOf(workingVariableStoreNameFor(this.targetScene().oid()));
  }
  
  
  @Override
  public Action toAction(ActionComposer actionComposer, ResolverBundle ongoingResolverBundle) {
    return actionComposer.create(this, ongoingResolverBundle);
  }
  
  public Action begin() {
    return beginSceneCall(this);
  }
  
  public Action end() {
    return endSceneCall(this);
  }
  
 
  private static Action beginSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(),
                                c -> c.assignTo(workingVariableStoreNameFor(sceneCall.targetScene().oid()),
                                                createWorkingVariableStore(sceneCall, c)));
  }
  
  private static Map<String, Object> createWorkingVariableStore(SceneCall sceneCall,
                                                                Context context) {
    var ret = new HashMap<String, Object>();
    sceneCall.variableResolverBundle()
             .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }

  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputVariableStoreName(), c.valueOf(workingVariableStoreNameFor(sceneCall.targetScene().oid())));
      c.unassign(workingVariableStoreNameFor(sceneCall.targetScene().oid()));
    });
  }
}
