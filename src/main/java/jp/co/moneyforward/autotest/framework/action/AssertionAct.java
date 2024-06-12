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
import static com.github.valid8j.classic.Requires.requireNonNull;

public class AssertionAct<T, R> implements Act<T, R> {
  private final Function<R, Statement<R>> assertion;
  private final String name;
  private final LeafAct<T, R> parent;
  
  public AssertionAct(LeafAct<T, R> parent, String name, Function<R, Statement<R>> assertion) {
    this.parent = parent;
    this.assertion = requireNonNull(assertion);
    this.name = requireNonNull(name);
  }
  
  public LeafAct<T, R> parent() {
    return this.parent;
  }
  
  public Function<R, Statement<R>> assertion() {
    return this.assertion;
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return sequential(
        Stream.concat(
            toListIfSequential(parent.toAction(actionComposer, inputFieldName, outputFieldName)).stream(),
            Stream.of(assertionAct(assertion).toAction(actionComposer, outputFieldName, outputFieldName))).toList());
  }
  
  private List<Action> toListIfSequential(Action action) {
    if (action instanceof Composite && !((Composite) action).isParallel())
      return ((Composite) action).children();
    return List.of(action);
  }
  
  private static <R> LeafAct<R, R> assertionAct(Function<R, Statement<R>> assertion) {
    return new LeafAct<>() {
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
    return this.name;
  }
}
