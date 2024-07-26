package jp.co.moneyforward.autotest.ca_web.core;

import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.Optional;
import java.util.function.Supplier;

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
    return create(() -> InternalUtils.currentBranchNameFor(InternalUtils.projectDir()));
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
  
  /**
   * Returns a "home" url of the application, from which a test starts at the beginning (login).
   *
   * The returned value will be like: `https:/{domain}/`
   *
   * @return a "home" url of the application.
   * @see ExecutionProfile#domain()
   */
  String homeUrl();
  
  /**
   * Returns the anticipated date, where "self-hosted" GitHub Actions is provided.
   *
   * @return The date, where the "self-hosted" GitHub Actions becomes available.
   */
  String plannedDateForSettingUpSelfhostedGitHubActions();
  
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
  String accountsUrl();
  
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
  
  private static String composeIdevDomainName(String branchName) {
    return String.format("ca-web-%s.idev.test.musubu.co.in",
                         branchName.substring(branchName.indexOf('@') + 1));
  }
}
