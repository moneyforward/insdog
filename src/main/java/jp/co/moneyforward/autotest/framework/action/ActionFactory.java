package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.actions.Composite;
import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;

public interface ActionFactory<T, R> {
  
  default String name() {
    return this.getClass().getSimpleName();
  }
  
  default Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return actionComposer.create(this, inputFieldName, outputFieldName);
  }
  
  /**
   * @param assertion An assertion to be
   * @return A new ActionFactory object with the given assertion.
   */
  default ActionFactory<T, R> assertion(Function<R, Statement<R>> assertion) {
    return new ActionFactory<>() {
      @Override
      public Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
        return sequential(
            Stream.concat(
                toListIfSequential(ActionFactory.this.toAction(actionComposer, inputFieldName, outputFieldName)).stream(),
                Stream.of(assertionAct(assertion).toAction(actionComposer, outputFieldName, outputFieldName))).toList());
      }
      
      private List<Action> toListIfSequential(Action action) {
        if (action instanceof Composite && !((Composite) action).isParallel())
          return ((Composite) action).children();
        return List.of(action);
      }
      
      private static <R> Act<R, R> assertionAct(Function<R, Statement<R>> assertion) {
        return new Act<>() {
          @Override
          public String name() {
            // This is a hack to compose a human-readable string.
            return "assertion:" + assertion.apply(null).statementPredicate();
          }
          
          @Override
          public R perform(R value, ExecutionEnvironment executionEnvironment) {
            Expectations.assertStatement(assertion.apply(value));
            return value;
          }
        };
      }
      
      @Override
      public String name() {
        return ActionFactory.this.name();
      }
    };
  }
}
