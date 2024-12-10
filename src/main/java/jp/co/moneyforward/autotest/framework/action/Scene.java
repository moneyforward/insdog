package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.actions.web.Value;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.util.Arrays.asList;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.*;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.simpleClassNameOf;

/**
 * An interface that represents a reusable unit of an action in autotest-ca's programming model.
 * An instance of this object contains 0 or more {@link Act} instances.
 *
 * Note that `Scene` uses the same map for both input and output.
 */
public interface Scene extends WithOid {
  /**
   * A default value of default variable name for `Scene.Builder` and `@Export`.
   * This value is currently defined `page` for historical reason.
   * However, **InspektorDog** is designed not only for GUI end-to-end test, but a general purpose testing framework.
   * The value should be changed to a more context neutral keyword, such as `var` or `session`.
   */
  String DEFAULT_DEFAULT_VARIABLE_NAME = "page";
  
  /**
   * Creates a scene by chaining acts.
   * When you need to handle multiple variables, use {@link Scene.Builder} directly.
   *
   * @param variableName A variable chained acts read input value from and write output value to.
   * @param acts         Acts from which a scene is created.
   * @return Created scene.
   */
  static Scene create(String variableName, Act<?, ?>... acts) {
    Scene.Builder b = new Builder(variableName);
    for (Act<?, ?> act : acts) {
      b.add(act);
    }
    return b.build();
  }
  
  /**
   * Creates a scene by chaining acts.
   * This method internally calls {@link Scene#create(String, Act[])} using {@link Scene#DEFAULT_DEFAULT_VARIABLE_NAME} as a `variableName`.
   *
   * When you need to handle other variables, use {@link Scene#create(String, Act[])} or {@link Scene.Builder}, instead.
   *
   * @param acts Acts from which a scene is created.
   * @return Created scene.
   */
  static Scene create(Act<?, ?>... acts) {
    return create(Scene.DEFAULT_DEFAULT_VARIABLE_NAME, acts);
  }
  
  /**
   * Creates a sequential action from the child calls of this object
   *
   * @param actionComposer A visitor that builds a sequential action from child calls of this object.
   * @return A sequential action created from child calls
   * @see Scene#children()
   */
  default Action toSequentialAction(ActionComposer actionComposer) {
    return sequential(toActions(actionComposer));
  }
  
  /**
   * Returns members of this scene object, which are executed as "children".
   *
   * @return members of this scene object.
   */
  List<Call> children();
  
  /**
   * Returns an object identifier of this object.
   *
   * @return An object identifier of this object.
   */
  String oid();
  
  /**
   * Returns a name of this object.
   * The returned string will appear in an action tree printed during the action execution.
   *
   * @return A name of this object.
   */
  default String name() {
    return simpleClassNameOf(this.getClass());
  }
  
  /**
   * Returns a list of variables that are assigned by child scenes of this object.
   *
   * @return A list of variables that are assigned by child scenes of this object.
   */
  default List<String> outputVariableNames() {
    return this.children()
               .stream()
               .flatMap(Scene::outputVariableNamesOf)
               .distinct()
               .toList();
  }
  
  /**
   * Returns a list of variables that are accessed by child scenes of this object.
   *
   * @return A list of variables that are accessed by child scenes of this object.
   */
  default List<String> inputVariableNames() {
    return this.children()
               .stream()
               .flatMap(Scene::inputVariableNamesOf)
               .distinct()
               .toList();
  }
  
  private static Stream<String> outputVariableNamesOf(Call call) {
    return switch (call) {
      case SceneCall sceneCall -> sceneCall.targetScene().outputVariableNames().stream();
      case ActCall<?, ?> actCall -> Stream.of(actCall.outputVariableName());
      case EnsuredCall ensuredCall -> outputVariableNamesOf(ensuredCall);
      case CallDecorator<?> callDecorator -> outputVariableNamesOf(callDecorator.targetCall());
    };
  }
  
  private static Stream<String> outputVariableNamesOf(EnsuredCall ensuredCall) {
    return Stream.concat(outputVariableNamesOf(ensuredCall.targetCall()),
                         ensuredCall.ensurers()
                                    .stream()
                                    .flatMap(Scene::outputVariableNamesOf)).distinct();
  }
  
  private static Stream<String> inputVariableNamesOf(Call call) {
    return switch (call) {
      case SceneCall sceneCall -> sceneCall.targetScene().inputVariableNames().stream();
      case ActCall<?, ?> actCall -> Stream.of(actCall.inputVariableName());
      case EnsuredCall ensuredCall -> inputVariableNamesOf(ensuredCall);
      case CallDecorator<?> callDecorator -> inputVariableNamesOf(callDecorator.targetCall());
    };
  }
  
  private static Stream<String> inputVariableNamesOf(EnsuredCall ensuredCall) {
    return Stream.concat(inputVariableNamesOf(ensuredCall.targetCall()),
                         ensuredCall.ensurers()
                                    .stream()
                                    .flatMap(Scene::inputVariableNamesOf)).distinct();
  }
  
  private List<Action> toActions(ActionComposer actionComposer) {
    return children().stream()
                     .map((Call each) -> each.toAction(actionComposer))
                     .flatMap(InternalUtils::flattenIfSequential)
                     .toList();
  }
  
  /**
   * A builder for `Scene` class.
   *
   * @see Scene
   */
  class Builder implements WithOid {
    final String defaultVariableName;
    private final List<Call> children = new LinkedList<>();
    private String name = null;
    
    /**
     * Creates an instance of this class.
     *
     * Note that `defaultVariableName` is only used by this `Builder`, not directly by the `Scene` built by this object.
     *
     * @param defaultVariableName A name of field used when use `add` methods without explicit input/output target field names.
     */
    public Builder(String defaultVariableName) {
      this.defaultVariableName = defaultVariableName;
    }
    
    /**
     * Creates an instance of this class.
     * If you add an act to this object without explicitly specifying variable name with which the act interacts,
     * a `NullPointerException` will be thrown.
     */
    public Builder() {
      this(DEFAULT_DEFAULT_VARIABLE_NAME);
    }
    
    public Builder name(String name) {
      this.name = requireNonNull(name);
      return this;
    }
    
    /**
     * A "syntax-sugar" method to group a sequence of method calls to this `Builder` object.
     *
     * That is, you can do:
     *
     * ```java
     * new SceneBuilder.with(b -> b.add(...)
     * .add(...)
     * .add(...))
     * .build();
     * ```
     *
     * Note that the operator `op` is supposed to return `this` object.
     *
     * @param op A unary operator that groups operator on this object.
     * @return This object returned by `op`.
     */
    public final Builder with(UnaryOperator<Builder> op) {
      return op.apply(this);
    }
    
    /**
     * Adds an `Act` to this builder.
     * `defaultFieldName` is used for both input and output.
     * Note that in case `T` and `R` are different, the field will have a different type after `leafAct` execution from the value before it is executed.
     *
     * @param act An act object to be added to this builder.
     * @param <T> Type of input parameter field.
     * @param <R> Type of output parameter field.
     * @return This object.
     */
    public final <T, R> Builder add(Act<T, R> act) {
      return this.add(defaultVariableName(), act, defaultVariableName());
    }
    
    public final <T, R> Builder add(String outputVariableName, Act<T, R> act) {
      return this.add(outputVariableName, act, defaultVariableName());
    }
    
    public final <T, R> Builder add(String outputVariableName, Act<T, R> act, String inputVariableName) {
      return this.addCall(actCall(outputVariableName, act, inputVariableName));
    }
    
    @SuppressWarnings("unchecked")
    public final <R> Builder assertion(Function<R, Statement<R>> assertion) {
      return this.assertions(assertion);
    }
    
    @SuppressWarnings("unchecked")
    public final <R> Builder assertions(Function<R, Statement<R>>... assertions) {
      return this.assertions(defaultVariableName(), assertions);
    }
    
    /**
     * Returns an `AssertionAct` object that verifies a variable in a currently ongoing scene call's variable store.
     * The variable in the store is specified by  `inputFieldName`.
     * This method is implemented as:
     *
     * `this.addCall(assertionCall(variableName, new Value<>(), singletonList(assertionAct), variableName))`,
     * where `Value` is a trivial act which just copies its input variable to an output variable.
     *
     * @param <R>          Type of the variable specified by `inputVariableName`.
     * @param variableName A name of an input variable to be verified.
     * @param assertions   An assertion function
     * @return This object
     */
    @SuppressWarnings("unchecked")
    public final <R> Builder assertions(String variableName, Function<R, Statement<R>>... assertions) {
      return this.addCall(assertionCall(variableName, new Value<>(), asList(assertions), variableName));
    }
    
    /**
     * Adds a given `scene` to this builder object.
     * A call to the `scene` will be created (a `SceneCall` object), and it will be a child of the scene that this builder builds.
     *
     * The child call will use the working variable store of the parent scene (i.e., a scene built by this builder) as its input variable store.
     * With this mechanism, the child can reference the values of the
     *
     * @param scene A scene to be added.
     * @return This object,
     */
    public final Builder add(Scene scene) {
      return this.addCall(toSceneCall(scene));
    }
    
    /**
     * Adds a call that retries a given `call`.
     * The call retries given `times` on a failure designated by a class `onException`.
     * An interval between tries will be `interval` seconds.
     *
     * Note that `times` means number of "RE"-tries.
     * If you give 1, it will be retried once after `interval` seconds, if the first try fails.
     *
     * @param call        A call to be retried
     * @param times       Number of retries at maximum.
     * @param onException An exception class on which retries should be attempted.
     * @param interval    Interval between tries.
     * @return A call that retries a given `call`.
     */
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException, int interval) {
      return this.addCall(AutotestSupport.retryCall(call, times, onException, interval));
    }
    
    /**
     * This method is implemented as a shorthand for `this.retry(call, times, onException, 5)`.
     *
     * @param call  A call to be retried
     * @param times How many times `call` should be retried until it succeeds.
     * @return This object
     */
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException) {
      return retry(call, times, onException, 5);
    }
    
    /**
     * This method is implemented as a shorthand for `this.retry(call, times, AssertionFailedError.class)`.
     *
     * @param call  A call to be retried
     * @param times How many times `call` should be retried until it succeeds.
     * @return This object
     */
    public final Builder retry(Call call, int times) {
      return retry(call, times, Throwable.class);
    }
    
    public final Builder retry(Scene scene) {
      return retry(toSceneCall(scene));
    }
    
    /**
     * This method is implemented as a shorthand for `this.retry(call, 2)`.
     *
     * @param call A call object to be added.
     * @return This object.
     */
    public final Builder retry(Call call) {
      return retry(call, 2);
    }
    
    /**
     * Adds a call to this object as a child.
     * You need to ensure that requirements of `call` are satisfied in the context it will be run by yourself.
     *
     * For instance, if the call is a `SceneCall`, the variable store from which it reads needs to be prepared beforehand by one of preceding calls.
     *
     * @param call A `Call` object to be added.
     * @return This object.
     */
    public Builder addCall(Call call) {
      this.children.add(call);
      return this;
    }
    
    /**
     * Builds a `Scene` object.
     *
     * @return A `Scene` object.
     */
    public Scene build() {
      return new Scene() {
        private final List<Call> children = Builder.this.children;
        private final String oid = Builder.this.oid();
        
        @Override
        public List<Call> children() {
          return children;
        }
        
        @Override
        public String oid() {
          return oid;
        }
        
        @Override
        public String toString() {
          return name() + "@" + oid();
        }
        
        @Override
        public String name() {
          return name != null ? name
                              : Scene.super.name();
        }
      };
    }
    
    /**
     * Returns an object identifier of this object.
     *
     * @return An object identifier of this object.
     */
    @Override
    public String oid() {
      return "id-" + System.identityHashCode(this);
    }
    
    private String defaultVariableName() {
      return requireNonNull(this.defaultVariableName);
    }
    
    private SceneCall toSceneCall(Scene scene) {
      return sceneToSceneCall(scene, this.workingVariableStoreName());
    }
  }
}
