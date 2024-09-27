package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.retry;
import static com.github.dakusui.actionunit.core.ActionSupport.sequential;
import static com.github.dakusui.valid8j.Requires.requireNonNull;
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
 * In this interface, there are `create(XyzCall xyzCall, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall)` methods defined.
 *
 * `assignmentResolversFromCurrentCall` is a map from a variable name to a function which resolves its value from the
 * ongoing context object.
 * By relying on this object for resolving variable values referenced inside `Act` objects (, which are held by `Calls`), we can define `Act` objects
 * work in different variable spaces without changing code (transparent to variable space name, which is determined by a call's object name).
 *
 * @see Call
 * @see Scene
 * @see Act
 */
public interface ActionComposer {
  /**
   * A logger object
   */
  Logger LOGGER = LoggerFactory.getLogger(ActionComposer.class);
  
  /**
   * Returns currently ongoing `SceneCall` object.
   *
   * @return Currently ongoing `SceneCall` object.
   */
  SceneCall ongoingSceneCall();
  
  /**
   * Returns an execution environment in which actions created by this composer objects are performed.
   *
   * @return An execution environment.
   */
  ExecutionEnvironment executionEnvironment();
  
  /**
   * Creates an action for a given `SceneCall` object.
   *
   * @param sceneCall   A scene call from which an action should be created.
   * @param resolverMap A map from a variable name to a function which resolves its value from the
   *                    ongoing context object
   * @return A sequential action created from `sceneCall`.
   */
  default Action create(SceneCall sceneCall, SceneCall.ResolverBundle resolverMap) {
    return sequential(concat(Stream.of(sceneCall.begin(resolverMap)),
                             Stream.of(sceneCall.targetScene().toSequentialAction(resolverMap, this)),
                             Stream.of(sceneCall.end()))
                          .toList());
  }
  
  default Action create(RetryCall retryCall, SceneCall.ResolverBundle resolverMap) {
    return retry(retryCall.targetCall().toAction(this, resolverMap))
        .times(retryCall.times())
        .on(retryCall.onException())
        .withIntervalOf(retryCall.interval(), retryCall.intervalUnit())
        .$();
  }
  
  default Action create(AssertionCall<?> call, SceneCall.ResolverBundle resolverMap) {
    return sequential(
        Stream.concat(
                  Stream.of(call.targetCall().toAction(this, resolverMap)),
                  call.assertionsAsActCalls()
                      .stream()
                      .map(each -> each.toAction(this, resolverMap)))
              .toList());
  }
  
  default Action create(ActCall<?, ?> actCall) {
    SceneCall ongoingSceneCall = ongoingSceneCall();
    
    return InternalUtils.action(actCall.act().name() + "[" + actCall.inputVariableName() + "]",
                                toContextConsumerFromAct(ongoingSceneCall,
                                                         actCall,
                                                         this.executionEnvironment()));
  }
  
  private static <T, R> Consumer<Context> toContextConsumerFromAct(SceneCall ongoingSceneCall,
                                                                   ActCall<T, R> actCall,
                                                                   ExecutionEnvironment executionEnvironment) {
    return toContextConsumerFromAct(c -> actCall.resolveVariable(ongoingSceneCall, c),
                                    actCall.act(),
                                    actCall.outputVariableName(),
                                    ongoingSceneCall,
                                    executionEnvironment);
  }
  
  private static <T, R> Consumer<Context> toContextConsumerFromAct(Function<Context, T> inputVariableResolver,
                                                                   Act<T, R> act,
                                                                   String outputVariableName,
                                                                   SceneCall ongoingSceneCall,
                                                                   ExecutionEnvironment executionEnvironment) {
    return c -> {
      LOGGER.debug("ENTERING: {}:{}", ongoingSceneCall.targetScene().name(), act.name());
      try {
        var v = act.perform(inputVariableResolver.apply(c), executionEnvironment);
        ongoingSceneCall.workingVariableStore(c).put(outputVariableName, v);
      } catch (Error | RuntimeException e) {
        LOGGER.error(e.getMessage());
        LOGGER.debug(e.getMessage(), e);
        throw e;
      } finally {
        LOGGER.debug("LEAVING:  {}:{}", ongoingSceneCall.targetScene().name(), act.name());
      }
    };
  }
  
  static ActionComposer createActionComposer(final ExecutionEnvironment executionEnvironment) {
    return new ActionComposer() {
      SceneCall ongoingSceneCall = null;
      
      @Override
      public SceneCall ongoingSceneCall() {
        return requireNonNull(ongoingSceneCall);
      }
      
      @Override
      public ExecutionEnvironment executionEnvironment() {
        return executionEnvironment;
      }
      
      @Override
      public Action create(SceneCall sceneCall, SceneCall.ResolverBundle resolverMap) {
        var before = this.ongoingSceneCall;
        try {
          this.ongoingSceneCall = sceneCall;
          return ActionComposer.super.create(sceneCall, resolverMap);
        } finally {
          this.ongoingSceneCall = before;
        }
      }
    };
  }
}
