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
import static java.util.Collections.singletonList;
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
    final String defaultFieldName;
    private final List<Call> children = new LinkedList<>();
    
    /**
     * Creates an instance of this class.
     *
     * @param defaultFieldName A name of field used when use `add` methods without explicit input/output target field names.
     */
    public Builder(String defaultFieldName) {
      this.defaultFieldName = requireNonNull(defaultFieldName);
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
      return this.add(defaultFieldName, act, defaultFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, Act<T, R> act) {
      return this.add(outputFieldName, act, defaultFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, Act<T, R> act, String inputFieldName) {
      return this.addCall(leafCall(outputFieldName, act, inputFieldName));
    }
    
    public final <R> Builder assertion(Function<R, Statement<R>> assertion) {
      return this.assertion(defaultFieldName, assertion, defaultFieldName);
    }
    
    public final <R> Builder assertion(String outputFieldName, Function<R, Statement<R>> assertionAct, String inputFieldName) {
      return this.addCall(assertionCall(outputFieldName, new Value<>(), singletonList(assertionAct), inputFieldName));
    }
    
    public final Builder add(Scene scene) {
      return this.addCall(sceneCall(scene));
    }
    
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException, int interval) {
      return this.addCall(new RetryCall(call, onException, times, interval));
    }
    
    public final Builder retry(Call call, int times, Class<? extends Throwable> onException) {
      return retry(call, times, onException, 5);
    }
    
    public final Builder retry(Call call, int times) {
      return retry(call, times, AssertionFailedError.class);
    }
    
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
          return Scene.super.name() + "[" + defaultFieldName + "]";
        }
      };
    }
  }
}
