package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class AssertionCall<R> implements Call {
  private final List<Function<R, Statement<R>>> assertions;
  private final Call target;
  
  public AssertionCall(Call target, List<Function<R, Statement<R>>> assertion) {
    this.target = target;
    this.assertions = requireNonNull(assertion);
  }
  
  List<ActCall<R, R>> assertionAsLeafActCalls() {
    return assertions.stream()
                     .map(assertion -> new ActCall<>(outputFieldName(), assertionAsLeafAct(assertion), outputFieldName()))
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
  
  Call target() {
    return this.target;
  }
  
  @Override
  public String outputFieldName() {
    return this.target().outputFieldName();
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this, assignmentResolversFromCurrentCall);
  }
}
