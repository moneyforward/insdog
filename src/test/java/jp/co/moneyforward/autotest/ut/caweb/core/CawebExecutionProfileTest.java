package jp.co.moneyforward.autotest.ut.caweb.core;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.github.dakusui.osynth.ObjectSynthesizer;
import jp.co.moneyforward.autotest.ca_web.core.CawebExecutionProfile;
import jp.co.moneyforward.autotest.framework.core.AutotestException;
import jp.co.moneyforward.autotest.framework.core.ExecutionProfile;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Map;

import static com.github.dakusui.osynth.ObjectSynthesizer.methodCall;
import static com.github.valid8j.fluent.Expectations.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CawebExecutionProfileTest {
  @Test
  void givenCurrent_whenCreateExecutionProfile_thenNonNull() {
    var out = createCawebExecutionProfile();
    
    assertStatement(value(out).toBe().notNull());
  }
  
  @Test
  void givenNullForProfileOverriders_whenCreateExecutionProfile_thenPlainExecutionProfileReturned() {
    var plainProfile = createCawebExecutionProfile();
    
    var executionProfile = ExecutionProfile.create(plainProfile, null, CawebExecutionProfile.class);
    
    assertStatement(value(executionProfile).toBe().equalTo(plainProfile));
  }
  
  @Test
  void givenNonEmptyProfileOverriders_whenCreateExecutionProfile_thenSynthesizedExecutionProfileReturned() {
    var plainProfile = createCawebExecutionProfile();
    
    var executionProfile = ExecutionProfile.create(plainProfile, Map.of("accountServiceId", "HELLO_ACCOUNT_SERVICE"), CawebExecutionProfile.class);
    
    assertAll(value(executionProfile).toBe()
                                     .not(v -> v.equalTo(plainProfile)),
              value(executionProfile).invoke("accountServiceId")
                                     .toBe()
                                     .equalTo("HELLO_ACCOUNT_SERVICE"));
  }
  
  @Test
  void whenTotpForNextTimeStep_thenDifferentTotpFromCurrentOne() {
    CawebExecutionProfile executionProfile = createCawebExecutionProfile();
    String totpForNow = executionProfile.totpForNow();
    
    String nextTotp = executionProfile.nextTotp();
    
    assertStatement(value(nextTotp).toBe().not(s -> s.equalTo(totpForNow)));
  }
  
  @Test
  void whenGeneratingTotpResultingInInvalidKeyException_thenWrapped() throws InvalidKeyException {
    TimeBasedOneTimePasswordGenerator totp = mock(TimeBasedOneTimePasswordGenerator.class);
    when(totp.generateOneTimePasswordString(any(), any())).thenThrow(InvalidKeyException.class);
    CawebExecutionProfile executionProfile = new ObjectSynthesizer().addInterface(CawebExecutionProfile.class)
                                                                    .handle(methodCall("totp").with((self, args) -> totp))
                                                                    .synthesize(createCawebExecutionProfile())
                                                                    .castTo(CawebExecutionProfile.class);
    
    assertThrows(AutotestException.class, executionProfile::totpForNow);
  }
  
  @Test
  void whenTotpKey_thenValidKeyReturned() {
    Key key = createCawebExecutionProfile().totpKey();
    
    assertAll(value(key).toBe().instanceOf(SecretKey.class));
    assertAll(value(key.getAlgorithm()).toBe().equalTo("HmacSHA1"));
    assertAll(value(key.getFormat()).toBe().nullValue());
    assertAll(value(key.getEncoded()).toBe().instanceOf(byte[].class));
  }
  
  /**
   * Creates an `ExecutionProfile` object.
   *
   * @return An `ExecutionProfile` object.
   */
  private static CawebExecutionProfile createCawebExecutionProfile() {
    return ExecutionProfile.create(CawebExecutionProfile.class);
  }
}
