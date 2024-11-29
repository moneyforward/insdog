package jp.co.moneyforward.autotest.ut.framework.core;

import jp.co.moneyforward.autotest.framework.core.ExecutionProfile;
import org.junit.jupiter.api.Test;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class ExecutionProfileTest {
  
  @Test
  void whenExecutionProfile_when_then() {
    ExecutionProfile executionProfile = ExecutionProfile.create(ExecutionProfileExample.class);
    
    assertStatement(value(executionProfile).toBe().notNull());
  }
}
