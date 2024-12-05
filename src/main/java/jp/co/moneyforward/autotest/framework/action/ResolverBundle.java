package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.PreparedBy;
import jp.co.moneyforward.autotest.framework.annotations.When;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static jp.co.moneyforward.autotest.framework.testengine.AutotestEngine.findMethodByName;

/**
 * A bundle of variable resolvers.
 *
 * A variable resolver figures out a value of a variable specified by a variable name.
 * This class bundles a set of such resolvers and associated each resolver with a variable name which the resolver
 * should figure out the value.
 */
public class ResolverBundle extends HashMap<String, Function<Context, Object>> {
  /**
   * Creates an instance of this class.
   *
   * @param resolvers A map of resolvers from variable names to resolvers.
   */
  public ResolverBundle(Map<String, Function<Context, Object>> resolvers) {
    super(resolvers);
  }
  
  /**
   * Creates an instance of this class.
   *
   * @param resolvers A list of resolvers from which an object of this class will be created.
   */
  public ResolverBundle(List<Resolver> resolvers) {
    this(resolversToMap(resolvers));
  }
  
  /**
   * Returns a resolver bundle object which figures out both input and output variable values for a given `Scene`.
   * The variables are looked up from a variable store specified by the argument `variableStoreName`.
   *
   * For each input or output variable, a resolver will be created, and it will be an element of the returned bundle.
   *
   * @param scene             A scene for which resolver bundle is created.
   * @param variableStoreName A name of variable store, where variable values are looked up.
   * @return A new resolver bundle.
   */
  public static ResolverBundle resolverBundleFor(Scene scene, String variableStoreName) {
    return new ResolverBundle(createVariableResolversFor(scene, variableStoreName));
  }
  
  /**
   * Returns a resolver bundle which is necessary for creating a new `SceneCall` call from a `Scene` defined by
   * `targetMethod`.
   *
   * @param targetMethod     A method which creates a `Scene`.
   * @param accessModelClass A class to which `targetMethod` belongs and from which dependencies of `target` method are
   *                         searched.
   * @return A resolver bundle for a `Scene` created by `targetMethod`.
   */
  public static ResolverBundle resolverBundleFromDependenciesOf(Method targetMethod, Class<?> accessModelClass) {
    assert Scene.class.isAssignableFrom(targetMethod.getReturnType());
    return new ResolverBundle(resolversFromDependenciesOf(targetMethod, accessModelClass));
  }
  
  /**
   * Returns an empty resolver bundle.
   *
   * @return An empty resolver bundle.
   */
  public static ResolverBundle emptyResolverBundle() {
    return new ResolverBundle(List.of());
  }
  
  /**
   * Returns a variable resolvers of a given `scene` based on its children's input and output variable names.
   * The returned variable resolvers figure out the value of a given variable name from a Map context variable.
   * The context variable is specified by a `variableStoreName`.
   *
   * This is "de facto"-based method to create variable resolvers, so to say.
   *
   * @param scene             A scene for which variable resolvers are created and returned.
   * @param variableStoreName A name of a context variable that stores a map.
   *                          From the map, the returned resolvers figure out the values of given variable names,
   *                          which are used by the scene.
   * @return A list of variable resolvers.
   */
  private static List<Resolver> createVariableResolversFor(Scene scene, String variableStoreName) {
    return Resolver.resolversFor(Stream.concat(scene.inputVariableNames().stream(),
                                               scene.outputVariableNames().stream())
                                       .distinct()
                                       .toList(), variableStoreName
    );
  }
  
  /**
   * Creates resolvers (`Resolver`) for a scene call associated with a scene returned by `method`.
   *
   * Either `@DependsOn` or `@When` annotations attached to `method` tells the framework that methods which it depends on.
   * This method scans `@Export` attached to those methods to figure out variables available to the `method`.
   *
   * This is "de juro"-based method to create variable resolvers, so to say.
   *
   * @param method           A method that returns a `Scene` object.
   * @param accessModelClass An access model class to which method belongs.
   * @return A list of resolvers that a scene returned by `method` requires.
   */
  private static List<Resolver> resolversFromDependenciesOf(Method method, Class<?> accessModelClass) {
    return InternalUtils.concat(variableResolversFor(annotationValuesOf(method, PreparedBy.class, PreparedBy::value), accessModelClass).stream(),
                                variableResolversFor(annotationValuesOf(method, DependsOn.class, DependsOn::value), accessModelClass).stream(),
                                variableResolversFor(annotationValuesOf(method, When.class, When::value), accessModelClass).stream()).toList();
  }
  
  private static <A extends Annotation> String[] annotationValuesOf(Method method, Class<A> annotationClass, Function<A, String[]> func) {
    A[] annotationsByType = method.getAnnotationsByType(annotationClass);
    if (annotationsByType.length == 0)
      return new String[0];
    return Arrays.stream(annotationsByType).flatMap(a -> Arrays.stream(func.apply(a))).toArray(String[]::new);
  }
  
  /**
   * Returns a list of variable resolvers (`Resolver`) for scenes specified by their names (`sceneNames).
   *
   * Creates variable resolvers for a scene created from a method `m`.
   *
   * The scene created by `m` will be called "scene `m`" in this description, hereafter.
   * Variable resolvers created based on annotations specified by `dependencyAnnotationClass`, which is usually `@DependsOn`.
   *
   * @param sceneNames       Scenes for which variable resolvers are searched, created, and returned.
   * @param accessModelClass An access model class that defines a set of scene creating methods, on which `m` potentially depends.
   * @return Resolvers for a scene created by `m`.
   * @see Resolver
   */
  private static List<Resolver> variableResolversFor(String[] sceneNames, Class<?> accessModelClass) {
    if (sceneNames.length == 0)
      return emptyList();
    return variableResolversFor(sceneNames,
                                dependencySceneName -> exportedVariablesOf(methodForName(dependencySceneName, accessModelClass)));
  }
  
  /**
   * Returns `Resolver`s for variables exported by scenes specified by `sceneNames`.
   * A resolver in the list returns a value of a variable defined in a scene that exports it with the same name.
   *
   * @param sceneNames                    Names of `Scene`s.
   * @param exportedVariableNamesResolver A function that returns a list of exported variable names by a scene specified as a parameter.
   * @return `Resolver`s for variables exported by specified scenes.
   */
  private static List<Resolver> variableResolversFor(String[] sceneNames,
                                                     Function<String, List<String>> exportedVariableNamesResolver) {
    return Arrays.stream(sceneNames)
                 .flatMap((String sceneName) -> exportedVariablesResolversOf(sceneName, exportedVariableNamesResolver).stream())
                 .toList();
  }
  
  private static List<Resolver> exportedVariablesResolversOf(String sceneName, Function<String, List<String>> exportedVariablesNamesResolver) {
    return exportedVariablesNamesResolver.apply(sceneName)
                                         .stream()
                                         .map((String variableNane) -> Resolver.resolverFor(variableNane, sceneName)).toList();
  }
  
  private static List<String> exportedVariablesOf(Method method) {
    return List.of(method.getAnnotation(Export.class).value());
  }
  
  private static Map<String, Function<Context, Object>> resolversToMap(List<Resolver> resolvers) {
    Map<String, Function<Context, Object>> resolverMap = new HashMap<>();
    for (Resolver resolver : resolvers) {
      resolverMap.put(resolver.variableName(), resolver.resolverFunction());
    }
    return resolverMap;
  }
  
  private static Method methodForName(String methodName, Class<?> classObject) {
    return findMethodByName(methodName,
                            classObject).orElseThrow(() -> messageForNoSuchMethod(classObject, methodName));
  }
  
  
  private static NoSuchElementException messageForNoSuchMethod(Class<?> accessModelClass, String dependencySceneName) {
    return new NoSuchElementException(String.format("A method named:'%s' was not found in class:'%s'",
                                                    dependencySceneName,
                                                    accessModelClass.getCanonicalName()));
  }
}
