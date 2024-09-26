package jp.co.moneyforward.autotest.framework.action;

import java.util.List;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

public sealed interface TargetedCall extends Call permits TargetedCall.Base, AssertionCall, RetryCall {
  abstract sealed class Base implements TargetedCall permits AssertionCall, RetryCall {
    private final Call target;
    
    protected Base(Call target) {
      this.target = requireNonNull(target);
    }
    
    @Override
    public List<String> requiredVariableNames() {
      return this.target().requiredVariableNames();
    }
    
    @Override
    public String outputVariableName() {
      return this.target().outputVariableName();
    }
    
    Call target() {
      return this.target;
    }
  }
}
