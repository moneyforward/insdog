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
import static java.util.stream.Collectors.toMap;
import static jp.co.moneyforward.autotest.framework.action.Utils.action;

public interface ActionComposer {
  default <T, R> Action create(ActionFactory<T, R> actionFactory, String inputFieldName, String outputFieldName) {
    throw new UnsupportedOperationException(inputFieldName + ":=" + actionFactory + "[" + outputFieldName + "]");
  }
  
  default <T, R> Action create(Act<T, R> act, String inputFieldName, String outputFieldName) {
    return act.toAction(inputProvider(inputFieldName),
                        outputConsumerProvider(outputFieldName),
                        executionEnvironment());
  }
  
  default Action create(Scene scene, String inputFieldName, String outputFieldName) {
    ActionComposer child = createActionComposer(inputFieldName, scene.name(), outputFieldName, executionEnvironment(), parameterMapping(scene.parameterAssignments()));
    return ActionSupport.sequential(
        InternalUtils.concat(
                         Stream.of(beginScene(scene, inputFieldName, outputFieldName)),
                         scene.children()
                              .stream()
                              .map((ActionFactoryHolder<?, ?, ?> each) -> each.get().toAction(child,
                                                                                              each.inputFieldName(),
                                                                                              each.outputFieldName())),
                         Stream.of(endScene(scene, inputFieldName, outputFieldName)))
                     .toList());
  }
  
  private static Action beginScene(Scene scene, String inputFieldName, String outputFieldName) {
    return action(format("%s:=%s:{", outputFieldName, scene.name()), c -> {
      System.err.println("BEGIN:" + c);
      c.assignTo(scene.name(), composeInputValueMap(getInputVariableMapOrCreate(inputFieldName, c), parameterMapping(scene.parameterAssignments())));
    });
  }
  
  private static Action endScene(Scene scene, String inputFieldName, String outputFieldName) {
    return action("}[" + inputFieldName + "]",
                  c -> {
                    c.assignTo(outputFieldName, new HashMap<>(c.valueOf(scene.name())));
                    System.err.println("END:" + c);
                  });
  }
  
  
  private static Map<String, Object> getInputVariableMapOrCreate(String inputFieldName, Context c) {
    return c.defined(inputFieldName) ? c.valueOf(inputFieldName)
                                     : new HashMap<>();
  }
  
  private static Map<String, String> parameterMapping(Collection<Scene.ParameterAssignment> parameterAssignments) {
    Map<String, String> ret = new HashMap<>();
    parameterAssignments.forEach(each -> ret.put(each.formalName(), each.actualName()));
    return ret;
  }
  
  private static Map<String, Object> composeInputValueMap(Map<String, Object> originalInputVariableMap, Map<String, String> parameterAssignments) {
    Map<String, Object> ret = new HashMap<>();
    originalInputVariableMap.forEach((key, value) -> {
      ret.put(parameterAssignments.getOrDefault(key, key), value);
    });
    return ret;
  }
  
  static ActionComposer createActionComposer(String inputFieldName, String workingFieldName, String outputFieldName, final ExecutionEnvironment executionEnvironment, Map<String, String> stringStringMap) {
    return new ActionComposer() {
      
      @SuppressWarnings("unchecked")
      @Override
      public <T> Function<Context, T> inputProvider(String inputFieldName_) {
        return function(inputFieldName + "[" + inputFieldName_ + "]", c -> (T) store(c, workingFieldName).get(stringStringMap.getOrDefault(inputFieldName_, inputFieldName_)));
      }
      
      @Override
      public <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName_) {
        return function(outputFieldName + "[" + outputFieldName_ + "]", c -> r -> store(c, workingFieldName).put(outputFieldName_, r));
      }
      
      @Override
      public ExecutionEnvironment executionEnvironment() {
        return executionEnvironment;
      }
      
      private static Map<String, Object> store(Context c, String fieldName) {
        if (!c.defined(fieldName))
          c.assignTo(fieldName, new HashMap<>());
        return c.valueOf(fieldName);
      }
    };
  }
  
  <T> Function<Context, T> inputProvider(String inputFieldName);
  
  <R> Function<Context, Consumer<R>> outputConsumerProvider(String outputFieldName);
  
  ExecutionEnvironment executionEnvironment();
}
