package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static com.github.valid8j.fluent.Expectations.requireState;
import static com.github.valid8j.fluent.Expectations.value;
import static com.github.valid8j.pcond.forms.Predicates.contains;
import static com.github.valid8j.pcond.forms.Predicates.not;
import static com.github.valid8j.pcond.forms.Printables.function;
import static java.lang.System.nanoTime;

/**
 * An interface that represents a reusable unit of an action in autotest-ca's programming model.
 * An instance of this object may contain {@link Act} instances.
 *
 * Note that `Scene` uses the same map for both input and output.
 */
public interface Scene extends ActionFactory<Map<String, Object>, Map<String, Object>> {
  String IMPLICIT_VARIABLE_NAME = "_";
  
  @Override
  default Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return actionComposer.create(this, inputFieldName, outputFieldName);
  }
  
  List<String> inputFieldNames();
  
  interface ActionFactoryHolder<A extends ActionFactory<T, R>, T, R> {
    A get();
    
    String inputFieldName();
    
    String outputFieldName();
    
    static <A extends ActionFactory<T, R>, T, R> ActionFactoryHolder<A, T, R> create(String inputFieldName, String outputFieldName, A actionFactory) {
      return new ActionFactoryHolder<>() {
        @Override
        public A get() {
          return actionFactory;
        }
        
        @Override
        public String inputFieldName() {
          return inputFieldName;
        }
        
        @Override
        public String outputFieldName() {
          return outputFieldName;
        }
      };
    }
  }
  
  List<ActionFactoryHolder<?, Object, Object>> children();
  
  class Builder {
    private final List<String> inputFieldNames = new LinkedList<>();
    private final List<ActionFactoryHolder<?, Object, Object>> main;
    private final String sceneName;
    
    public Builder() {
      this(Scene.class.getSimpleName());
    }
    
    public Builder(String sceneName) {
      this.main = new LinkedList<>();
      this.sceneName = sceneName;
    }
    
    @SuppressWarnings("unchecked")
    public final <T, R> Builder add(String outputFieldName, ActionFactory<T, R> action, String inputFieldName) {
      this.main.add(ActionFactoryHolder.create(inputFieldName, outputFieldName, (ActionFactory<Object, Object>) action));
      return this;
    }
    
    public final <T, R> Builder add(ActionFactory<T, R> action, String inputFieldName) {
      return this.add(IMPLICIT_VARIABLE_NAME, action, inputFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, ActionFactory<T, R> action) {
      return this.add(outputFieldName, action, IMPLICIT_VARIABLE_NAME);
    }
    
    public final <T, R> Builder add(ActionFactory<T, R> action) {
      return this.add(IMPLICIT_VARIABLE_NAME, action, IMPLICIT_VARIABLE_NAME);
    }
    
    public Scene build() {
      return new Scene() {
        @Override
        public List<String> inputFieldNames() {
          return Builder.this.inputFieldNames;
        }
        
        @Override
        public List<ActionFactoryHolder<?, Object, Object>> children() {
          return Builder.this.main;
        }
        
        @Override
        public String toString() {
          return Builder.this.sceneName + "@" + System.identityHashCode(this);
        }
      };
    }
  }
}
