package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.Resolver;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A facade class of the "autotest" framework.
 */
public enum AutotestSupport {
  ;
  
  public static Scene scene(List<Call> children) {
    var builder = new Scene.Builder("default");
    children.forEach(builder::addCall);
    return builder.build();
  }
  
  /**
   * Returns a `SceneCall` object for a given
   *
   * @param outputVariableName A variable for an output map whose keys and values are variable names and their values.
   * @param scene              A scene for which a call is created
   * @param resolvers          Resolvers for variables available for the scene in the ongoing context.
   * @return A `SceneCall` object for `scene`.
   */
  public static SceneCall sceneCall(String outputVariableName, Scene scene, List<Resolver> resolvers) {
    var resolverMap = new HashMap<String, Function<Context, Object>>();
    resolvers.forEach(r -> resolverMap.put(r.variableName(), r.resolverFunction()));
    return new SceneCall(outputVariableName, scene, resolverMap);
  }
  
  public static SceneCall sceneCall(Scene scene) {
    return new SceneCall(scene);
  }
  
  public static <T, R> ActCall<T, R> actCall(String outputVariableName, Act<T, R> leaf, String inputFieldName) {
    return new ActCall<>(outputVariableName, leaf, inputFieldName);
  }
  
  public static <T, R> AssertionCall<R> assertionCall(String outputVariableName, Act<T, R> act, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    return new AssertionCall<>(actCall(outputVariableName, act, inputVariableName), assertions);
  }
}
