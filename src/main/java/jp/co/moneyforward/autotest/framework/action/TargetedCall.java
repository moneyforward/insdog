package jp.co.moneyforward.autotest.framework.action;

import java.util.List;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

public sealed interface TargetedCall<C extends Call> extends Call permits TargetedCall.Base {
  C targetCall();
  
  abstract sealed class Base<C extends Call> implements TargetedCall<C> permits AssertionCall, RetryCall {
    private final C target;
    
    protected Base(C target) {
      this.target = requireNonNull(target);
    }
    
    @Override
    public List<String> requiredVariableNames() {
      return this.targetCall().requiredVariableNames();
    }
    
    
//    @Override
//    public String outputVariableName() {
//      return this.targetCall().outputVariableName();
//    }
    
    @Override
    public C targetCall() {
      return this.target;
    }
  }
}
