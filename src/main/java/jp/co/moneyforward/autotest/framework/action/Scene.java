package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.actions.web.Value;

import java.util.*;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.*;

/**
 * An interface that represents a reusable unit of an action in autotest-ca's programming model.
 * An instance of this object contains 0 or more {@link Act} instances.
 *
 * Note that `Scene` uses the same map for both input and output.
 */
public interface Scene extends ActionFactory {
  /**
   * Returns members of this scene object, which are executed as "children".
   *
   * @return members of this scene object.
   */
  List<Call> children();
  
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
    
    /**
     * Adds `leafAct` to this builder.
     * `defaultFieldName` is used for both input and output.
     * Note that in case `T` and `R` are different, the field will have a different type after `leafAct` execution from the value before it is executed.
     *
     * @param leafAct An act object to be added to this builder.
     * @param <T>     Type of input parameter field.
     * @param <R>     Type of output parameter field.
     * @return This object.
     */
    public final <T, R> Builder add(LeafAct<T, R> leafAct) {
      return this.add(defaultFieldName, leafAct, defaultFieldName);
    }
    
    public final <T, R> Builder add(LeafAct<T, R> leafAct, String inputFieldName) {
      return this.add(defaultFieldName, leafAct, inputFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, LeafAct<T, R> leafAct) {
      return this.add(outputFieldName, leafAct, defaultFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, LeafAct<T, R> leafAct, String inputFieldName) {
      return this.addCall(leafCall(outputFieldName, leafAct, inputFieldName));
    }
    
    public final <T, R> Builder add(AssertionAct<T, R> assertionAct) {
      return this.add(defaultFieldName, assertionAct, defaultFieldName);
    }
    
    public final <T, R> Builder add(AssertionAct<T, R> assertionAct, String inputFieldName) {
      return this.add(defaultFieldName, assertionAct, inputFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, AssertionAct<T, R> assertionAct) {
      return this.add(outputFieldName, assertionAct, defaultFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, AssertionAct<T, R> assertionAct, String inputFieldName) {
      return this.addCall(assertionCall(outputFieldName, assertionAct.parent(), assertionAct.assertions(), inputFieldName));
    }
    
    public final <R> Builder assertion(Function<R, Statement<R>> assertion) {
      return this.assertion(defaultFieldName, assertion, defaultFieldName);
    }
    
    public final <R> Builder assertion(Function<R, Statement<R>> assertionAct, String inputFieldName) {
      return this.assertion(defaultFieldName, assertionAct, inputFieldName);
    }
    
    public final <R> Builder assertion(String outputFieldName, Function<R, Statement<R>> assertionAct) {
      return this.assertion(outputFieldName, assertionAct, defaultFieldName);
    }
    
    public final <R> Builder assertion(String outputFieldName, Function<R, Statement<R>> assertionAct, String inputFieldName) {
      return this.addCall(assertionCall(outputFieldName, new Value<>(), Collections.singletonList(assertionAct), inputFieldName));
    }
    
    public final Builder add(Scene scene) {
      return this.addCall(sceneCall(scene));
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
          return "Scene[" + defaultFieldName + "]";
        }
      };
    }
  }
  
  record ParameterAssignment(String formalName, String sourceSceneName, String fieldNameInSourceScene) {
  }
}
