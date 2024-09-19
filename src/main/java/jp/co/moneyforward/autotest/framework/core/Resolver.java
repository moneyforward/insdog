package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

/**
 * `Resolver` figures out a variable designated by `variableName` in a context.
 * Note that a resolver takes a variable name only, and it returns a value for a given variable name.
 *
 * A resolver provides a "namespace" of variables available for a scene.
 *
 * @param variableName A name of a variable whose value is to be resolved
 * @param resolverFunction A function that resolves the value.
 */
public record Resolver(String variableName, Function<Context, Object> resolverFunction) {
  /**
   * Typically, this function is called by a method `resolverFor` and the `variableName` passed to it should be used as `variableNameInScene` for this method.
   *
   * @param sceneName A name of a scene from which the value is resolved.
   * @param variableNameInScene A name of a variable whose value is to be resolved.
   * @return A function that gives the value of `variableNameInScene` from a `Context` object.
   */
  public static Function<Context, Object> valueFrom(String sceneName, String variableNameInScene) {
    return context -> context.<Map<String, Object>>valueOf(sceneName).get(variableNameInScene);
  }
  
  /**
   * Returns a `Resolver` object, which resolves a value of a variable designated by `variableName` exported by a scene `sceneName`.
   *
   * @param sceneName A name of a scene by which `variableName` is exported.
   * @param variableName A name of a variable to be resolved by the returned `Resolver`.
   * @return A `Resolver` object.
   */
  public static Resolver resolverFor(String sceneName, String variableName) {
    return new Resolver(variableName, valueFrom(sceneName, variableName));
  }
}
