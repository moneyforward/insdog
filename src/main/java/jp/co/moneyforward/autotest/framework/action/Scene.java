package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
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
  
  default Action toAction(Function<Context, Map<String, Object>> inputProvider, Function<Context, Consumer<Map<String, Object>>> outputConsumerProvider, ExecutionEnvironment executionEnvironment) {
    return sequential(children().stream()
                                .map(ActionFactoryHolder::get)
                                .map(each -> {
                              AtomicReference<Map<String, Object>> inputHolder = new AtomicReference<>();
                              return each.toAction(c -> {
                                                     Map<String, Object> input = inputProvider.apply(c);
                                                     inputHolder.set(input);
                                                     Optional<String> name = each.name();
                                                     return input.containsKey(name)
                                                            ? input.get(name) :
                                                            Void.class;
                                                   },
                                                   c -> o -> outputConsumerProvider.apply(c).accept(inputHolder.get()),
                                                   executionEnvironment);
                            })
                                .toList());
  }
  
  static <T, R> Act<T, R> asAct(Function<T, R> func) {
    return asAct((v, e) -> func.apply(v));
  }
  
  static <T, R> Act<T, R> asAct(BiFunction<T, ExecutionEnvironment, R> func) {
    return new Act<T, R>() {
      @Override
      public R perform(T value, ExecutionEnvironment executionEnvironment) {
        return func.apply(value, executionEnvironment);
      }
    };
  }
  
  class Builder {
    private final List<ActionFactoryHolder<?, Object, Object>> main;
    private final String sceneName;
    private final Set<String> knownFieldNames;
    
    public Builder() {
      this(Scene.class.getSimpleName() + ":" + nanoTime());
    }
    
    public Builder(String sceneName) {
      this.main = new LinkedList<>();
      this.sceneName = sceneName;
      this.knownFieldNames = new HashSet<>();
    }
    
    @SuppressWarnings("unchecked")
    public final <T, R> Builder add(String outputFieldName, ActionFactory<T, R> action, String inputFieldName) {
      requireState(value(this).function(knownFieldNames())
                              .asList()
                              .satisfies()
                              .predicate(not(contains(inputFieldName))));
      this.knownFieldNames.add(inputFieldName);
      this.knownFieldNames.add(outputFieldName);
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
    
    
    private static Function<Builder, List<String>> knownFieldNames() {
      return function("knownFieldNames", x -> x.knownFieldNames.stream().toList());
    }
    
    public Scene build() {
      return new Scene() {
        @Override
        public Map<String, Object> perform(Map<String, Object> input, ExecutionEnvironment executionEnvironment) {
          return null;
        }
        
        @Override
        public List<ActionFactoryHolder<?, Object, Object>> children() {
          return null;
        }
      };
    }
  }
}
