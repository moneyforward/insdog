package jp.co.moneyforward.autotest.ca_web.core;

/**
 * A class that holds information, which doesn't change throughout an execution session of "autotest-ca"
 */
class ExecutionProfileImpl implements ExecutionProfile {
  
  /**
   * Returns a "home" url of the application, from which a test starts at the beginning (login).
   *
   * @return a "home" url of the application.
   */
  @Override
  public String homeUrl() {
    return String.format("https://%s/", domain());
  }
  
  /**
   * Returns an account with which the **autotest-ca** logs in to the application.
   *
   * @return A user email for an account used in the test.
   */
  public String userEmail() {
    return "ukai.hiroshi+autotest1@moneyforward.co.jp";
  }
  
  /**
   * A password for an account specified by the returned value of `userEmail()` method.
   *
   * @return A password for `userEmail()`.
   * @see ExecutionProfileImpl#userEmail()
   */
  @Override
  public String userPassword() {
    return "MASK!!QAZ@WSX";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_id1`.
   *
   * @return An ID for the "account service".
   */
  @Override
  public String accountServiceId() {
    return "MASK!WgeiXfUgHsPn90t5kQtS";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_pw1`.
   *
   * @return A password for the "account service".
   */
  @Override
  public String accountServicePassword() {
    return "MASK!eQCZmxlS1DlmB8Moe710";
  }
  
  /*
    # comment1,comment2,comment3,*action,*what,attribute,matcher,value
    #,直叩き用URL定義,,,,,,
      ,,,store,ca_accounts_service_list_url,,,https://#{domain}/accounts/service_list
      ,,,store,ca_accounts_group_url,,,https://#{domain}/accounts/group
      ,,,store,ca_accounts_url,,,https://#{domain}/accounts
   */
  @Override
  public String accountsUrl() {
    return String.format("https://%s/accounts", domain());
  }
  
  /**
   * Returns the anticipated date, where "self-hosted" GitHub Actions is provided.
   *
   * @return The date, where the "self-hosted" GitHub Actions becomes available.
   */
  @Override
  public String plannedDateForSettingUpSelfhostedGitHubActions() {
    return "Jul/10/2024";
  }
  
  /**
   * Returns if **autotest** should be executed in headless or head-ful.
   * The head-ful is useful for developing and debugging the **autotest** not intended for using it in the C/I environment.
   *
   * @return `true` - headless (default) / `false` - head-ful mode.
   */
  @Override
  public boolean setHeadless() {
    return true;
  }
  
  
  /**
   * Returns a locale to open a browser for the execution of **autotest**.
   * I.e., the value will be passed to `ContextOptions#setLocale` of **Playwright-java**.
   *
   * Currently, this always returns `ja-JP`.
   *
   * @return The locale, in which the tests should be executed.
   */
  @Override
  public String locale() {
    return "ja-JP";
  }
  
  /**
   * Returns a domain against which tests are conducted.
   *
   * @return The domain against which tests are conducted.
   */
  @Override
  public String domain() {
    return "accounting-stg1.ebisubook.com";
  }
}
