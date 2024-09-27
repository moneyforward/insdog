package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class to model a "call" to a `Scene`.
 */
public final class SceneCall implements Call {
  private final Scene scene;
  private final ResolverBundle assignmentResolvers;
  private final String outputVariableStoreName;
  
  public SceneCall(String outputVariableStoreName, Scene scene, ResolverBundle resolverBundle) {
    this.outputVariableStoreName = requireNonNull(outputVariableStoreName);
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = requireNonNull(resolverBundle);
  }
  
  /**
   * This constructor should be removed.
   *
   * @param outputVariableStoreName
   * @param scene                   A scene to be called.
   */
  public SceneCall(String outputVariableStoreName, Scene scene) {
    this.outputVariableStoreName = outputVariableStoreName;
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = null;
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
   * Returns a name of working variable store, which stores variables of child calls of the `targetScene`.
   * This method returns a unique string among all the `SceneCall` objects.
   *
   * @return A working variable store name.
   */
  public String workingVariableStoreName() {
    return "work-" + objectId();
  }
  
  public Optional<ResolverBundle> assignmentResolvers() {
    return Optional.ofNullable(assignmentResolvers);
  }
  
  /**
   * Returns currently ongoing working variable store.
   *
   * @param context A context in which this call is being executed.
   * @return A currently ongoing working variable store.
   */
  public Map<String, Object> workingVariableStore(Context context) {
    return context.valueOf(workingVariableStoreName());
  }
  
  public List<Resolver> outputVariableStoreResolvers() {
    return targetScene().resolversFor(outputVariableStoreName());
  }
  public List<Resolver> workingVariableStoreResolvers() {
    return targetScene().resolversFor(workingVariableStoreName());
  }
 
  @Override
  public Action toAction(ActionComposer actionComposer, ResolverBundle resolversFromCurrentCall) {
    return actionComposer.create(this, resolversFromCurrentCall);
  }
  
  public Action begin(ResolverBundle assignmentResolversFromCurrentCall) {
    return beginSceneCall(this, assignmentResolversFromCurrentCall);
  }
  
  public Action end() {
    if (this.outputVariableStoreName() != null)
      return endSceneCall(this);
    return endSceneCallDismissingOutput(this);
  }
  
  private static Map<String, Object> createWorkingVariableStore(SceneCall sceneCall,
                                                                Context context,
                                                                ResolverBundle assignmentResolversFromCurrentCall) {
    var ret = new HashMap<String, Object>();
    sceneCall.assignmentResolvers()
             .orElse(assignmentResolversFromCurrentCall)
             .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }
  
  
  private static Action beginSceneCall(SceneCall sceneCall, ResolverBundle resolverBundle) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(),
                                c -> c.assignTo(sceneCall.workingVariableStoreName(),
                                                createWorkingVariableStore(sceneCall,
                                                                           c,
                                                                           resolverBundle)));
  }
  
  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputVariableStoreName, c.valueOf(sceneCall.workingVariableStoreName()));
      c.unassign(sceneCall.workingVariableStoreName());
    });
  }
  
  private static Action endSceneCallDismissingOutput(SceneCall sceneCall) {
    return InternalUtils.action("END[dismissingOutput]@" + sceneCall.scene.name(), c -> {
    });
  }
  
  private String objectId() {
    return scene.name() + ":" + System.identityHashCode(this);
  }
}
