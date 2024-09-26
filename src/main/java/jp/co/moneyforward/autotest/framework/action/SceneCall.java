package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A class to model a "call" to a `Scene`.
 */
public final class SceneCall implements Call {
  final Scene scene;
  final Map<String, Function<Context, Object>> assignmentResolvers;
  private final String outputVariableName;
  
  public SceneCall(String outputVariableName, Scene scene, Map<String, Function<Context, Object>> assignmentResolvers) {
    this.outputVariableName = requireNonNull(outputVariableName);
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = requireNonNull(assignmentResolvers);
  }
  
  /**
   * This constructor should be removed.
   *
   * @param scene A scene to be called.
   */
  public SceneCall(Scene scene) {
    this.outputVariableName = null;
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
  
  /**
   * Returns a name of working variable store, which stores variables of child calls of the `targetScene`.
   * This method returns a unique string among all the `SceneCall` objects.
   *
   * @return A working variable store name.
   */
  public String workingVariableStoreName() {
    return "work-" + objectId();
  }
  
  public Optional<Map<String, Function<Context, Object>>> assignmentResolvers() {
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
  
  @Override
  public String outputVariableName() {
    return this.outputVariableName;
  }
  
  @Override
  public List<String> requiredVariableNames() {
    return scene.children()
                .stream()
                .flatMap(c -> c.requiredVariableNames()
                               .stream())
                .distinct()
                .toList();
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this, assignmentResolversFromCurrentCall);
  }
  
  public Action begin(Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return beginSceneCall(this, assignmentResolversFromCurrentCall);
  }
  
  public Action end() {
    if (this.outputVariableName != null)
      return endSceneCall(this);
    return endSceneCallDismissingOutput(this);
  }
  
  private static Map<String, Object> createWorkingVariableStore(SceneCall sceneCall,
                                                                Context context,
                                                                Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    var ret = new HashMap<String, Object>();
    sceneCall.assignmentResolvers()
             .orElse(assignmentResolversFromCurrentCall)
             .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }
  
  
  private static Action beginSceneCall(SceneCall sceneCall, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(),
                                c -> c.assignTo(sceneCall.workingVariableStoreName(),
                                                createWorkingVariableStore(sceneCall,
                                                                           c,
                                                                           assignmentResolversFromCurrentCall)));
  }
  
  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputVariableName, c.valueOf(sceneCall.workingVariableStoreName()));
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
