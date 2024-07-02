package jp.co.moneyforward.autotest.ca_web.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.date;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.today;

/**
 * A class that holds information, which doesn't change throughout an execution session of "autotest-ca"
 */
public class ExecutionProfile {
  public static BrowserContext browserContextFrom(Browser b, ExecutionProfile executionProfile) {
    BrowserContext c;
    if (today().after(date(executionProfile.plannedDateForSettingUpSelfhostedGitHubActions()))) {
      c = b.newContext();
    } else {
      // Base64 encode the credentials
      String username = "money-book";
      String password = "asdfasdf";
      String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      
      // Set up the context with the BASIC Auth headers
      Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Basic " + basicAuth);
      contextOptions.setExtraHTTPHeaders(headers);
      
      c = b.newContext(contextOptions);
    }
    return c;
  }
  
  /**
   * Returns a "home" url of the application, from which a test starts at the beginning (login).
   * @return a "home" url of the application.
   */
  public String homeUrl() {
    return String.format("https://%s/", domain());
  }
  
  /**
   * Returns an account with which the **autotest-ca** logs in to the application.
   * @return A user email for an account used in the test.
   */
  public String userEmail() {
    return "ukai.hiroshi+autotest1@moneyforward.co.jp";
  }
  
  /**
   * A password for an account specified by the returned value of `userEmail()` method.
   *
   * @return A password for `userEmail()`.
   * @see ExecutionProfile#userEmail()
   */
  public String userPassword() {
    return "MASK!!QAZ@WSX";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_id1`.
   *
   * @return An ID for the "account service".
   */
  public String accountServiceId() {
    return "MASK!WgeiXfUgHsPn90t5kQtS";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_pw1`.
   *
   * @return A password for the "account service".
   */
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
  public String accountsUrl() {
    return String.format("https://%s/accounts", domain());
  }
  
  private String domain() {
    return "accounting-stg1.ebisubook.com";
  }
  
  public String plannedDateForSettingUpSelfhostedGitHubActions() {
    return "Jul/10/2024";
  }
}
