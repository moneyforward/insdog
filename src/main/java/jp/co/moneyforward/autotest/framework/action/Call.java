package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public interface Call {
  String outputFieldName();
  
  Action toAction(ActionComposer actionComposer);
  
  class SceneCall implements Call {
    final Scene scene;
    final Map<String, Function<Context, Object>> assignmentResolvers;
    private final String outputFieldName;
    
    
    public SceneCall(String outputFieldName, Scene scene, Map<String, Function<Context, Object>> assignmentResolvers) {
      this.outputFieldName = requireNonNull(outputFieldName);
      this.scene = requireNonNull(scene);
      this.assignmentResolvers = requireNonNull(assignmentResolvers);
    }
    
    String workAreaName() {
      return "work-" + objectId();
    }
    
    Map<String, Object> initializeWorkArea(Context context) {
      var ret = new HashMap<String, Object>();
      assignmentResolvers.forEach((k, r) -> ret.put(k, r.apply(context)));
      return ret;
    }
    
    Map<String, Object> workArea(Context context) {
      return context.valueOf(workAreaName());
    }
    
    String objectId() {
      return scene.name() + ":" + System.identityHashCode(scene);
    }
    
    @Override
    public String outputFieldName() {
      return this.outputFieldName;
    }
    
    @Override
    public Action toAction(ActionComposer actionComposer) {
      return actionComposer.create(this);
    }
  }
  
  abstract class ActCall<T, R> implements Call {
    private final String inputFieldName;
    private final String outputFieldName;
    
    protected ActCall(String inputFieldName, String outputFieldName) {
      this.inputFieldName = requireNonNull(inputFieldName);
      this.outputFieldName = requireNonNull(outputFieldName);
    }
    
    String inputFieldName() {
      return this.inputFieldName;
    }
    
    @Override
    public String outputFieldName() {
      return this.outputFieldName;
    }
  }
  
  class AssertionActCall<T, R> extends ActCall<T, R> {
    private final List<Function<R, Statement<R>>> assertions;
    private final ActCall<T, R> target;
    
    public AssertionActCall(ActCall<T, R> target, List<Function<R, Statement<R>>> assertion) {
      super(target.inputFieldName, target.outputFieldName());
      this.target = target;
      this.assertions = requireNonNull(assertion);
    }
    
    List<LeafActCall<R, R>> assertionAsLeafActCalls() {
      return assertions.stream()
                       .map(assertion -> new LeafActCall<>(outputFieldName(), assertionAsLeafAct(assertion), outputFieldName()))
                       .toList();
    }
    
    private LeafAct<R, R> assertionAsLeafAct(Function<R, Statement<R>> assertion) {
      return new LeafAct<R, R>() {
        @Override
        public R perform(R value, ExecutionEnvironment executionEnvironment) {
          Expectations.assertStatement(assertion.apply(value));
          return value;
        }
        
        @Override
        public String name() {
          // This is a hack to compose a human-readable string.
          return "assertion:" + assertion.apply(null).statementPredicate();
        }
      };
    }
    
    ActCall<T, R> target() {
      return this.target;
    }
    
    @Override
    public Action toAction(ActionComposer actionComposer) {
      return actionComposer.create(this);
    }
  }
  
  class LeafActCall<T, R> extends ActCall<T, R> {
    
    private final LeafAct<T, R> act;
    
    public LeafActCall(String outputFieldName, LeafAct<T, R> act, String inputFieldName) {
      super(inputFieldName, outputFieldName);
      this.act = act;
    }
    
    public LeafAct<T, R> act() {
      return this.act;
    }
    
    
    @SuppressWarnings("unchecked")
    T value(SceneCall sceneCall, Context context) {
      return (T) sceneCall.workArea(context).get(inputFieldName());
    }
    
    @Override
    public Action toAction(ActionComposer actionComposer) {
      return actionComposer.create(this);
    }
  }
}
