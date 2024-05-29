package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        
        @Override
        public String toString() {
          return String.format("%s", actionFactory);
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
    
    public Builder parameter(String inputFieldName) {
      this.inputFieldNames.add(inputFieldName);
      return this;
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
        public String name() {
          return Builder.this.sceneName;
        }
        
        @Override
        public String toString() {
          return name() + "@" + System.identityHashCode(this);
        }
      };
    }
  }
}
