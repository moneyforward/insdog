package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.actions.web.Value;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.util.Arrays.asList;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.*;

/**
 * An interface that represents a reusable unit of an action in autotest-ca's programming model.
 * An instance of this object contains 0 or more {@link Act} instances.
 *
 * Note that `Scene` uses the same map for both input and output.
 */
public interface Scene {
  /**
   * Creates a scene by chaining acts.
   *
   * @param variableName An variable chained acts read input value from and write output value to.
   * @param acts         Acts from which a scene is created.
   * @return Created scene.
   */
  static Scene fromActs(String variableName, Act<?, ?>... acts) {
    Scene.Builder b = new Builder(variableName);
    for (Act<?, ?> act : acts) {
      b.add(act);
    }
    return b.build();
  }
  
  /**
   * Creates a sequential action from the child calls of this object
   *
   * @param resolverBundle A resolver bundle.
   * @param actionComposer A visitor that builds a sequential action from child calls of this object.
   * @return A sequential action created from child calls
   * @see Scene#children()
   */
  default Action toSequentialAction(ResolverBundle resolverBundle, ActionComposer actionComposer) {
    return sequential(toActions(resolverBundle, actionComposer));
  }
  
  /**
   * Returns members of this scene object, which are executed as "children".
   *
   * @return members of this scene object.
   */
  List<Call> children();
  
  /**
   * Returns a name of this object.
   * The returned string will appear in an action tree printed during the action execution.
   *
   * @return A name of this object.
   */
  default String name() {
    return InternalUtils.simpleClassNameOf(this.getClass());
  }
  
  /**
   * Creates a `ResolverBundle` which figures out values of variables in a variable store specified by `variableStoreName`.
   * Variables that become resolvable by the returned `ResolverBundle` are given by `outputVariableNames`.
   *
   * @param variableStoreName A variable store name for which a `ResolverBundle` is created.
   * @return A resolver bundle object.
   * @see ResolverBundle
   * @see Scene#outputVariableNames()
   */
  default ResolverBundle resolverBundleFor(String variableStoreName) {
    return new ResolverBundle(resolversFor(variableStoreName));
  }
  
  default List<Resolver> resolversFor(String variableStoreName) {
    return Resolver.resolversFor(variableStoreName, this.outputVariableNames());
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
               .toList();
  }
  
  private static Stream<String> outputVariableNamesOf(Call c) {
    if (c instanceof SceneCall sceneCall) {
      return sceneCall.targetScene().outputVariableNames().stream();
    } else if (c instanceof ActCall<?, ?> actCall) {
      return Stream.of(actCall.outputVariableName());
    } else if (c instanceof CallDecorator<?>) {
      return outputVariableNamesOf(((CallDecorator<?>) c).targetCall());
    }
    throw new AssertionError();
  }
  
  private List<Action> toActions(ResolverBundle resolverBundle, ActionComposer actionComposer) {
    return children().stream()
                     .map((Call each) -> each.toAction(actionComposer, resolverBundle))
                     .flatMap(InternalUtils::flattenIfSequential)
                     .toList();
  }
  
  /**
   * A builder for `Scene` class.
   *
   * @see Scene
   */
  class Builder {
    final String defaultVariableName;
    private final List<Call> children = new LinkedList<>();
    
    /**
     * Creates an instance of this class.
     *
     * @param defaultVariableName A name of field used when use `add` methods without explicit input/output target field names.
     */
    public Builder(String defaultVariableName) {
      this.defaultVariableName = requireNonNull(defaultVariableName);
    }
    
    public final Builder with(UnaryOperator<Builder> op) {
      return op.apply(this);
    }
    
    /**
     * Adds `leafAct` to this builder.
     * `defaultFieldName` is used for both input and output.
     * Note that in case `T` and `R` are different, the field will have a different type after `leafAct` execution from the value before it is executed.
     *
     * @param act An act object to be added to this builder.
     * @param <T> Type of input parameter field.
     * @param <R> Type of output parameter field.
     * @return This object.
     */
    public final <T, R> Builder add(Act<T, R> act) {
      return this.add(defaultVariableName, act, defaultVariableName);
    }
    
    public final <T, R> Builder add(String outputFieldName, Act<T, R> act) {
      return this.add(outputFieldName, act, defaultVariableName);
    }
    
    public final <T, R> Builder add(String outputFieldName, Act<T, R> act, String inputFieldName) {
      return this.addCall(actCall(outputFieldName, act, inputFieldName));
    }
    
    @SuppressWarnings("unchecked")
    public final <R> Builder assertion(Function<R, Statement<R>> assertion) {
      return this.assertions(defaultVariableName, assertion);
    }
    
    @SuppressWarnings("unchecked")
    public final <R> Builder assertions(Function<R, Statement<R>>... assertions) {
      return this.assertions(defaultVariableName, assertions);
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
    
    public final Builder add(Scene scene) {
      return this.addCall(sceneCall(this.defaultVariableName, scene, SceneCall.workingVariableStoreNameFor(scene)));
    }
    
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException, int interval) {
      return this.addCall(new RetryCall(call, onException, times, interval));
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
      return retry(call, times, AssertionFailedError.class);
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
    
    public Builder addCall(Call call) {
      this.children.add(call);
      return this;
    }
    
    public Scene build() {
      return new Scene() {
        @Override
        public List<Call> children() {
          return Builder.this.children;
        }
        
        @Override
        public String toString() {
          return name() + "@" + System.identityHashCode(this);
        }
        
        @Override
        public String name() {
          return Scene.super.name() + "[" + defaultVariableName + "]";
        }
      };
    }
  }
}
