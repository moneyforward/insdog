package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.fluent.Expectations;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.util.Collections.singletonList;

public class AssertionAct<T, R> implements Act<T, R> {
  private final List<Function<R, Statement<R>>> assertions;
  private final String name;
  private final LeafAct<T, R> parent;
  
  public AssertionAct(LeafAct<T, R> parent, String name, Function<R, Statement<R>> assertion) {
    this(parent, name, singletonList(assertion));
  }
  
  public AssertionAct(LeafAct<T, R> parent, String name, List<Function<R, Statement<R>>> assertion) {
    this.parent = parent;
    this.assertions = requireNonNull(assertion);
    this.name = requireNonNull(name);
  }
  
  public LeafAct<T, R> parent() {
    return this.parent;
  }
  
  public List<Function<R, Statement<R>>> assertions() {
    return this.assertions;
  }
  
  
  @Override
  public AssertionAct<T, R> assertion(Function<R, Statement<R>> assertion) {
    return new AssertionAct<>(this.parent(),
                              this.name(),
                              Stream.concat(
                                  this.assertions().stream(),
                                  Stream.of(assertion)).toList());
  }
  
  @Override
  public String name() {
    return this.name;
  }
  
  private static <R> LeafAct<R, R> toLeafAct(Function<R, Statement<R>> assertion) {
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
}
