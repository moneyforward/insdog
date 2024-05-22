package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;

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
    return new ActionFactory<T, R>() {
      @Override
      public Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
        AtomicReference<R> output = new AtomicReference<>();
        return sequential(
            nop(), // TODO
            leaf(context -> Expectations.assertStatement(assertion.apply(output.get()))));
      }
      
      
      @Override
      public Optional<String> name() {
        return ActionFactory.this.name();
      }
    };
  }
}
