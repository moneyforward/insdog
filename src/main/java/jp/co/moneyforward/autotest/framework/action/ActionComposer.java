package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.valid8j.pcond.forms.Printables.function;
import static java.lang.String.format;
import static jp.co.moneyforward.autotest.framework.action.Utils.action;

public interface ActionComposer {
  default <T, R> Action create(ActionFactory<T, R> actionFactory, String inputFieldName, String outputFieldName) {
    throw new UnsupportedOperationException(inputFieldName + ":=" + actionFactory + "[" + outputFieldName + "]");
  }
  
  default <T, R> Action create(Act<T, R> act, String inputFieldName, String outputFieldName) {
    return act.toAction(inputProvider(inputFieldName), outputConsumerProvider(outputFieldName), executionEnvironment());
  }
  
  default Action create(Scene scene, String inputFieldName, String outputFieldName) {
    ActionComposer child = createActionComposer(inputFieldName, outputFieldName, executionEnvironment());
    return ActionSupport.sequential(
        InternalUtils.concat(
            Stream.of(beginScene(scene, inputFieldName, outputFieldName)),
            scene.children()
                 .stream()
                 .map((Scene.ActionFactoryHolder<?, ?, ?> each) -> each.get().toAction(child,
                                                                                       each.inputFieldName(),
                                                                                       each.outputFieldName())),
            Stream.of(endScene(inputFieldName, outputFieldName)))
            .toList());
  }
  
  private static Action beginScene(Scene scene, String inputFieldName, String outputFieldName) {
    return action(format("%s:=%s:{", outputFieldName, scene.name()), c -> {
      System.err.println(c);
      c.assignTo(inputFieldName, composeInputValueMap(getInputVariableMapOrCreate(inputFieldName, c),
                                                      scene.inputFieldNames()));
    });
  }

  private static Action endScene(String inputFieldName, String outputFieldName) {
    return action("}[" + inputFieldName + "]",
                  c -> c.assignTo(outputFieldName, new HashMap<>(c.valueOf(inputFieldName))));
  }
  
  
  private static Map<String, Object> getInputVariableMapOrCreate(String inputFieldName, Context c) {
    return c.defined(inputFieldName) ? c.valueOf(inputFieldName)
                                     : new HashMap<>();
  }
  
  private static HashMap<String, Object> composeInputValueMap(Map<String, Object> parentVariableMap, Collection<String> inputFieldNames) {
    HashMap<String, Object> ret = new HashMap<>();
    inputFieldNames.forEach(key -> ret.put(key, parentVariableMap.get(key)));
    return ret;
  }
  
  static ActionComposer createActionComposer(String inputFieldName, String outputFieldName, final ExecutionEnvironment executionEnvironment) {
    return new ActionComposer() {
      
      @SuppressWarnings("unchecked")
      @Override
      public <T> Function<Context, T> inputProvider(String inputFieldName_) {
        return function(inputFieldName_, c -> (T) store(c, inputFieldName).get(inputFieldName_));
      }
      
      @Override
      public <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName_) {
        return function(outputFieldName_, c -> r -> store(c, inputFieldName).put(outputFieldName_, r));
      }
      
      @Override
      public ExecutionEnvironment executionEnvironment() {
        return executionEnvironment;
      }
      
      private static Map<String, Object> store(Context c, String sceneName) {
        return c.valueOf(sceneName);
      }
    };
  }
  
  <T> Function<Context, T> inputProvider(String inputFieldName);
  
  <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName);
  
  ExecutionEnvironment executionEnvironment();
}
