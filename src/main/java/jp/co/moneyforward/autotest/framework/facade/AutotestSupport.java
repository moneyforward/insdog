package jp.co.moneyforward.autotest.framework.facade;

import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.action.Call;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.Resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A facade class of the "autotest" framework.
 */
public enum AutotestSupport {
  ;
  
  public static Call.SceneCall sceneCall(String outputFieldName, List<Call> children, List<Resolver> assignments) {
    var scene = scene(children);
    return sceneCall(outputFieldName, scene, assignments);
  }
  
  public static Call.SceneCall sceneCall(String outputFieldName, Scene scene, List<Resolver> assignments) {
    var resolverMap = new HashMap<String, Function<Context, Object>>();
    assignments.forEach(r -> resolverMap.put(r.parameterName(), r.resolverFunction()));
    return new Call.SceneCall(outputFieldName, scene, resolverMap);
  }
  
  public static Scene scene(List<Call> children) {
    var builder = new Scene.Builder("default");
    children.forEach(builder::addCall);
    return builder.build();
  }
  
  public static Function<Context, Object> valueFrom(String sourceSceneName, String fieldNameInSourceScene) {
    return context -> context.<Map<String, Object>>valueOf(sourceSceneName).get(fieldNameInSourceScene);
  }
  
  public static <R> Call.LeafActCall<?, R> leafCall(String outputVariableName, LeafAct<?, R> leaf) {
    return leafCall(outputVariableName, leaf, "NONE");
  }
  
  public static <T, R> Call.LeafActCall<T, R> leafCall(String outputVariableName, LeafAct<T, R> leaf, String inputFieldName) {
    return new Call.LeafActCall<>(outputVariableName, leaf, inputFieldName);
  }
  
  public static <T, R> Call.AssertionActCall<T, R> assertionCall(String outputVariableName, LeafAct<T, R> leafAct, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    return new Call.AssertionActCall<>(leafCall(outputVariableName, leafAct, inputVariableName), assertions);
  }
}
