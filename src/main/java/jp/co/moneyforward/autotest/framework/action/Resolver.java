package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.forms.Printables;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.lang.String.format;

/// 
/// `Resolver` figures out a variable designated by `variableName` in a context.
/// Note that a resolver takes a variable name only, and it returns a value for a given variable name.
/// 
/// A resolver provides a "namespace" of variables available for a scene.
/// 
/// @param variableName     A name of a variable whose value is to be resolved by this object.
/// @param resolverFunction A function that resolves the value.
/// 
public record Resolver(String variableName, Function<Context, Object> resolverFunction) {
  /// 
  /// Typically, this function is called by a method `resolverFor` and the `variableName` passed to it should be used as `variableNameInScene` for this method.
  /// 
  /// @param variableName      A name of a variable whose value is to be resolved.
  /// @param variableStoreName A name of a scene in which the value of the variable is resolved.
  /// @return A function that gives the value of `variableNameInScene` from a `Context` object.
  /// 
  public static Function<Context, Object> resolver(String variableName, String variableStoreName) {
    return Printables.function(format("resolve[%s][%s]", variableName, variableStoreName),
                               context -> context.defined(variableStoreName) ? context.<Map<String, Object>>valueOf(variableStoreName).get(variableName)
                                                                             : null);
  }
  
  /// 
  /// Returns a `Resolver` object, which resolves a value of a variable designated by `variableName` exported by a scene `sceneName`.
  /// A `sceneName` must be a name of a scene, which is guaranteed to be performed one and only once during one test execution.
  /// 
  /// @param variableName      A name of a variable to be resolved by the returned `Resolver`.
  /// @param variableStoreName A name of a scene by which `variableName` is exported.
  /// @return A `Resolver` object.
  /// 
  public static Resolver resolverFor(String variableName, String variableStoreName) {
    return new Resolver(variableName, resolver(variableName, variableStoreName));
  }
  
  /// 
  /// Returns a list of resolvers for variables specified by `variableNames`.
  /// Resolvers in the list try to find a variable in a variable store specified by `variableStoreName`.
  /// 
  /// @param variableNames     Names of variables to be resolved by returned resolvers.
  /// @param variableStoreName A name of variable store from which values of variables are looked up.
  /// @return A list of resolvers.
  /// 
  public static List<Resolver> resolversFor(List<String> variableNames, String variableStoreName) {
    requireNonNull(variableStoreName);
    return variableNames.stream()
                        .map(n -> new Resolver(n, (Context c) -> c.<Map<String, Object>>valueOf(variableStoreName).get(n)))
                        .toList();
  }
}
