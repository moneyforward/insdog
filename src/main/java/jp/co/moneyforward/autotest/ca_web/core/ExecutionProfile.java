package jp.co.moneyforward.autotest.ca_web.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.date;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.today;

public interface ExecutionProfile {
  static ExecutionProfile create() {
    return new ExecutionProfileImpl();
  }
  
  /**
   * Creates a browser context object from the browser `b`.
   *
   * @param b                A browser.
   * @param executionProfile An execution profile.
   * @return A browser context object.
   */
  static BrowserContext browserContextFrom(Browser b, ExecutionProfile executionProfile) {
    BrowserContext c;
    if (today().after(date(executionProfile.plannedDateForSettingUpSelfhostedGitHubActions()))) {
      c = b.newContext(new Browser.NewContextOptions()
                           .setLocale(executionProfile.locale()));
    } else {
      // Base64 encode the credentials
      String username = "money-book";
      String password = "asdfasdf";
      String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
      
      // Set up the context with the BASIC Auth headers
      Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
      Map<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Basic " + basicAuth);
      contextOptions.setExtraHTTPHeaders(headers)
                    .setLocale(executionProfile.locale());
      c = b.newContext(contextOptions);
    }
    return c;
  }
  
  String plannedDateForSettingUpSelfhostedGitHubActions();
  
  String locale();
  
  String userEmail();
  
  String userPassword();
  
  String accountServiceId();
  
  String accountServicePassword();
  
  String homeUrl();
  
  String accountsUrl();
  
  boolean setHeadless();
  
  String domain();
}
