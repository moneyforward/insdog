package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.HashMap;
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
  public SceneCall(String outputVariableStoreName, Scene scene, String inputVariableStoreName) {
    this.outputVariableStoreName = outputVariableStoreName;
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = new ResolverBundle(Resolver.resolversFor(inputVariableStoreName, this.targetScene().outputVariableNames()));
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
  
  public static String workingVariableStoreNameFor(Scene scene) {
    return "work-" + objectId(scene);
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
    return context.valueOf(workingVariableStoreNameFor(this.targetScene()));
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
                                c -> c.assignTo(workingVariableStoreNameFor(sceneCall.targetScene()),
                                                createWorkingVariableStore(sceneCall,
                                                                           c,
                                                                           resolverBundle)));
  }
  
  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputVariableStoreName, c.valueOf(workingVariableStoreNameFor(sceneCall.targetScene())));
      c.unassign(workingVariableStoreNameFor(sceneCall.targetScene()));
    });
  }
  
  private static Action endSceneCallDismissingOutput(SceneCall sceneCall) {
    return InternalUtils.action("END[dismissingOutput]@" + sceneCall.scene.name(), c -> {
    });
  }
  
  private static String objectId(Scene scene1) {
    return scene1.name() + ":" + System.identityHashCode(scene1);
  }
}
