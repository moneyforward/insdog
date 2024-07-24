package jp.co.moneyforward.autotest.ca_web.core;

import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

class ExecutionProfileImpl implements ExecutionProfile {
  
  @Override
  public String homeUrl() {
    return String.format("https://%s/", domain());
  }
  
  @Override
  public String userEmail() {
    return "ukai.hiroshi+autotest1@moneyforward.co.jp";
  }
  
  @Override
  public String userPassword() {
    return "MASK!!QAZ@WSX";
  }
  
  @Override
  public String accountServiceId() {
    return "MASK!WgeiXfUgHsPn90t5kQtS";
  }
  
  @Override
  public String accountServicePassword() {
    return "MASK!eQCZmxlS1DlmB8Moe710";
  }
  
  @Override
  public String accountsUrl() {
    return String.format("https://%s/accounts", domain());
  }
  
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
    return InternalUtils.isPresumablyRunningFromIDE();
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
  
  @Override
  public String domain() {
    return "accounting-stg1.ebisubook.com";
  }
}
