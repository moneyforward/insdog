package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.actions.web.Value;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
  static Scene chainActs(String fieldName, Act<?, ?>... acts) {
    Scene.Builder b = new Builder(fieldName);
    for (Act<?, ?> act : acts) {
      b.add(act);
    }
    return b.build();
  }
  
  default Action toSequentialAction(Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall, ActionComposer actionComposer) {
    return sequential(toActions(assignmentResolversFromCurrentCall, actionComposer));
  }
  
  /**
   * Returns members of this scene object, which are executed as "children".
   *
   * @return members of this scene object.
   */
  List<Call> children();

  default SceneCall chain(Scene scene) {
    return new SceneCall("xyz", this, null);
  }
  
  default String name() {
    return InternalUtils.simpleClassNameOf(this.getClass());
  }
  
  private List<Action> toActions(Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall, ActionComposer actionComposer) {
    return children().stream()
                     .map((Call each) -> each.toAction(actionComposer, assignmentResolversFromCurrentCall))
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
     * @param <T>     Type of input parameter field.
     * @param <R>     Type of output parameter field.
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
     * @param assertions    An assertion function
     * @return This object
     */
    @SuppressWarnings("unchecked")
    public final <R> Builder assertions(String variableName, Function<R, Statement<R>>... assertions) {
      return this.addCall(assertionCall(variableName, new Value<>(), asList(assertions), variableName));
    }
    
    public final Builder add(Scene scene) {
      return this.addCall(sceneCall(scene));
    }
    
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException, int interval) {
      return this.addCall(new RetryCall(call, onException, times, interval));
    }
    
    /**
     * This method is implemented as a shorthand for `this.retry(call, times, onException, 5)`.
     
     * @param call A call to be retried
     * @param times How many times `call` should be retried until it succeeds.
     * @return This object
     */
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException) {
      return retry(call, times, onException, 5);
    }
    
    /**
     * This method is implemented as a shorthand for `this.retry(call, times, AssertionFailedError.class)`.
     
     * @param call A call to be retried
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
