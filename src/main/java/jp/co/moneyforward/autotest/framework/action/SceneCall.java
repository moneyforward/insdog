package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.nop;
import static com.github.valid8j.classic.Requires.requireNonNull;

public class SceneCall implements Call {
  final Scene scene;
  final Map<String, Function<Context, Object>> assignmentResolvers;
  private final String outputFieldName;
  
  
  public SceneCall(String outputFieldName, Scene scene, Map<String, Function<Context, Object>> assignmentResolvers) {
    this.outputFieldName = requireNonNull(outputFieldName);
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = requireNonNull(assignmentResolvers);
  }
  
  public SceneCall(Scene scene, Map<String, Function<Context, Object>> assignmentResolvers) {
    this.outputFieldName = null;
    this.scene = requireNonNull(scene);
    this.assignmentResolvers = requireNonNull(assignmentResolvers);
  }
  
  String workAreaName() {
    return "work-" + objectId();
  }
  
  Map<String, Object> initializeWorkArea(Context context) {
    var ret = new HashMap<String, Object>();
    assignmentResolvers.forEach((k, r) -> ret.put(k, r.apply(context)));
    return ret;
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
  public Action toAction(ActionComposer actionComposer) {
    return actionComposer.create(this);
  }
  
  public Action begin() {
    return beginSceneCall(this);
  }
  
  public Action end() {
    if (this.outputFieldName != null)
      return endSceneCall(this);
    return endSceneCallDismissingOutput(this);
  }
  
  private static Action beginSceneCall(SceneCall sceneCall) {
    return InternalUtils.action("BEGIN@" + sceneCall.scene.name(), c -> {
      c.assignTo(sceneCall.workAreaName(), sceneCall.initializeWorkArea(c));
    });
  }
  
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
