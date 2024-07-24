package jp.co.moneyforward.autotest.ca_web.core;

public interface ExecutionProfile {
  static ExecutionProfile create() {
    return new ExecutionProfileImpl();
  }
  
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
  
  String userEmail();
  
  String userPassword();
  
  String accountServiceId();
  
  String accountServicePassword();
  
  String homeUrl();
  
  String accountsUrl();
  
  /**
   * Returns if **autotest** should be executed in headless or head-ful.
   * The head-ful is useful for developing and debugging the **autotest** not intended for using it in the C/I environment.
   *
   * @return `true` - headless (default) / `false` - head-ful mode.
   */
  boolean setHeadless();
  
  String domain();
}
