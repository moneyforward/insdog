package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class AssertionActCall<T, R> extends ActCall<T, R> {
  private final List<Function<R, Statement<R>>> assertions;
  private final ActCall<T, R> target;
  
  public AssertionActCall(ActCall<T, R> target, List<Function<R, Statement<R>>> assertion) {
    super(target.inputFieldName(), target.outputFieldName());
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
