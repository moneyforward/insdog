package jp.co.moneyforward.autotest.ut.caweb.core;

import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class ExecutionProfileTest {
  @Test
  void givenCurrent_whenCreateExecutionProfile_thenNonNull() {
    var out = ExecutionProfile.create();
    
    assertStatement(value(out).toBe().notNull());
  }
  
  @Test
  void givenBranchNameWithoutAtMark_whenCreateExecutionProfile_thenStagingReturningOnDomainName() {
    var out = ExecutionProfile.create(() -> Optional.of("hello-world"));
    
    assertStatement(value(out).function(ExecutionProfile::domain)
                              .asString()
                              .toBe()
                              .containing("accounting-stg1"));
  }
  
  @Test
  void givenBranchNameWithAtMark_whenCreateExecutionProfile_thenIdevReturningOnDomainName() {
    var out = ExecutionProfile.create(() -> Optional.of("hello@world"));
    
    assertStatement(value(out).function(ExecutionProfile::domain)
                              .asString()
                              .toBe()
                              .equalTo(String.format("ca-web-%s.idev.test.musubu.co.in", "world")));
  }
}
