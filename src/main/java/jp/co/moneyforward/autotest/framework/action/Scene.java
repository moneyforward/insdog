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
    return actionComposer.create(this,
                                 inputFieldName,
                                 outputFieldName);
  }
  
  List<ParameterAssignment> parameterAssignments();
  
  String inputFieldName();
  
  String outputFieldName();
  
  List<ActionFactoryHolder<?, Object, Object>> children();
  
  class Builder {
    private String outputConnectorFieldName;
    private String inputConnectorFieldName;
    
    private final List<Scene.ParameterAssignment> inputFieldNames = new LinkedList<>();
    private final List<ActionFactoryHolder<?, Object, Object>> main;
    private final String sceneName;
    
    public Builder() {
      this(Scene.class.getSimpleName());
    }
    
    public Builder(String sceneName) {
      this.main = new LinkedList<>();
      this.sceneName = sceneName;
      this.inputConnectorFieldName(IMPLICIT_VARIABLE_NAME).outputConnectorFieldName(IMPLICIT_VARIABLE_NAME);
    }
    
    public Builder inputConnectorFieldName(String inputFieldName) {
      this.inputConnectorFieldName = inputFieldName;
      return this;
    }
    
    public Builder outputConnectorFieldName(String outputFieldName) {
      this.outputConnectorFieldName = outputFieldName;
      return this;
    }
    
    
    @SuppressWarnings("unchecked")
    public final <T, R> Builder add(String outputFieldName, ActionFactory<T, R> action, String inputFieldName) {
      this.main.add(ActionFactoryHolder.create(inputFieldName, outputFieldName, (ActionFactory<Object, Object>) action));
      return this;
    }
    
    public final <T, R> Builder add(ActionFactory<T, R> action, String inputFieldName) {
      return this.add(this.outputConnectorFieldName, action, inputFieldName);
    }
    
    public final <T, R> Builder add(String outputFieldName, ActionFactory<T, R> action) {
      return this.add(outputFieldName, action, this.inputConnectorFieldName);
    }
    
    public final <T, R> Builder add(ActionFactory<T, R> action) {
      return this.add(this.outputConnectorFieldName, action, this.inputConnectorFieldName);
    }
    
    public Builder assign(String inputFieldName) {
      return this.assign(inputFieldName, inputFieldName);
    }
    
    public Builder assign(String parameterName, String fromVariable) {
      this.inputFieldNames.add(new Scene.ParameterAssignment(parameterName, fromVariable));
      return this;
    }
    
    public Scene build() {
      return new Scene() {
        @Override
        public List<ParameterAssignment> parameterAssignments() {
          return Builder.this.inputFieldNames;
        }
        
        @Override
        public String inputFieldName() {
          return "in:" + sceneName;
        }
        
        @Override
        public String outputFieldName() {
          return "out:" + sceneName;
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
  
  record ParameterAssignment(String formalName, String actualName) {
  }
}
