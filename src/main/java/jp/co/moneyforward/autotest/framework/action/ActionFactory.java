package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.*;

public interface ActionFactory<T, R> {
  
  default Optional<String> name() {
    return Optional.empty();
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
            ActionFactory.this.toAction(actionComposer, inputFieldName, outputFieldName),
            ((Act<R, R>) (value, executionEnvironment) -> {
              Expectations.assertStatement(assertion.apply(value));
              return value;
            }).toAction(actionComposer, outputFieldName, outputFieldName));
      }
      
      @Override
      public Optional<String> name() {
        return ActionFactory.this.name();
      }
    };
  }
}
