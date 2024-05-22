package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.*;

public interface ActionComposer {
  default <T, R> Action create(ActionFactory<T, R> actionFactory, String inputFieldName, String outputFieldName) {
    System.out.println(actionFactory + ":" + (actionFactory instanceof Scene));
    throw new UnsupportedOperationException();
  }
  
  default <T, R> Action create(Act<T, R> act, String inputFieldName, String outputFieldName) {
    return act.toAction(inputProvider(inputFieldName), outputConsumerProvider(outputFieldName), executionEnvironment());
  }
  
  default Action create(Scene scene, String inputFieldName, String outputFieldName) {
    ActionComposer child = createActionComposer(inputFieldName, executionEnvironment());
    return ActionSupport.sequential(
        leaf(c -> c.assignTo(inputFieldName, composeInputValueMap(composeParentVariableMap(inputFieldName, c), scene.inputFieldNames()))),
        leaf(c -> c.assignTo(outputFieldName, new HashMap<>())),
        attempt(sequential(scene.children()
                                .stream()
                                .map((Scene.ActionFactoryHolder<?, ?, ?> each) -> each.get().toAction(child,
                                                                                                      each.inputFieldName(),
                                                                                                      each.outputFieldName()))
                                .toList()))
            .ensure(leaf(c -> c.unassign(inputFieldName))));
  }
  
  private static Map<String, Object> composeParentVariableMap(String inputFieldName, Context c) {
    return c.defined(inputFieldName) ? c.valueOf(inputFieldName)
                                     : new HashMap<>();
  }
  
  private static HashMap<String, Object> composeInputValueMap(Map<String, Object> parentVariableMap, Collection<String> inputFieldNames) {
    HashMap<String, Object> ret = new HashMap<>();
    inputFieldNames.forEach(key -> ret.put(key, parentVariableMap.get(key)));
    return ret;
  }
  
  static ActionComposer createActionComposer(String sceneName, final ExecutionEnvironment executionEnvironment) {
    return new ActionComposer() {
      
      @SuppressWarnings("unchecked")
      @Override
      public <T> Function<Context, T> inputProvider(String inputFieldName) {
        return c -> (T) c.<Map<String, Object>>valueOf(sceneName).get(inputFieldName);
      }
      
      @Override
      public <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName) {
        return c -> r -> c.<Map<String, Object>>valueOf(sceneName).put(outputFieldName, r);
      }
      
      @Override
      public ExecutionEnvironment executionEnvironment() {
        return executionEnvironment;
      }
    };
  }
  
  <T> Function<Context, T> inputProvider(String inputFieldName);
  
  <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName);
  
  ExecutionEnvironment executionEnvironment();
  
  
}
