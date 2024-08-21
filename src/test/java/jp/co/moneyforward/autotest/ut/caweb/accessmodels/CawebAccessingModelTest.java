package jp.co.moneyforward.autotest.ut.caweb.accessmodels;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.github.dakusui.actionunit.visitors.ActionPerformer;
import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.ututils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.security.Key;
import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel.executionProfile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Add the following VM option, when you run this test.
 *
 * `--add-opens java.base/java.lang.invoke=ALL-UNNAMED`
 *
 * @see CawebAccessingModel
 */
class CawebAccessingModelTest {
  @Test
  void whenOpen_nonEmptySceneReturned() {
    Scene scene = CawebAccessingModel.open();
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).toBe().notEmpty());
  }
  
  @Test
  void whenLogin_nonEmptySceneReturned() {
    Scene scene = CawebAccessingModel.login();
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).toBe().notEmpty());
  }
  
  @Test
  void whenLogout_nonEmptySceneReturned() {
    Scene scene = CawebAccessingModel.logout();
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).toBe().notEmpty());
  }
  
  @Test
  void whenScreenshot_nonEmptySceneReturned() {
    Scene scene = CawebAccessingModel.screenshot();
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).toBe().notEmpty());
  }
  
  @Test
  void whenBrowserContextFrom_nonEmptySceneReturned() {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = TestUtils.launchHeadlessBrowser(playwright.chromium())) {
        BrowserContext browserContext = CawebAccessingModel.browserContextFrom(browser, executionProfile());
        
        assertStatement(value(browserContext).toBe().notNull());
      }
    }
  }
  
  @Test
  void whenActionPerformer_thenNonNull() {
    ActionPerformer actionPerformer = new CawebAccessingModel().actionPerformer();
    
    assertAll(value(actionPerformer).toBe().notNull());
  }
  
  
  @Test
  void whenClose_nonEmptySceneReturned() {
    Scene scene = CawebAccessingModel.close();
    
    assertAll(value(scene).toBe().notNull(),
              value(scene.children()).toBe().notEmpty());
  }
  
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
  
  @Test
  void whenGetContext_thenContextReturningItselfOnCreateChildReturned() {
    var context = CawebAccessingModel.getContext();
    
    assertAll(value(context).toBe().notNull(),
              value(context).invoke("createChild").toBe().equalTo(context));
  }
  
  @Test
  void givenHeadless_whenLaunchBrowser_thenBrowserReturned() {
    whenLaunchBrowser_thenBrowserReturned(true);
  }
  
  @Test
  void givenHeadful_whenLaunchBrowser_thenBrowserReturned() {
    whenLaunchBrowser_thenBrowserReturned(false);
  }
  
  private static void whenLaunchBrowser_thenBrowserReturned(boolean intendedHeadlessValue) {
    var browserType = mock(BrowserType.class);
    ArgumentCaptor<LaunchOptions> argumentCaptor = ArgumentCaptor.forClass(LaunchOptions.class);
    try (Browser mockedBrowser = mock(Browser.class)) {
      when(browserType.launch(any())).thenReturn(mockedBrowser);
      
      var b = CawebAccessingModel.launchBrowser(browserType, createExecutionProfile(intendedHeadlessValue));
      try (var argCaptor = verify(browserType).launch(argumentCaptor.capture())) {
        
        assertAll(value(b).toBe().equalTo(mockedBrowser),
                  value(argumentCaptor.getValue().headless).asBoolean().toBe().equalTo(intendedHeadlessValue));
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
      public Key totpKey() {
        return null;
      }
      
      @Override
      public String totpKeyString() {
        return "123456";
      }
      
      @Override
      public TimeBasedOneTimePasswordGenerator totp() {
        return null;
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
