package jp.co.moneyforward.autotest.ut.caweb.core;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class CawebAccessingModelTest {
  @Test
  void givenHeadlessExecutionProfile_whenLaunchBrowser_thenHeadless() {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = CawebAccessingModel.launchBrowser(playwright.chromium(),
                                                               createExecutionProfile(true))) {
        assertStatement(value(browser).function(isHeadless())
                                      .asBoolean()
                                      .toBe()
                                      .trueValue());
      }
    }
  }
  
  private static Function<Browser, Boolean> isHeadless() {
    return Printables.function("isHeadless", CawebAccessingModelTest::isHeadless);
  }
  
  private static boolean isHeadless(Browser browser) {
    // Create a new page
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    
    // Evaluate JavaScript to check if headless
    boolean isHeadless = (boolean) page.evaluate("() => navigator.userAgent.includes('Headless')");
    
    // Close the context
    context.close();
    
    return isHeadless;
  }
  
  private static ExecutionProfile createExecutionProfile(final boolean headless) {
    return new ExecutionProfile() {
      
      @Override
      public String homeUrl() {
        return "";
      }
      
      @Override
      public String locale() {
        return "";
      }
      
      @Override
      public String userEmail() {
        return "";
      }
      
      @Override
      public String userPassword() {
        return "";
      }
      
      @Override
      public String accountServiceId() {
        return "";
      }
      
      @Override
      public String accountServicePassword() {
        return "";
      }
      
      @Override
      public String accountsUrl() {
        return "";
      }
      
      @Override
      public boolean setHeadless() {
        return headless;
      }
      
      @Override
      public String domain() {
        return "";
      }
      
      @Override
      public String userDisplayName() {
        return "";
      }
      
      @Override
      public String officeName() {
        return "";
      }
    };
  }
}
