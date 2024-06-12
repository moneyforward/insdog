package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.actions.Composite;
import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static com.github.valid8j.pcond.forms.Printables.function;
import static java.lang.String.format;
import static jp.co.moneyforward.autotest.framework.action.Utils.action;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.concat;

public interface ActionComposer {
  Optional<Call.SceneCall> currentSceneCall();
  
  default <T, R> Action create(ActionFactory<T, R> actionFactory, String inputFieldName, String outputFieldName) {
    throw new UnsupportedOperationException(inputFieldName + ":=" + actionFactory + "[" + outputFieldName + "]");
  }
  
  default Action create(Call call) {
    throw new UnsupportedOperationException("Not supported:" + call);
  }
  
  default Action create(Call.SceneCall sceneCall) {
    return sequential(concat(Stream.of(beginSceneCall(sceneCall)),
                             Stream.of(ActionSupport.sequential(sceneCall.scene.children()
                                                                               .stream()
                                                                               .map((Call each) -> each.toAction(this))
                                                                               .flatMap(ActionComposer::flattenIfSequential)
                                                                               .toList())),
                             Stream.of(endSceneCall(sceneCall)))
                          .toList());
  }
  
  private static Stream<Action> flattenIfSequential(Action a) {
    return a instanceof Composite && !((Composite) a).isParallel() ? ((Composite) a).children().stream()
                                                                   : Stream.of(a);
  }
  
  private static Action beginSceneCall(Call.SceneCall sceneCall) {
    return Utils.action("BEGIN:", c -> {
      System.err.println(c);
      c.assignTo(sceneCall.workAreaName(), sceneCall.initializeWorkArea(c));
    });
  }
  
  private static Action endSceneCall(Call.SceneCall sceneCall) {
    return Utils.action("END:", c -> {
      c.assignTo(sceneCall.outputFieldName(), c.valueOf(sceneCall.workAreaName()));
      c.unassign(sceneCall.workAreaName());
      System.err.println(c);
    });
  }
  
  
  default Action create(Call.AssertionActCall<?, ?> call) {
    return ActionSupport.sequential(
        call.target().toAction(this),
        call.assertion().toAction(this)
    );
  }
  
  default Action create(Call.LeafActCall<?, ?> actCall) {
    Call.SceneCall currentSceneCall = this.currentSceneCall().orElseThrow();
    
    return Utils.action(actCall.act().name() + "[" + actCall.inputFieldName() + "]",
                        toContextConsumerFromAct(this, currentSceneCall, actCall));
  }
  
  private <T, R> Consumer<Context> toContextConsumerFromAct(ActionComposer actionComposer, Call.SceneCall currentSceneCall, Call.LeafActCall<T, R> actCall) {
    return c -> {
      System.out.println(actCall.act().name() + ":" + c);
      var v = actCall.act().perform(actCall.value(currentSceneCall, c),
                                    actionComposer.executionEnvironment());
      currentSceneCall.workArea(c).put(actCall.outputFieldName(), v);
    };
  }
  
  static ActionComposer createActionComposer(String inputFieldName, String workingFieldName, String outputFieldName, final ExecutionEnvironment executionEnvironment, Map<String, String> stringStringMap) {
    return new ActionComposer() {
      Call.SceneCall currentSceneCall = null;
      
      @Override
      public Optional<Call.SceneCall> currentSceneCall() {
        return Optional.ofNullable(currentSceneCall);
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
      
      @Override
      public Action create(Call.SceneCall sceneCall) {
        var before = currentSceneCall;
        try {
          currentSceneCall = sceneCall;
          return ActionComposer.super.create(sceneCall);
        } finally {
          currentSceneCall = before;
        }
      }
    };
  }
  
  ExecutionEnvironment executionEnvironment();
}
