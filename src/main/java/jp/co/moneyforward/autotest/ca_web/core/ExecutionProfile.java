package jp.co.moneyforward.autotest.ca_web.core;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.github.dakusui.osynth.ObjectSynthesizer;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.github.dakusui.osynth.ObjectSynthesizer.methodCall;
import static jp.co.moneyforward.autotest.framework.cli.CliUtils.getProfileOverriders;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.wrap;

/**
 * A class that holds information, which doesn't change throughout an execution session of "autotest-ca"
 */
public interface ExecutionProfile {
  /**
   * Creates an `ExecutionProfile` object.
   *
   * @return An `ExecutionProfile` object.
   */
  static ExecutionProfile create() {
    return create(create(() -> InternalUtils.currentBranchNameFor(InternalUtils.projectDir())), getProfileOverriders());
  }
  
  /**
   * Creates an `ExecutionProfile` object.
   *
   * @param branchNameSupplier A supplier that gives a branch name.
   * @return An `ExecutionProfile` object.
   */
  static ExecutionProfile create(Supplier<Optional<String>> branchNameSupplier) {
    Optional<String> branchName = branchNameSupplier.get();
    return branchName.filter(v -> v.contains("@"))
                     .isPresent() ? new ExecutionProfileImpl(composeIdevDomainName(branchName.get()))
                                  : new ExecutionProfileImpl("accounting-stg1.ebisubook.com");
  }
  
  static ExecutionProfile create(ExecutionProfile base, Map<String, String> profileOverriders) {
    if (profileOverriders == null) {
      return base;
    }
    ObjectSynthesizer objectSynthesizer = new ObjectSynthesizer();
    for (Map.Entry<String, String> overrider : profileOverriders.entrySet())
      objectSynthesizer.handle(methodCall(overrider.getKey()).with((self, args) -> profileOverriders.get(overrider.getKey())));
    
    return objectSynthesizer.addInterface(ExecutionProfile.class)
                            .synthesize(base)
                            .castTo(ExecutionProfile.class);
  }
  
  /**
   * Returns a "home" url of the application, from which a test starts at the beginning (login).
   *
   * The returned value will be like: `https:/{domain}/`
   *
   * @return a "home" url of the application.
   * @see ExecutionProfile#domain()
   */
  default String homeUrl() {
    return String.format("https://%s/", domain());
  }
  
  /**
   * Returns a locale to open a browser for the execution of **autotest**.
   * I.e., the value will be passed to `ContextOptions#setLocale` of **Playwright-java**.
   *
   * Currently, this always returns `ja-JP`.
   *
   * @return The locale, in which the tests should be executed.
   */
  String locale();
  
  /**
   * Returns an account with which the **autotest-ca** logs in to the application.
   *
   * @return A user email for an account used in the test.
   */
  String userEmail();
  
  /**
   * A password for an account specified by the returned value of `userEmail()` method.
   *
   * @return A password for `userEmail()`.
   * @see ExecutionProfileImpl#userEmail()
   */
  String userPassword();
  
  /**
   * A method that returns a TOTP (Time-based Onetime password) for current time step.
   * Time of the calling this method.
   *
   * @return A TOTP for the current time.
   * @see ExecutionProfile#totpFor(Instant)
   */
  default String totpForNow() {
    return totpFor(Instant.now());
  }
  
  /**
   * A method that returns a TOTP for the next time step from the one for now.
   *
   * @return A TOTP for the next time step after the current time.
   * @see ExecutionProfile#totpFor(Instant)
   */
  default String nextTotp() {
    return totpFor(Instant.now().plus(totp().getTimeStep()));
  }
  
  /**
   * Returns a TOTP (Time-based Onetime password) string for the given `instant`.
   *
   * @param instant An instant for which a TOTP is generated.
   * @return A TOTP string.
   */
  default String totpFor(Instant instant) {
    try {
      return totp().generateOneTimePasswordString(totpKey(), instant);
    } catch (InvalidKeyException e) {
      throw wrap(e);
    }
  }
  
  /**
   * Returns a key object from a returned value of `totpKeyString`.
   *
   * @return A secret key instance fot TOTP.
   * @see ExecutionProfile#totpKeyString
   */
  default Key totpKey() {
    return new SecretKey() {
      
      @Override
      public String getAlgorithm() {
        return totp().getAlgorithm();
      }
      
      @Override
      public String getFormat() {
        return null;
      }
      
      @Override
      public byte[] getEncoded() {
        return new Base32().decode(totpKeyString());
      }
    };
  }
  
  /**
   * A key string shared with the account.
   * The value is what is defined as "Key" in "TOTP: Time-Based One-Time Password Algorithm"[RFC-6238](https://datatracker.ietf.org/doc/html/rfc6238).
   *
   * @return A key string to generate a onetime password.
   */
  String totpKeyString();
  
  /**
   * A method that returns A TOTP generator object.
   *
   * @return A TOTP generator object.
   */
  TimeBasedOneTimePasswordGenerator totp();
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_id1`.
   *
   * @return An ID for the "account service".
   */
  String accountServiceId();
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_pw1`.
   *
   * @return A password for the "account service".
   */
  String accountServicePassword();
  
  /*
    # comment1,comment2,comment3,*action,*what,attribute,matcher,value
    #,直叩き用URL定義,,,,,,
      ,,,store,ca_accounts_service_list_url,,,https://#{domain}/accounts/service_list
      ,,,store,ca_accounts_group_url,,,https://#{domain}/accounts/group
      ,,,store,ca_accounts_url,,,https://#{domain}/accounts
   */
  default String accountsUrl() {
    return String.format("https://%s/accounts", domain());
  }
  
  /**
   * Returns if **autotest** should be executed in headless or head-ful.
   * The head-ful is useful for developing and debugging the **autotest** not intended for using it in the C/I environment.
   *
   * @return `true` - headless (default) / `false` - head-ful mode.
   */
  boolean setHeadless();
  
  /**
   * Returns a domain against which tests are conducted.
   *
   * @return The domain against which tests are conducted.
   */
  String domain();
  
  String userDisplayName();
  
  String officeName();
  
  private static String composeIdevDomainName(String branchName) {
    return String.format("ca-web-%s.idev.test.musubu.co.in",
                         branchName.substring(branchName.indexOf('@') + 1));
  }
}
