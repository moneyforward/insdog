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
final public class SceneCall implements Call {
  final Scene scene;
  final Map<String, Function<Context, Object>> assignmentResolvers;
  private final String outputFieldName;
  
  public SceneCall(String outputFieldName, Scene scene, Map<String, Function<Context, Object>> assignmentResolvers) {
    this.outputFieldName = requireNonNull(outputFieldName);
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = requireNonNull(assignmentResolvers);
  }
  
  public SceneCall(Scene scene) {
    this.outputFieldName = null;
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = null;
  }
  
  Action toSequentialAction(Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall, ActionComposer actionComposer) {
    return this.scene.toSequentialAction(assignmentResolversFromCurrentCall, actionComposer);
  }
  
  String workAreaName() {
    return "work-" + objectId();
  }
  
  Map<String, Object> initializeWorkArea(Context context, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    var ret = new HashMap<String, Object>();
    assignmentResolvers().orElse(assignmentResolversFromCurrentCall)
                         .forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
  }
  
  public Optional<Map<String, Function<Context, Object>>> assignmentResolvers() {
    return Optional.ofNullable(assignmentResolvers);
  }
  
  Map<String, Object> workArea(Context context) {
    return context.valueOf(workAreaName());
  }
  
  String objectId() {
    return scene.name() + ":" + System.identityHashCode(scene);
  }
  
  @Override
  public String outputFieldName() {
    return requireNonNull(this.outputFieldName);
  }
  
  @Override
  public List<String> inputFieldNames() {
    return scene.children()
                .stream()
                .flatMap(c -> c.inputFieldNames()
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
    if (this.outputFieldName != null)
      return endSceneCall(this);
    return endSceneCallDismissingOutput(this);
  }
  
  private static Action beginSceneCall(SceneCall sceneCall, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(),
                                c -> c.assignTo(sceneCall.workAreaName(),
                                                sceneCall.initializeWorkArea(c, assignmentResolversFromCurrentCall)));
  }
  
  /*
   * Copies the map stored as "work area" to `outputFieldName` variable.
   */
  private static Action endSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("END@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.outputFieldName(), c.valueOf(sceneCall.workAreaName()));
      c.unassign(sceneCall.workAreaName());
    });
  }
  
  private static Action endSceneCallDismissingOutput(SceneCall sceneCall) {
    return InternalUtils.action("END[dismissingOutput]@" + sceneCall.scene.name(), c -> {
    });
  }
}
