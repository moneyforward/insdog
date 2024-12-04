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
public final class SceneCall implements Call, WithOid {
  private final Scene scene;
  private final ResolverBundle resolverBundle;
  private final String outputVariableStoreName;
  
  /**
   * Creates an instance of this class.
   *
   * outputVariableStoreName specifies a name of a context variable (**actionunit**), to which the output of `scene`
   * is written.
   *
   * `resolverBundle` is used to compute input variable values.
   *
   * @param scene                   A scene to be performed by this call.
   * @param outputVariableStoreName A name of variable store, to which the `scene` writes its output.
   * @param resolverBundle          A bundle of resolvers.
   */
  public SceneCall(Scene scene,
                   String outputVariableStoreName,
                   ResolverBundle resolverBundle) {
    this.outputVariableStoreName = requireNonNull(outputVariableStoreName);
    this.scene = requireNonNull(scene);
    this.resolverBundle = requireNonNull(resolverBundle);
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer) {
    return actionComposer.create(this);
  }
  
  /**
   * Returns an object identifier of this object.
   *
   * @return An object identifier of this object.
   */
  @Override
  public String oid() {
    return this.targetScene().oid();
  }
  
  /**
   * Returns a `Scene` object targeted by this call.
   *
   * @return A `Scene` object.
   */
  public Scene targetScene() {
    return this.scene;
  }
  
  /**
   * A name of "output" variable store, where this `SceneCall` writes its final result at the end of execution.
   *
   * @return A name of variable store, which this `SceneCall` writes its result to.
   */
  public String outputVariableStoreName() {
    return this.outputVariableStoreName;
  }
  
  /**
   * Returns a bundle of variable resolvers of this object.
   *
   * @return A bundle of variable resolvers.
   */
  public ResolverBundle resolverBundle() {
    return resolverBundle;
  }
  
  /**
   * Returns currently ongoing working variable store.
   *
   * @param context A context in which this call is being executed.
   * @return A currently ongoing working variable store.
   */
  public Map<String, Object> workingVariableStore(Context context) {
    return context.valueOf(this.workingVariableStoreName());
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
  
  /**
   * Creates an action that prepares variable store for a given `SceneCall` object.
   *
   * @param sceneCall A call for which a preparation action is created.
   * @return A created Action.
   */
  private static Action beginSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(),
                                c -> c.assignTo(sceneCall.workingVariableStoreName(),
                                                composeWorkingVariableStore(sceneCall, c)));
  }
  
  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputVariableStoreName(), c.valueOf(sceneCall.workingVariableStoreName()));
      c.unassign(sceneCall.workingVariableStoreName());
    });
  }
  
  
  /**
   * Returns a map (variable store), with which a targetScene can interact to store/read data.
   * Initial values of variables are resolved by giving a `context` parameter value to each element in `resolverBundle`.
   *
   * @param sceneCall A scene call for which a returned map is created.
   * @param context   A context in which actions created from the target scene are performed.
   * @return A data store map.
   * @see ResolverBundle
   */
  private static Map<String, Object> composeWorkingVariableStore(SceneCall sceneCall,
                                                                 Context context) {
    var ret = new HashMap<String, Object>();
    sceneCall.resolverBundle()
             .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }
}
