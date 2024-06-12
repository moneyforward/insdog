package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.facade.Resolver;

import java.util.*;
import java.util.function.Function;

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
    private final List<Call> children = new LinkedList<>();
    
    public Builder() {
    }
    
    
    public final <T, R> Builder add(String outputFieldName, LeafAct<T, R> leafAct) {
      return this.addCall(leafCall(outputFieldName, leafAct));
    }
    
    public final <T, R> Builder add(String outputFieldName, AssertionAct<T, R> leafAct, String inputFieldName) {
      return this.addCall(assertionCall(outputFieldName, leafAct.parent(), leafAct.assertion(), inputFieldName));
    }
    
    public final <T, R> Builder add(String outputFieldName, LeafAct<T, R> leafAct, String inputFieldName) {
      return this.addCall(leafCall(outputFieldName, leafAct, inputFieldName));
    }
    
    public final Builder add(String outputFieldName, Scene scene, Resolver... resolvers) {
      var resolverMap = new HashMap<String, Function<Context, Object>>();
      Arrays.stream(resolvers).forEach(r -> resolverMap.put(r.parameterName(), r.resolverFunction()));
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
