package jp.co.moneyforward.autotest.ut.caweb.core;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.github.dakusui.osynth.ObjectSynthesizer;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.core.AutotestException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Map;
import java.util.Optional;

import static com.github.dakusui.osynth.ObjectSynthesizer.methodCall;
import static com.github.valid8j.fluent.Expectations.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
  
  @Test
  void givenNullForProfileOverriders_whenCreateExecutionProfile_thenPlainExecutionProfileReturned() {
    var plainProfile = ExecutionProfile.create();
    
    var executionProfile = ExecutionProfile.create(plainProfile, null);
    
    assertStatement(value(executionProfile).toBe().equalTo(plainProfile));
  }
  
  @Test
  void givenNonEmptyProfileOverriders_whenCreateExecutionProfile_thenSynthesizedExecutionProfileReturned() {
    var plainProfile = ExecutionProfile.create();
    
    var executionProfile = ExecutionProfile.create(plainProfile, Map.of("accountServiceId", "HELLO_ACCOUNT_SERVICE"));
    
    assertAll(value(executionProfile).toBe()
                                     .not(v -> v.equalTo(plainProfile)),
              value(executionProfile).invoke("accountServiceId")
                                     .toBe()
                                     .equalTo("HELLO_ACCOUNT_SERVICE"));
  }
  
  @Test
  void whenTotpForNextTimeStep_thenDifferentTotpFromCurrentOne() {
    ExecutionProfile executionProfile = ExecutionProfile.create();
    String totpForNow = executionProfile.totpForNow();
    
    String nextTotp = executionProfile.nextTotp();
    
    assertStatement(value(nextTotp).toBe().not(s -> s.equalTo(totpForNow)));
  }
  
  @Test
  void whenGeneratingTotpResultingInInvalidKeyException_thenWrapped() throws InvalidKeyException {
    TimeBasedOneTimePasswordGenerator totp = mock(TimeBasedOneTimePasswordGenerator.class);
    when(totp.generateOneTimePasswordString(any(), any())).thenThrow(InvalidKeyException.class);
    ExecutionProfile executionProfile = new ObjectSynthesizer().addInterface(ExecutionProfile.class)
                                                               .handle(methodCall("totp").with((self, args) -> totp))
                                                               .synthesize(ExecutionProfile.create())
                                                               .castTo(ExecutionProfile.class);
    
    assertThrows(AutotestException.class, executionProfile::totpForNow);
  }
  
  @Test
  void whenTotpKey_thenValidKeyReturned() {
    Key key = ExecutionProfile.create().totpKey();
    
    assertAll(value(key).toBe().instanceOf(SecretKey.class));
    assertAll(value(key.getAlgorithm()).toBe().equalTo("HmacSHA1"));
    assertAll(value(key.getFormat()).toBe().nullValue());
    assertAll(value(key.getEncoded()).toBe().instanceOf(byte[].class));
  }
}
