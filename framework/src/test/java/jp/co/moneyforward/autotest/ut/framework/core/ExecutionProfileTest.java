package jp.co.moneyforward.autotest.ut.framework.core;

import jp.co.moneyforward.autotest.framework.core.ExecutionProfile;
import org.junit.jupiter.api.Test;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class ExecutionProfileTest {
  @ExecutionProfile.CreateWith(ExecutionProfileExample.Factory.class)
  public static class ExecutionProfileExample implements ExecutionProfile {
    public static class Factory implements ExecutionProfile.Factory<ExecutionProfileExample> {
      @Override
      public ExecutionProfileExample create(String branchName) {
        return new ExecutionProfileExample();
      }
    }
  }
  
  @Test
  void whenExecutionProfile_when_then() {
    ExecutionProfile executionProfile = ExecutionProfile.create(ExecutionProfileExample.class);
    
    assertStatement(value(executionProfile).toBe().notNull());
  }
}
