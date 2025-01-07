package jp.co.moneyforward.autotest.ut.framework.utils;

import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import jp.co.moneyforward.autotest.framework.utils.Valid8JCliches;
import org.junit.jupiter.api.Test;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Valid8JClichesTest {
  @Test
  void givenPredicateResultingInFalse_whenAssumeStatement_thenAssumptionViolated() {
    assertThrows(InternalUtils.AssumptionViolation.class,
                 () -> Valid8JCliches.assumeStatement(Expectations.value(true).toBe().falseValue()));
  }
  
  @Test
  void givenPredicateResultingInTrue_whenAssumeStatement_thenNothingHappens() {
    boolean trueValue = true;
    Valid8JCliches.assumeStatement(Expectations.value(trueValue).toBe().trueValue());
    // An assertion just to suppress warning from SonarQube
    assertStatement(Expectations.value(true).toBe().trueValue());
  }
}
