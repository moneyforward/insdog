package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * A call for assertions.
 *
 * Both for input and output variable name, this uses `assertion:{target().outputVariableName()}`.
 *
 * @param <R> Type of value to be validated by assertions.
 */
public final class AssertionCall<R> extends CallDecorator.Base<ActCall<?, R>> {
  private final List<Function<R, Statement<R>>> assertions;
  
  /**
   * Creates an object of this class.
   *
   * @param target An act call to be verified by this call.
   * @param assertions A list of functions that return verifying statements for the output `target`.
   */
  public AssertionCall(ActCall<?, R> target, List<Function<R, Statement<R>>> assertions) {
    super(target);
    this.assertions = requireNonNull(assertions);
  }
  
  /**
   * Returns a name of a variable that holds the output of `targetCall()`.
   *
   * @return A name of a variable that holds the output of `targetCall()`.
   */
  public String outputVariableName() {
    return this.targetCall().outputVariableName();
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer) {
    return actionComposer.create(this);
  }
  
  /**
   * Returns a list of act calls, which are converted from assertions represented as a list of `Function<R, Statement<R>>`.
   * Each act call in the list reads the value to be verified from variable specified by `outputVariableName()`.
   *
   * @return A list of act calls.
   */
  public List<ActCall<R, R>> assertionsAsActCalls() {
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
