package jp.co.moneyforward.autotest.framework.action;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

/**
 * An interface to decorate a behavior of a given call.
 *
 * @param <C> Type of call object whose behavior is to be modified by an implementation of this interface.
 */
public sealed interface CallDecorator<C extends Call> extends Call permits CallDecorator.Base {
  /**
   * An instance of a call whose behavior is to be decorated by this object.
   *
   * @return An instance of a call,
   */
  C targetCall();
  
  /**
   * A base class for `CallDecorator`.
   *
   * @param <C> A call class to be decorated by this class.
   */
  abstract sealed class Base<C extends Call> implements CallDecorator<C> permits AssertionCall, RetryCall, AltCall {
    private final C target;
    
    /**
     * Creates an object of this class.
     *
     * @param target A target class to be decorated.
     */
    protected Base(C target) {
      this.target = requireNonNull(target);
    }
    
    @Override
    public C targetCall() {
      return this.target;
    }
  }
}
