package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.actions.Composite;
import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.concat;

public interface ActionComposer {
  Optional<Call.SceneCall> currentSceneCall();
  
  default <A extends ActionFactory<T, R>, T, R> Action create(ActionFactory<T, R> actionFactory, String inputFieldName, String outputFieldName) {
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
    return InternalUtils.action("BEGIN:", c -> {
      System.err.println(c);
      c.assignTo(sceneCall.workAreaName(), sceneCall.initializeWorkArea(c));
    });
  }
  
  private static Action endSceneCall(Call.SceneCall sceneCall) {
    return InternalUtils.action("END:", c -> {
      c.assignTo(sceneCall.outputFieldName(), c.valueOf(sceneCall.workAreaName()));
      c.unassign(sceneCall.workAreaName());
      System.err.println(c);
    });
  }
  
  
  default Action create(Call.AssertionActCall<?, ?> call) {
    return ActionSupport.sequential(
        Stream.concat(
            Stream.of(call.target().toAction(this)),
            call.assertionAsLeafActCalls().stream().map(each -> each.toAction(this))
        ).toList());
  }
  
  default Action create(Call.LeafActCall<?, ?> actCall) {
    Call.SceneCall currentSceneCall = this.currentSceneCall().orElseThrow();
    
    return InternalUtils.action(actCall.act().name() + "[" + actCall.inputFieldName() + "]",
                                toContextConsumerFromAct(currentSceneCall, actCall, this.executionEnvironment()));
  }
  
  private <T, R> Consumer<Context> toContextConsumerFromAct(Call.SceneCall currentSceneCall, Call.LeafActCall<T, R> actCall, ExecutionEnvironment executionEnvironment) {
    return c -> {
      System.out.println(actCall.act().name() + ":" + c);
      var v = actCall.act().perform(actCall.value(currentSceneCall, c),
                                    executionEnvironment);
      currentSceneCall.workArea(c).put(actCall.outputFieldName(), v);
    };
  }
  
  static ActionComposer createActionComposer(final ExecutionEnvironment executionEnvironment) {
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
