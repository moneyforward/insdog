package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.actions.web.Value;
import jp.co.moneyforward.autotest.framework.core.Resolver;

import java.util.*;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static jp.co.moneyforward.autotest.framework.facade.AutotestSupport.*;

/**
 * An interface that represents a reusable unit of an action in autotest-ca's programming model.
 * An instance of this object may contain {@link LeafAct} instances.
 *
 * Note that `Scene` uses the same map for both input and output.
 */
public interface Scene extends ActionFactory<Map<String, Object>, Map<String, Object>> {
  List<Call> children();
  
  class Builder {
    final String defaultFieldName;
    private final List<Call> children = new LinkedList<>();
    
    public Builder(String defaultFieldName) {
      this.defaultFieldName = requireNonNull(defaultFieldName);
    }
    
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
    
    public final <T, R> Builder assertion(Function<R, Statement<R>> assertion) {
      return this.assertion(defaultFieldName, assertion, defaultFieldName);
    }
    
    public final <T, R> Builder assertion(Function<R, Statement<R>> assertionAct, String inputFieldName) {
      return this.assertion(defaultFieldName, assertionAct, inputFieldName);
    }
    
    public final <T, R> Builder assertion(String outputFieldName, Function<R, Statement<R>> assertionAct) {
      return this.assertion(outputFieldName, assertionAct, defaultFieldName);
    }
    
    public final <T, R> Builder assertion(String outputFieldName, Function<R, Statement<R>> assertionAct, String inputFieldName) {
      return this.addCall(assertionCall(outputFieldName, new Value<>(), Collections.singletonList(assertionAct), inputFieldName));
    }
    
    public final Builder add(String outputFieldName, Scene scene, Resolver... resolvers) {
      return this.addCall(sceneCall(outputFieldName, scene, Arrays.asList(resolvers)));
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
      };
    }
  }
  
  record ParameterAssignment(String formalName, String sourceSceneName, String fieldNameInSourceScene) {
  }
}
