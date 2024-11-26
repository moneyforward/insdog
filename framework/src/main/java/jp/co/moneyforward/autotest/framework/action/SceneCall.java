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
  
  /**
   * Creates an instance of this class.
   *
   * outputVariableStoreName specifies a name of a context variable (**actionunit**), to which the output of `scene`
   * is written.
   *
   * `resolverBundle` is used to compute input variable values.
   *
   * @param outputVariableStoreName A name of variable store, to which the `scene` writes its output.
   * @param scene A scene to be performed by this call.
   * @param resolverBundle A bundle of resolvers.
   */
  public SceneCall(String outputVariableStoreName,
                   Scene scene,
                   ResolverBundle resolverBundle) {
    this.outputVariableStoreName = requireNonNull(outputVariableStoreName);
    this.scene = requireNonNull(scene);
    this.variableResolverBundle = requireNonNull(resolverBundle);
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer) {
    return actionComposer.create(this);
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
  
  /**
   * Returns a working variable store name for a given object ID.
   *
   * @param objectId An object ID for which a working variable store is created.
   * @return A working variable store name.
   */
  public static String workingVariableStoreNameFor(String objectId) {
    return "work-" + objectId;
  }
  
  /**
   * Returns a bundle of variable resolvers of this object.
   *
   * @return A bundle of variable resolvers.
   */
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
  
  
  /**
   * Returns an action, which marks a beginning of a sequence of main actions.
   *
   * The action copies a map of the InsDog's framework variables for this scene call to a context variable whose name
   * is computed by `workingVariableStoreNameFor(this.targetScene().oid())`.
   *
   * @return An action, which marks a beginning of a sequence of main actions.
   */
  public Action begin() {
    return beginSceneCall(this);
  }
  
  /**
   * Returns an action, which marks an ending of a sequence of main actions.
   *
   * The action copies to a map of the InsDog's framework variables for this scene call from a context variable whose
   * name is computed by `workingVariableStoreNameFor(this.targetScene().oid())`.
   *
   * @return An action, which marks an ending of a sequence of main actions.
   */
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
