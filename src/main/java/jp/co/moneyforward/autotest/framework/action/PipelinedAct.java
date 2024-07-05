package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.valid8j.classic.Requires.requireNonNull;

public interface PipelinedAct<T, M, R> extends Act<T, R> {
  LeafAct<T, M> head();
  
  Act<M, R> tail();
  
  default Stream<LeafAct<?, ?>> tails() {
    return Stream.empty();
  }
  static <T, M, R> PipelinedAct<T, M, R> create(LeafAct<T, M> head, Act<M, R> tail) {
    requireNonNull(head);
    requireNonNull(tail);
    return new PipelinedAct<>() {
      @Override
      public LeafAct<T, M> head() {
        return head;
      }
      
      @Override
      public Act<M, R> tail() {
        return tail;
      }
      
      @Override
      public AssertionAct<T, R> assertion(Function<R, Statement<R>> assertion) {
        return new AssertionAct<>(this, head().name() + "->" + tail().name(), assertion);
      }
    };
  }
}
