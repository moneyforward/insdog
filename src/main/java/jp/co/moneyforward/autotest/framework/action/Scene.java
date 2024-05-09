package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.action.ActionFactory.Io;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.*;
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
 */
public interface Scene extends ActionFactory<Map<String, Io<Object, Object>>, Map<String, Io<Object, Object>>> {
  
  List<ActionFactory<Object, Object>> main();
  
  default Action toAction(Function<Context, Io<Map<String, Io<Object, Object>>, Map<String, Io<Object, Object>>>> ioProvider, ExecutionEnvironment executionEnvironment) {
    return sequential(main().stream()
                            .map(each -> each.toAction(c -> ioProvider.apply(c)
                                                                      .input()
                                                                      .get(each.name()),
                                                       executionEnvironment))
                            .toList());
  }
  
  class Builder {
    private final List<ActionFactory<Object, Object>> main;
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
    public final <T, R> Builder add(String fieldName,
                                    ActionFactory<T, R> action,
                                    Function<R, Statement<R>> assertion) {
      requireState(value(this).function(knownFieldNames())
                              .asList()
                              .satisfies()
                              .predicate(not(contains(fieldName))));
      this.knownFieldNames.add(fieldName);
      this.main.add((ActionFactory<Object, Object>) action.assertion(fieldName, assertion));
      return this;
    }
    
    private static Function<Builder, List<String>> knownFieldNames() {
      return function("knownFieldNames", x -> x.knownFieldNames.stream().toList());
    }
    
    public Scene build() {
      return new Scene() {
        @Override
        public List<ActionFactory<Object, Object>> main() {
          return Builder.this.main;
        }
        
        @Override
        public String name() {
          return sceneName;
        }
      };
    }
  }
}
