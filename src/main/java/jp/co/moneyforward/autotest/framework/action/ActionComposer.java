package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.actions.Composite;
import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.concat;

/**
 * An interface that models a factory of actions.
 *
 * This interface is designed to be a "visitor" of "calls", each of which represents a call of an action (`ActionFactory`).
 *
 * A call is a model of an occurrence of an action, and it has input and output.
 *
 * Calls can be categorized into two.
 * Calls for scenes (`Scene`) and calls for acts (`Act`).
 * Corresponding to the subclasses of `Act`, there are subcategories of it, which are `LeafAct`, `AssertionAct`, and `PipelinedAct`.
 *
 * @see Call
 * @see Scene
 * @see Act
 * @see ActionFactory
 */
public interface ActionComposer {
  Logger LOGGER = LoggerFactory.getLogger(ActionComposer.class);
  
  Optional<SceneCall> currentSceneCall();
  
  ExecutionEnvironment executionEnvironment();
  
  default Action create(SceneCall sceneCall) {
    return sequential(concat(Stream.of(sceneCall.begin()),
                             Stream.of(sequential(sceneCall.scene.children()
                                                                 .stream()
                                                                 .map((Call each) -> each.toAction(this))
                                                                 .flatMap(ActionComposer::flattenIfSequential)
                                                                 .toList())),
                             Stream.of(sceneCall.end()))
                          .toList());
  }
  
  default Action create(AssertionActCall<?, ?> call) {
    return sequential(
        Stream.concat(
                  Stream.of(call.target().toAction(this)),
                  call.assertionAsLeafActCalls()
                      .stream()
                      .map(each -> each.toAction(this)))
              .toList());
  }
  
  default <T, R> Action create(Call.PipelinedActCall<T, R> pipelinedActCall) {
    throw new UnsupportedOperationException();
  }
  
  default Action create(LeafActCall<?, ?> actCall) {
    SceneCall currentSceneCall = this.currentSceneCall().orElseThrow();
    
    return InternalUtils.action(actCall.act().name() + "[" + actCall.inputFieldName() + "]",
                                toContextConsumerFromAct(currentSceneCall,
                                                         actCall,
                                                         this.executionEnvironment()));
  }
  
  private static <T, R> Consumer<Context> toContextConsumerFromAct(SceneCall currentSceneCall,
                                                                   LeafActCall<T, R> actCall,
                                                                   ExecutionEnvironment executionEnvironment) {
    return toContextConsumerFromAct(c -> actCall.inputFieldValue(currentSceneCall, c),
                                    actCall.act(),
                                    actCall.outputFieldName(),
                                    currentSceneCall,
                                    executionEnvironment);
  }
  
  private static <T, R> Consumer<Context> toContextConsumerFromAct(Function<Context, T> inputFieldValueResolver,
                                                                   LeafAct<T, R> act,
                                                                   String outputFieldName,
                                                                   SceneCall currentSceneCall,
                                                                   ExecutionEnvironment executionEnvironment) {
    return c -> {
      LOGGER.info("ENTERING: {}:{}", currentSceneCall.scene.name(), act.name());
      try {
        var v = act.perform(inputFieldValueResolver.apply(c),
                            executionEnvironment);
        currentSceneCall.workArea(c).put(outputFieldName, v);
      } catch (Error | RuntimeException e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        throw e;
      } finally {
        LOGGER.info("LEAVING:  {}:{}", currentSceneCall.scene.name(), act.name());
      }
    };
  }
  
  static ActionComposer createActionComposer(final ExecutionEnvironment executionEnvironment) {
    return new ActionComposer() {
      SceneCall currentSceneCall = null;
      
      @Override
      public Optional<SceneCall> currentSceneCall() {
        return Optional.ofNullable(currentSceneCall);
      }
      
      @Override
      public ExecutionEnvironment executionEnvironment() {
        return executionEnvironment;
      }
      
      @Override
      public Action create(SceneCall sceneCall) {
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
  
  private static Stream<Action> flattenIfSequential(Action a) {
    return a instanceof Composite && !((Composite) a).isParallel() ? ((Composite) a).children().stream()
                                                                   : Stream.of(a);
  }
}
