package jp.co.moneyforward.autotest.framework.utils;

import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.action.*;
import jp.co.moneyforward.autotest.framework.core.Resolver;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A facade class of the "autotest" framework.
 */
public enum AutotestSupport {
  ;
  
  public static SceneCall sceneCall(String outputFieldName, List<Call> children, List<Resolver> assignments) {
    var scene = scene(children);
    return sceneCall(outputFieldName, scene, assignments);
  }
  
  public static SceneCall sceneCall(String outputFieldName, Scene scene, List<Resolver> assignments) {
    var resolverMap = new HashMap<String, Function<Context, Object>>();
    assignments.forEach(r -> resolverMap.put(r.parameterName(), r.resolverFunction()));
    return new SceneCall(outputFieldName, scene, resolverMap);
  }
  
  public static SceneCall sceneCall(Scene scene, List<Resolver> assignments) {
    var resolverMap = new HashMap<String, Function<Context, Object>>();
    assignments.forEach(r -> resolverMap.put(r.parameterName(), r.resolverFunction()));
    return new SceneCall(scene, resolverMap);
  }
  
  
  public static Scene scene(List<Call> children) {
    var builder = new Scene.Builder("default");
    children.forEach(builder::addCall);
    return builder.build();
  }
  
  public static <T, R> LeafActCall<T, R> leafCall(String outputVariableName, LeafAct<T, R> leaf, String inputFieldName) {
    return new LeafActCall<>(outputVariableName, leaf, inputFieldName);
  }
  
  public static <T, R> Call.PipelinedActCall<T, R> pipelineCall(String outputVariableName, PipelinedAct<T, ?, R> pipeline, String inputFieldName) {
    return new Call.PipelinedActCall<>(outputVariableName, pipeline, inputFieldName);
  }
  
  public static <T, R> AssertionActCall<T, R> assertionCall(String outputVariableName, Act<T, R> act, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    if (act instanceof LeafAct<T, R>)
      return new AssertionActCall<>(leafCall(outputVariableName, (LeafAct<T, R>) act, inputVariableName), assertions);
    else if (act instanceof PipelinedAct<T, ?, R>)
      return new AssertionActCall<>(pipelineCall(outputVariableName, (PipelinedAct<T, ?, R>) act, inputVariableName), assertions);
    throw new AssertionError();
  }
  
  public static <T, R> AssertionActCall<T, R> assertionCall(String outputVariableName, LeafAct<T, R> leafAct, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    return new AssertionActCall<>(leafCall(outputVariableName, leafAct, inputVariableName), assertions);
  }
  
  public static <T, R> AssertionActCall<T, R> assertionCall(String outputVariableName, PipelinedAct<T, ?, R> pipelinedAct, List<Function<R, Statement<R>>> assertions, String inputVariableName) {
    return new AssertionActCall<>(pipelineCall(outputVariableName, pipelinedAct, inputVariableName), assertions);
  }
}
