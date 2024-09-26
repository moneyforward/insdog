package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public final class AssertionCall<R> extends TargetedCall.Base implements TargetedCall {
  private final List<Function<R, Statement<R>>> assertions;
  
  public AssertionCall(Call target, List<Function<R, Statement<R>>> assertions) {
    super(target);
    this.assertions = requireNonNull(assertions);
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this, assignmentResolversFromCurrentCall);
  }
  
  
  List<ActCall<R, R>> assertionAsLeafActCalls() {
    return assertions.stream()
                     .map(assertion -> new ActCall<>(this.outputVariableName(), assertionAsLeafAct(assertion), outputVariableName()))
                     .toList();
  }
  
  private Act<R, R> assertionAsLeafAct(Function<R, Statement<R>> assertion) {
    return new Act<>() {
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
}
